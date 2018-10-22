package game;

import engine.camera.Camera;
import engine.entities.Entity;
import engine.GameWindow;
import engine.Transformation;
import engine.entities.animatedModel.Player;
import engine.gui.NanoVG;
import engine.lights.SceneLight;
import game.map.Map;
import game.map.tile.Tile;
import game.mobs.Snake;
import graphics.HDR;
import graphics.Mesh;
import graphics.ShadowsManager;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import sun.security.ssl.Debug;


import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Class that handles all graphic updates
 *
 * @author Cas Wognum (TU/e, 1012585)
 */

public class Renderer {
    private FrustumIntersection frustumIntersection;
    private ShaderManager shaderManager;
    private ShadowsManager shadowsManager;
    private HDR hdrManager;
    private boolean firstRender = true;

    private static final float FOV = (float) Math.toRadians(45.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private final Transformation transformation;

    private final float specularPower;
    private boolean shadowEnable = true;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init() throws Exception {
        shadowsManager = new ShadowsManager();
        frustumIntersection = new FrustumIntersection();
        shaderManager = new ShaderManager();
        shaderManager.setupSceneShader();
        shaderManager.setupDepthShader();
        shaderManager.setupHDRShader();

        // Permanently Enable Back Face Culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    /**
     * Reset the screen to the clear color
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void render(Camera camera, Entity[] entities, SceneLight sceneLight, Map map) {
        List<Entity> entityList = new ArrayList<>();
        for (Entity entity : entities) {
            entityList.add(entity);
        }
        render(camera, entityList, sceneLight, map);
    }

    /**
     * Renders the scene
     *
     * @param camera           Camera
     * @param entities         List of entities to draw
     * @param sceneLight       The scene light object
     */
    public void render(
            Camera camera,
            List<Entity>  entities,
            SceneLight sceneLight,
            Map map
    ) {
        clear();

        GameWindow window = GameWindow.getGameWindow();
        /* We attach a callback which is invoked when we resize the window */
        glfwSetWindowSizeCallback(window.getWindowHandle(), new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long windowHandle, int width, int height){
                glfwSetWindowSize(windowHandle, width, height); //Set new window size
                window.setWindowHeight(height);
                window.setWindowWidth(width);
                glViewport(0, 0, width, height); //Update the Viewport with new width and height
                NanoVG.reload();
            }
        });

        if (hdrManager == null) {
            hdrManager = new HDR(window.getWindowWidth(), window.getWindowHeight());
            try {hdrManager.init();} catch (Exception e) { Debug.println("HDR", "FAILURE TO INITIALIZE");}
        } else if (hdrManager.getWidth() != window.getWindowWidth() || hdrManager.getHeight() != window.getWindowHeight()) {
            hdrManager.cleanup();
            hdrManager = new HDR(window.getWindowWidth(), window.getWindowHeight());
            try {hdrManager.init();} catch (Exception e) { Debug.println("HDR", "FAILURE TO INITIALIZE");}
        }

        if (shadowEnable){
            if (firstRender) {
                shadowsManager.renderStaticShadows(transformation, sceneLight, shaderManager, map, entities);
                firstRender = false;
            }
            shadowsManager.renderDynamicShadows(transformation, sceneLight, shaderManager, map, entities);
        }

        glBindRenderbuffer(GL_RENDERBUFFER, hdrManager.getRender());
        glBindFramebuffer(GL_FRAMEBUFFER, hdrManager.getHdrFBO());
        clear();
        renderScene(camera, entities, sceneLight, map);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        clear();
        glDisable(GL_CULL_FACE);
        shaderManager.bindHDRShader();
        shaderManager.allocateTextureUnitsToHDRShader(hdrManager.getHdr());
        hdrManager.renderQuad();
        shaderManager.unbindHDRShader();
        glEnable(GL_CULL_FACE);
    }

    public void renderScene(Camera camera,
                            List<Entity> entities,
                            SceneLight sceneLight,
                            Map map) {
        // Compute necessary matrices
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(
                FOV,
                GameWindow.getGameWindow().getWindowWidth(),
                GameWindow.getGameWindow().getWindowHeight(),
                Z_NEAR,
                Z_FAR
        );
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        Matrix4f projectionAndView = new Matrix4f(projectionMatrix);
        projectionAndView.mul(viewMatrix);
        frustumIntersection.set(projectionAndView);
        Matrix4f model;

        // Update ViewPort
        GameWindow window = GameWindow.getGameWindow();
        glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());

        shaderManager.bindSceneShader();
        shaderManager.initializeSceneShader(camera.getPosition(), shadowEnable, sceneLight, specularPower);
        // Render Map Layout
        if (map != null) {
            for (Tile[] row : map.getTiles()) {
                for (Tile tile : row) {
                    if (tile == null) {
                        continue;
                    }
                    Vector3f tilePos = new Vector3f(tile.getPosition().x, 0, tile.getPosition().y);
                    int frustrum = frustumIntersection.intersectAab(new Vector3f(tilePos).sub(1.0f, 1.1f, 1.0f), new Vector3f(tilePos).add(1.0f,3.0f, 1.0f));
                    // Calculate the Model matrix in World coordinates
                    if (frustrum == -2 || frustrum == -1) {
                        Mesh mesh = tile.getMesh();
                        model = transformation.getWorldMatrix(
                                new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                                tile.getRotation(),
                                0.5f);
                        shaderManager.updateSceneShader(model, projectionAndView, mesh.getMaterial());
                        shaderManager.allocateTextureUnitsToSceneShader(null, sceneLight);
                        shaderManager.setSceneShaderModeDefault();
                        // Render the mesh
                        mesh.render();
                    }
                }
            }
        }

        // Render Entities
        for (Entity entity : entities) {
            int frustrum = frustumIntersection.intersectAab(new Vector3f(entity.getPosition()).sub(1.0f, 1.1f, 1.0f), new Vector3f(entity.getPosition()).add(1.0f,3.0f, 1.0f));
            if (frustrum == -2 || frustrum == -1) {
                Mesh mesh = entity.getMesh();
                model = transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector());
                shaderManager.updateSceneShader(model, projectionAndView, mesh.getMaterial());
                shaderManager.allocateTextureUnitsToSceneShader(null, sceneLight);
                // Render the mesh
                if (entity instanceof Snake) {
                    shaderManager.setSceneShaderMode0(((Snake) entity).getMorph(), new Vector3f(entity.getPosition()).add(1,0,0));
                } else if (entity instanceof Player) {
                    shaderManager.setSceneShaderMode1(((Player) entity).getAnimatedModel().getJointTransforms());
                } else {
                    shaderManager.setSceneShaderModeDefault();
                }
                mesh.render();
            }
        }
        shaderManager.unbindSceneShader();
    }
  
    public void terminate() {
        shaderManager.terminate();
    }

    public void resetShadowMap(){
        firstRender = true;
    }
}