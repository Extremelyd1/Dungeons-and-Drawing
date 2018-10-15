package graphics;

import engine.Transformation;
import engine.entities.Entity;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.lights.SpotLight;
import game.ShaderManager;
import game.map.Map;
import game.map.tile.Tile;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import sun.security.ssl.Debug;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ShadowsManager {
    //
    // Public Methods for rendering Shadows
    //
    public void renderDynamicShadows(Transformation transformation, SceneLight sceneLight, ShaderManager shaderManager, Map map, List<Entity>  entities) {
        if (sceneLight != null)
            renderShadows(transformation, sceneLight, shaderManager, map, entities, true);
    }
    public void renderStaticShadows(Transformation transformation, SceneLight sceneLight, ShaderManager shaderManager, Map map, List<Entity> entities) {
        if (sceneLight != null)
            renderShadows(transformation, sceneLight, shaderManager, map, entities, false);
    }

    //
    // Handle internally
    //
    private void renderShadows(Transformation transformation, SceneLight sceneLight, ShaderManager shaderManager, Map map, List<Entity>  entities, boolean isDynamic) {
        FrustumIntersection frustumIntersection = new FrustumIntersection();
        Matrix4f model;
        int numLights;
        ShadowMap shadowMap;

        if (sceneLight.directionalLight != null && sceneLight.directionalLight.isShadowEnabled()) {
            if (isDynamic) {
                shadowMap = sceneLight.directionalLight.getDynamicShadowMap();
            } else {
                shadowMap = sceneLight.directionalLight.getStaticShadowMap();
            }

            glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
            glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
            glClear(GL_DEPTH_BUFFER_BIT);

            frustumIntersection.set(sceneLight.directionalLight.getLightSpaceMatrix());
            shaderManager.bindDepthMapShader();
            shaderManager.initializeDepthShader(sceneLight.directionalLight.getLightSpaceMatrix());
            if (map != null) {
                for (Tile[] row : map.getTiles()) {
                    for (Tile tile : row) {
                        if (tile == null) {
                            continue;
                        }
                        Vector3f tilePos = new Vector3f(tile.getPosition().x, 0, tile.getPosition().y);
                        int frustrum = frustumIntersection.intersectAab(new Vector3f(tilePos).sub(1.0f, 1.1f, 1.0f), new Vector3f(tilePos).add(0.0f,2.5f, 0.0f));
                        // Calculate the Model matrix in World coordinates
                        if (frustrum == -2 || frustrum == -1) {
                            Mesh mesh = tile.getMesh();
                            if ((isDynamic && !mesh.isStatic()) || (!isDynamic && mesh.isStatic())) {
                                model = transformation.getWorldMatrix(
                                        new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                                        tile.getRotation(),
                                        0.5f);
                                // Set model view matrix for this item
                                shaderManager.updateDepthShader(model);
                                // Render the mesh
                                mesh.render();
                            }
                        }
                    }
                }
            }
            for (Entity entity : entities) {
                // Calculate the Model matrix in World coordinates
                if (frustumIntersection.testSphere(new Vector3f(entity.getPosition()), 0.5f)) {
                    Mesh mesh = entity.getMesh();
                    if ((isDynamic && !mesh.isStatic()) || (!isDynamic && mesh.isStatic())) {
                        model = transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector());
                        shaderManager.updateDepthShader(model);
                        // Render the mesh
                        mesh.render();
                    }
                }
            }
            //Unbind FBO and shader
            shaderManager.unbindDepthMapShader();
        }
        // Point Light Depth Shader
        numLights = sceneLight.pointLights != null ? sceneLight.pointLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            if (sceneLight.pointLights.get(i).getIntensity() > 0) {
                PointLight pointLight = sceneLight.pointLights.get(i);
                if (isDynamic) {
                    shadowMap = pointLight.getDynamicShadowMap();
                } else {
                    shadowMap = pointLight.getStaticShadowMap();
                }

                glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
                glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
                glClear(GL_DEPTH_BUFFER_BIT);

                shaderManager.bindDepthCubeMapShader();
                shaderManager.initializeDepthCubeMapShader(transformation, pointLight.getPosition(), pointLight.getPlane());

                if (map != null) {
                    for (Tile[] row : map.getTiles()) {
                        for (Tile tile : row) {
                            if (tile == null) {
                                continue;
                            }
                            // Calculate the Model matrix in World coordinates
                            Mesh mesh = tile.getMesh();
                            if ((isDynamic && !mesh.isStatic()) || (!isDynamic && mesh.isStatic())) {
                                model = transformation.getWorldMatrix(
                                        new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                                        tile.getRotation(),
                                        0.5f);
                                // Set model view matrix for this item
                                shaderManager.updateDepthCubeMapShader(model);
                                // Render the mesh
                                mesh.render();
                            }
                        }
                    }
                }
                for (Entity entity : entities) {
                    Mesh mesh = entity.getMesh();
                    if ((isDynamic && !mesh.isStatic()) || (!isDynamic && mesh.isStatic())) {
                        model = transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector());
                        shaderManager.updateDepthCubeMapShader(model);
                        mesh.render();
                    }
                }
                //Unbind FBO and shader
                shaderManager.unbindDepthCubeMapShader();
            }
        }
        // Spot Light Depth Shader
        numLights = sceneLight.spotLights != null ? sceneLight.spotLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            if (sceneLight.spotLights.get(i).getIntensity() > 0) {
                SpotLight spotLight = sceneLight.spotLights.get(i);
                if (isDynamic) {
                    shadowMap = spotLight.getDynamicShadowMap();
                } else {
                    shadowMap = spotLight.getStaticShadowMap();
                }

                glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
                glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
                glClear(GL_DEPTH_BUFFER_BIT);

                frustumIntersection.set(spotLight.getLightSpaceMatrix());
                shaderManager.bindDepthMapShader();
                shaderManager.initializeDepthShader(spotLight.getLightSpaceMatrix());
                if (map != null) {
                    for (Tile[] row : map.getTiles()) {
                        for (Tile tile : row) {
                            if (tile == null) {
                                continue;
                            }
                            Vector3f tilePos = new Vector3f(tile.getPosition().x, 0, tile.getPosition().y);
                            int frustrum = frustumIntersection.intersectAab(new Vector3f(tilePos).sub(1.0f, 1.1f, 1.0f), new Vector3f(tilePos).add(0.0f,2.5f, 0.0f));
                            // Calculate the Model matrix in World coordinates
                            if (frustrum == -2 || frustrum == -1) {
                                Mesh mesh = tile.getMesh();
                                if ((isDynamic && !mesh.isStatic()) || (!isDynamic && mesh.isStatic())) {
                                    model = transformation.getWorldMatrix(
                                            new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                                            tile.getRotation(),
                                            0.5f);
                                    // Set model view matrix for this item
                                    shaderManager.updateDepthShader(model);
                                    // Render the mesh
                                    mesh.render();
                                }
                            }
                        }
                    }
                }
                for (Entity entity : entities) {
                    // Calculate the Model matrix in World coordinates
                    if (frustumIntersection.testSphere(new Vector3f(entity.getPosition()), 0.5f)) {
                        Mesh mesh = entity.getMesh();
                        if ((isDynamic && !mesh.isStatic()) || (!isDynamic && mesh.isStatic())) {
                            model = transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector());
                            // Set model view matrix for this item
                            shaderManager.updateDepthShader(model);
                            // Render the mesh
                            mesh.render();
                        }
                    }
                }
                //Unbind FBO and shader
                shaderManager.unbindDepthMapShader();
            }
        }
    }
}
