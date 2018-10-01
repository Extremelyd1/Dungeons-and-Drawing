package game;

import engine.camera.Camera;
import engine.entities.Entity;
import engine.GameWindow;
import engine.Transformation;
import engine.gui.GUIComponent;
import engine.gui.Layer;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.lights.SpotLight;
import engine.util.Utilities;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import graphics.Shader;
import graphics.ShadowMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import sun.security.ssl.Debug;


import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * Class that handles all graphic updates
 *
 * @author Cas Wognum (TU/e, 1012585)
 */

public class Renderer {

    private Shader sceneShader;
    private Shader guiShader;
    private Shader depthShaderCube;
    private Shader depthShader;

    private static final float FOV = (float) Math.toRadians(45.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private final Transformation transformation;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private final float specularPower;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init() throws Exception {
        setupSceneShader();
        setupDepthShader();
        setupGUIShader();
    }

    private void setupSceneShader() throws Exception {
        // Create a sceneShader program
        sceneShader = new Shader();
        sceneShader.createVertexShader(Utilities.loadResource("/shaders/vertex.vs"));
        sceneShader.createFragmentShader(Utilities.loadResource("/shaders/fragment.fs"));
        sceneShader.link();

        // Create uniforms
        sceneShader.createUniform("texture_sampler");

        // Create uniform for material
        sceneShader.createMaterialUniform("material");

        // Create lighting related uniforms
        sceneShader.createUniform("specularPower");
        sceneShader.createUniform("ambientLight");
        sceneShader.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShader.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        //sceneShader.createDirectionalLightUniform("directionalLight"); // NOT IMPLEMENTED

        // Shadow mapping related uniforms
        sceneShader.createUniform("viewPos");
        sceneShader.createUniform("model");
        //sceneShader.createUniform("view");
        //sceneShader.createUniform("projection");
        sceneShader.createUniform("projectionViewModel");

        GameWindow.getGameWindow().setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    private void setupGUIShader() throws Exception {
        guiShader = new Shader();
        guiShader.createVertexShader(Utilities.loadResource("/shaders/vertex_gui.vs"));
        guiShader.createFragmentShader(Utilities.loadResource("/shaders/fragment_gui.fs"));
        guiShader.link();

        // Create uniforms for Ortographic-model projection matrix and base colour
        guiShader.createUniform("projModelMatrix");
        guiShader.createUniform("colour");
        guiShader.createUniform("hasTexture");
    }

    private void setupDepthShader() throws Exception {
        // Create Depth Cube Shader
        depthShaderCube = new Shader();
        depthShaderCube.createVertexShader(Utilities.loadResource("/shaders/depth_vertex_cube.vs"));
        depthShaderCube.createGeometryShader(Utilities.loadResource("/shaders/depth_geometry_cube.gs"));
        depthShaderCube.createFragmentShader(Utilities.loadResource("/shaders/depth_fragment_cube.fs"));
        depthShaderCube.link();
        // Create Depth Cube Shader variables
        depthShaderCube.createUniform("modelMatrix");
        depthShaderCube.createUniform("shadowMatrices");
        depthShaderCube.createUniform("lightPos");
        depthShaderCube.createUniform("far_plane");
        // Create Depth Shader
        depthShader = new Shader();
        depthShader.createVertexShader(Utilities.loadResource("/shaders/depth_vertex.vs"));
        depthShader.createFragmentShader(Utilities.loadResource("/shaders/depth_fragment.fs"));
        depthShader.link();
        // Create Depth Shader variables
        depthShader.createUniform("lightSpaceMatrix");
        depthShader.createUniform("modelMatrix");
    }

    /**
     * Reset the screen to the clear color
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Renders the scene (CHECKED OK)
     *
     * @param camera           Camera
     * @param entities         List of entities to draw
     * @param sceneLight       The scene light object
     */
    public void render(
            Camera camera,
            GUI gui,
            Entity[] entities,
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
            }
        });

        renderScene(camera, entities, sceneLight, map);
        if (gui != null) {
            renderGui(gui);
        }
    }

    public void renderScene(Camera camera,
                            Entity[] entities,
                            SceneLight sceneLight,
                            Map map) {

        sceneShader.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(
                FOV,
                GameWindow.getGameWindow().getWindowWidth(),
                GameWindow.getGameWindow().getWindowHeight(),
                Z_NEAR,
                Z_FAR
        );

//        sceneShader.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Update Shadows
        renderDepthMap(camera, entities, sceneLight, map);

        GameWindow window = GameWindow.getGameWindow();
        //WORKAROUND width and height are fixed because the viewport is wrong?
        glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
        //glViewport(0, 0, 3840, 2160);

        sceneShader.bind();
        // Update Lights
        renderLights(viewMatrix, sceneLight);

        //sceneShader.setUniform("projection", projectionMatrix);
        //sceneShader.setUniform("view", viewMatrix);
        sceneShader.setUniform("texture_sampler", 0);
        sceneShader.setUniform("viewPos", camera.getPosition());

        Matrix4f projectionAndView = new Matrix4f(projectionMatrix);
        projectionAndView.mul(viewMatrix);

        int numPointLights = sceneLight.pointLights != null ? sceneLight.pointLights.size() : 0;
        int numSpotLights = sceneLight.spotLights != null ? sceneLight.spotLights.size() : 0;
        for (int i = 0; i < numPointLights; i++) {
            sceneShader.setUniform("pointLights[" + i + "].shadowMap", 1 + i);
        }
        for (int i = 0; i < numSpotLights; i++) {
            sceneShader.setUniform("spotLights[" + i + "].shadowMap", 1 + numPointLights + i);
        }

        Matrix4f model;
        Matrix4f projectionViewModel;
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        if (map != null) {
            for (Tile[] row : map.getTiles()) {
                for (Tile tile : row) {
                    Mesh mesh = tile.getMesh();
                    // Set model view matrix for this item
                    model = transformation.getWorldMatrix(
                            new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                            tile.getRotation(),
                            0.5f);
                    sceneShader.setUniform("model", model);

                    projectionViewModel = new Matrix4f(projectionAndView);
                    projectionViewModel.mul(model);
                    sceneShader.setUniform("projectionViewModel", projectionViewModel);

                    // Render the mes for this game item
                    sceneShader.setUniform("material", mesh.getMaterial());

                    for (int i = 0; i < numPointLights; i++) {
                        glActiveTexture(GL_TEXTURE1 + i);
                        glBindTexture(GL_TEXTURE_CUBE_MAP, sceneLight.pointLights.get(i).getShadowMap().getDepthMap());
                    }
                    for (int i = 0; i < numSpotLights; i++) {
                        glActiveTexture(GL_TEXTURE1 + numPointLights + i);
                        glBindTexture(GL_TEXTURE_2D, sceneLight.spotLights.get(i).getShadowMap().getDepthMap());
                    }

                    mesh.render();
                }
            }
        }

        for (Entity entity : entities) {

            Mesh mesh = entity.getMesh();
            model = transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector());

            sceneShader.setUniform("material", mesh.getMaterial());
            sceneShader.setUniform("model", model);

            projectionViewModel = new Matrix4f(projectionAndView);
            projectionViewModel.mul(model);
            sceneShader.setUniform("projectionViewModel", projectionViewModel);

            for (int i = 0; i < numPointLights; i++) {
                glActiveTexture(GL_TEXTURE1 + i);
                glBindTexture(GL_TEXTURE_CUBE_MAP, sceneLight.pointLights.get(i).getShadowMap().getDepthMap());
            }
            for (int i = 0; i < numSpotLights; i++) {
                glActiveTexture(GL_TEXTURE1 + numPointLights + i);
                glBindTexture(GL_TEXTURE_2D, sceneLight.spotLights.get(i).getShadowMap().getDepthMap());
            }

            mesh.render();
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        sceneShader.unbind();
    }

    /**
     * Renders the lights
     *
     * @param viewMatrix       View matrix
     * @param sceneLight       The scene light object
     */
    private void renderLights(
            Matrix4f viewMatrix,
            SceneLight sceneLight
    ) {

        sceneShader.setUniform("ambientLight", sceneLight.ambientLight.getLight());
        sceneShader.setUniform("specularPower", specularPower);

        // Process Point Lights
        int numLights = sceneLight.pointLights != null ? sceneLight.pointLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            sceneShader.setUniform("pointLights", sceneLight.pointLights.get(i), i);
        }
        // Process Spot Ligths
        numLights = sceneLight.spotLights != null ? sceneLight.spotLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            sceneShader.setUniform("spotLights", sceneLight.spotLights.get(i), i);
        }
    }

    /**
     * Renders the Depth Map (CHECKED OK)
     */
    private void renderDepthMap(
            Camera camera,
            Entity[] entities,
            SceneLight sceneLight,
            Map map){
        int numLights;
        // Loop through all point light sources
        glDisable(GL_CULL_FACE);
        // Point Light Depth Shader
        numLights = sceneLight.pointLights != null ? sceneLight.pointLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            PointLight pointLight = sceneLight.pointLights.get(i);
            ShadowMap shadowMap = pointLight.getShadowMap();

            glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
            glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
            glClear(GL_DEPTH_BUFFER_BIT);

            Matrix4f shadowProj = new Matrix4f();
            shadowProj.setPerspective((float) Math.toRadians(90), 1.0f,
                    pointLight.getPlane().x,
                    pointLight.getPlane().y);
            Matrix4f views[] = new Matrix4f[6];

            views[0] = transformation.getProjectionWithDirection(
                    pointLight.getPosition(),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));
            views[1] = transformation.getProjectionWithDirection(
                    pointLight.getPosition(),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));
            views[2] = transformation.getProjectionWithDirection(
                    pointLight.getPosition(),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, 0.0f, 1.0f));
            views[3] = transformation.getProjectionWithDirection(
                    pointLight.getPosition(),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, 0.0f, -1.0f));
            views[4] = transformation.getProjectionWithDirection(
                    pointLight.getPosition(),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));
            views[5] = transformation.getProjectionWithDirection(
                    pointLight.getPosition(),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));

            depthShaderCube.bind();
            depthShaderCube.setUniform("shadowMatrices", views, 6);
            depthShaderCube.setUniform("lightPos", pointLight.getPosition());
            depthShaderCube.setUniform("far_plane", pointLight.getPlane().y);
            if (map != null) {
                for (Tile[] row : map.getTiles()) {
                    for (Tile tile : row) {
                        Mesh mesh = tile.getMesh();
                        // Set model view matrix for this item
                        depthShaderCube.setUniform("modelMatrix", transformation.getWorldMatrix(
                                new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                                tile.getRotation(),
                                0.5f));

                        mesh.render();
                    }
                }
            }
            for (Entity entity : entities) {
                Mesh mesh = entity.getMesh();
                depthShaderCube.setUniform("modelMatrix",
                        transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector()));
                mesh.render();
            }
            //Unbind FBO and shader
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            depthShaderCube.unbind();
        }
        // Spot Light Depth Shader
        numLights = sceneLight.spotLights != null ? sceneLight.spotLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            SpotLight spotLight = sceneLight.spotLights.get(i);
            ShadowMap shadowMap = spotLight.getShadowMap();

            glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
            glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
            glClear(GL_DEPTH_BUFFER_BIT);

            depthShader.bind();
            depthShader.setUniform("lightSpaceMatrix", spotLight.getLightSpaceMatrix());
            if (map != null) {
                for (Tile[] row : map.getTiles()) {
                    for (Tile tile : row) {
                        Mesh mesh = tile.getMesh();
                        // Set model view matrix for this item
                        depthShader.setUniform("modelMatrix", transformation.getWorldMatrix(
                                new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                                tile.getRotation(),
                                0.5f));

                        mesh.render();
                    }
                }
            }
            for (Entity entity : entities) {
                Mesh mesh = entity.getMesh();
                depthShader.setUniform("modelMatrix",
                        transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector()));
                mesh.render();
            }
            //Unbind FBO and shader
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            depthShader.unbind();
        }
    }

    private void renderGui(GUI gui) {

        guiShader.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, GameWindow.getGameWindow().getWindowWidth(),
                GameWindow.getGameWindow().getWindowHeight(), 0);

        for (Layer layer : gui.getLayers()) {

            for (GUIComponent element : layer.getElements()) {

                // Set ortohtaphic and model matrix for this HUD item
                Matrix4f projModelMatrix = transformation.getOrtoProjModelMatrix(element, ortho);
                guiShader.setUniform("projModelMatrix", projModelMatrix);

                Mesh mesh = element.getMesh();

                guiShader.setUniform("colour", mesh.getMaterial().getAmbientColour());
                guiShader.setUniform("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);

                // Render the mesh for this HUD item
                mesh.render();
            }
        }

        guiShader.unbind();
    }

    public void terminate() {
        if (sceneShader != null) {
            sceneShader.terminate();
        }

        if (guiShader != null) {
            guiShader.terminate();
        }
    }
}

