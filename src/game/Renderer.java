package game;

import engine.camera.Camera;
import engine.entities.Entity;
import engine.GameWindow;
import engine.Transformation;
import engine.gui.GUIComponent;
import engine.gui.Layer;
import engine.lights.SceneLight;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import graphics.ShadowsManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
    private ShaderManager shaderManager;
    private ShadowsManager shadowsManager;
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
        shaderManager = new ShaderManager();
        shaderManager.setupSceneShader();
        shaderManager.setupDepthShader();

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

    /**
     * Renders the scene
     *
     * @param camera           Camera
     * @param entities         List of entities to draw
     * @param sceneLight       The scene light object
     */

    public void render(
            Camera camera,
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

        if (shadowEnable){
            if (firstRender) {
                shadowsManager.renderStaticShadows(transformation, sceneLight, shaderManager, map, entities);
                firstRender = false;
            }
            shadowsManager.renderDynamicShadows(transformation, sceneLight, shaderManager, map, entities);
        }
        renderScene(camera, entities, sceneLight, map);
    }

    public void renderScene(Camera camera,
                            Entity[] entities,
                            SceneLight sceneLight,
                            Map map) {
        // Compute neccessary matrices
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
                    // Calculate the Model matrix in World coordinates
                    Mesh mesh = tile.getMesh();
                    model = transformation.getWorldMatrix(
                            new Vector3f(tile.getPosition().x, 0, tile.getPosition().y),
                            tile.getRotation(),
                            0.5f);
                    shaderManager.updateSceneShader(model, projectionAndView, mesh.getMaterial());
                    shaderManager.allocateTextureUnitsToSceneShader(null, sceneLight);
                    // Render the mesh
                    mesh.render();
                }
            }
        }
        // Render Entities
        for (Entity entity : entities) {
            Mesh mesh = entity.getMesh();
            model = transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScaleVector());
            shaderManager.updateSceneShader(model, projectionAndView, mesh.getMaterial());
            shaderManager.allocateTextureUnitsToSceneShader(null, sceneLight);
            // Render the mesh
            mesh.render();
        }
        shaderManager.unbindSceneShader();
    }
  
    public void terminate() {
        shaderManager.terminate();
    }
}

