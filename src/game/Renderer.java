package game;

import engine.camera.Camera;
import engine.entities.Entity;
import engine.GameWindow;
import engine.Transformation;
import engine.gui.GUIComponent;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.util.Utilities;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import graphics.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

import static org.lwjgl.opengl.GL11.*;

/**
 * Class that handles all graphic updates
 *
 * @author Cas Wognum (TU/e, 1012585)
 */

public class Renderer {

    private Shader sceneShader;
    private Shader guiShader;

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
        setupGUIShader();
    }

    private void setupSceneShader() throws Exception {
        // Create a sceneShader program
        sceneShader = new Shader();
        sceneShader.createVertexShader(Utilities.loadResource("/shaders/vertex.vs"));
        sceneShader.createFragmentShader(Utilities.loadResource("/shaders/fragment.fs"));
        sceneShader.link();

        // Create uniforms
        sceneShader.createUniform("projectionMatrix");
        sceneShader.createUniform("modelViewMatrix");
        sceneShader.createUniform("texture_sampler");

        // Create uniform for material
        sceneShader.createMaterialUniform("material");

        // Create lighting related uniforms
        sceneShader.createUniform("specularPower");
        sceneShader.createUniform("ambientLight");
        sceneShader.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShader.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShader.createDirectionalLightUniform("directionalLight");

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

    /**
     * Reset the screen to the clear color
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Renders the scene
     *
     * @param camera           Camera
     * @param entities         List of entities to draw
     * @param ambientLight     Ambient light
     * @param pointLightList   List of point lights
     * @param spotLightList    List of spot lights
     * @param directionalLight Directional light
     */
    public void render(
            Camera camera,
            GUI gui,
            Entity[] entities,
            Vector3f ambientLight,
            PointLight[] pointLightList,
            SpotLight[] spotLightList,
            DirectionalLight directionalLight,
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

        renderScene(camera, entities, ambientLight, pointLightList, spotLightList, directionalLight, map);
        if (gui != null) {
            renderGui(gui);
        }
    }

    public void renderScene(Camera camera,
                            Entity[] entities,
                            Vector3f ambientLight,
                            PointLight[] pointLightList,
                            SpotLight[] spotLightList,
                            DirectionalLight directionalLight,
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

        sceneShader.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Update Light Uniforms
        renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight);

        sceneShader.setUniform("texture_sampler", 0);

        for (Tile[] row : map.getTiles()) {
            for (Tile tile : row) {
                Mesh mesh = tile.getMesh();
                // Set model view matrix for this item
                Matrix4f modelViewMatrix = transformation.getModelViewMatrix(tile, viewMatrix);
                sceneShader.setUniform("modelViewMatrix", modelViewMatrix);

                // Render the mes for this game item
                sceneShader.setUniform("material", mesh.getMaterial());

                mesh.render();
            }
        }

        for (Entity entity : entities) {

            Mesh mesh = entity.getMesh();

            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(entity, viewMatrix);
            sceneShader.setUniform("modelViewMatrix", modelViewMatrix);

            // Render the mes for this game item
            sceneShader.setUniform("material", mesh.getMaterial());

            mesh.render();
        }

        sceneShader.unbind();
    }

    /**
     * Renders the lights
     *
     * @param viewMatrix       View matrix
     * @param ambientLight     Ambient light
     * @param pointLightList   List of point lights
     * @param spotLightList    List of spot lights
     * @param directionalLight Direction light
     */
    private void renderLights(
            Matrix4f viewMatrix,
            Vector3f ambientLight,
            PointLight[] pointLightList,
            SpotLight[] spotLightList,
            DirectionalLight directionalLight
    ) {

        sceneShader.setUniform("ambientLight", ambientLight);
        sceneShader.setUniform("specularPower", specularPower);

        // Process Point Lights
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            sceneShader.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();

            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            sceneShader.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        if (directionalLight != null) {
            DirectionalLight currDirLight = new DirectionalLight(directionalLight);
            Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
            dir.mul(viewMatrix);
            currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
            sceneShader.setUniform("directionalLight", currDirLight);
        }

    }

    private void renderGui(GUI gui) {
        guiShader.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, GameWindow.getGameWindow().getWindowWidth(),
                GameWindow.getGameWindow().getWindowHeight(), 0);

        for (GUIComponent gameItem : gui.getGUIComponents()) {
            Mesh mesh = gameItem.getMesh();
            // Set ortohtaphic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrtoProjModelMatrix(gameItem, ortho);
            guiShader.setUniform("projModelMatrix", projModelMatrix);
            guiShader.setUniform("colour", gameItem.getMesh().getMaterial().getAmbientColour());
            guiShader.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

            // Render the mesh for this HUD item
            mesh.render();
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

