package game;

import engine.Camera;
import engine.entities.GameEntity;
import engine.GameWindow;
import engine.Transformation;
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

    private Shader shader;

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

    public void init(GameWindow window) throws Exception {

        // Create a shader program
        shader = new Shader();
        shader.createVertexShader(Utilities.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utilities.loadResource("/shaders/fragment.fs"));
        shader.link();

        // Create uniforms
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("texture_sampler");

        // Create uniform for material
        shader.createMaterialUniform("material");

        // Create lighting related uniforms
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");
        shader.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        shader.createDirectionalLightUniform("directionalLight");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
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
     * @param window           Game window
     * @param camera           Camera
     * @param entities         List of entities to draw
     * @param ambientLight     Ambient light
     * @param pointLightList   List of point lights
     * @param spotLightList    List of spot lights
     * @param directionalLight Directional light
     */
    public void render(
            GameWindow window,
            Camera camera,
            GameEntity[] entities,
            Vector3f ambientLight,
            PointLight[] pointLightList,
            SpotLight[] spotLightList,
            DirectionalLight directionalLight,
            Map map
    ) {

        clear();
        /* We attach a callback which is invokd when we resize the window */
        glfwSetWindowSizeCallback(window.getWindowHandle(), new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                glfwSetWindowSize(window, width, height); //Set new window size
                glViewport(0, 0, width, height); //Update the Viewport with new width and height
            }
        });

        shader.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(
                FOV,
                window.getWindowWidth(),
                window.getWindowHeight(),
                Z_NEAR,
                Z_FAR
        );

        shader.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Update Light Uniforms
        renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight);

        shader.setUniform("texture_sampler", 0);

        for (Tile[] row : map.getTiles()) {
            for (Tile tile : row) {
                Mesh mesh = tile.getMesh();
                // Set model view matrix for this item
                Matrix4f modelViewMatrix = transformation.getModelViewMatrix(tile, viewMatrix);
                shader.setUniform("modelViewMatrix", modelViewMatrix);

                // Render the mes for this game item
                shader.setUniform("material", mesh.getMaterial());

                mesh.render();
            }
        }

        for (GameEntity entity : entities) {

            Mesh mesh = entity.getMesh();

            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(entity, viewMatrix);
            shader.setUniform("modelViewMatrix", modelViewMatrix);

            // Render the mes for this game item
            shader.setUniform("material", mesh.getMaterial());

            mesh.render();
        }

        shader.unbind();
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

        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("specularPower", specularPower);

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
            shader.setUniform("pointLights", currPointLight, i);
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

            shader.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        if (directionalLight != null) {
            DirectionalLight currDirLight = new DirectionalLight(directionalLight);
            Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
            dir.mul(viewMatrix);
            currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
            shader.setUniform("directionalLight", currDirLight);
        }

    }

    /**
     * Free up resources
     */
    public void terminate() {
        shader.terminate();
    }
}

