package game;

import engine.Camera;
import engine.GameEntity;
import engine.GameWindow;
import engine.Transformation;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.util.Utilities;
import graphics.Mesh;
import graphics.Shader;
import graphics.ShadowMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import sun.security.ssl.Debug;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * Class that handles all graphic updates
 *
 * @author Cas Wognum (TU/e, 1012585)
 */

public class Renderer {

    private Shader shader;
    private Shader depthShader;

    private static final float FOV = (float) Math.toRadians(45.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private final Transformation transformation;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private final float specularPower;

    private ShadowMap shadowMap;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(GameWindow window) throws Exception {
        // Setup Depth Shader
        setupDepthShader();

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

        // Shadow mapping related uniforms
        shader.createUniform("shadowMap");
        shader.createUniform("orthoProjectionMatrix");
        shader.createUniform("modelLightViewMatrix");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    // CHECKED OK
    private void setupDepthShader() throws Exception {
        // Create ShadowMap Object
        shadowMap = new ShadowMap(2048);
        // Create Depth Shader
        depthShader = new Shader();
        depthShader.createVertexShader(Utilities.loadResource("/shaders/depth_vertex.vs"));
        depthShader.createFragmentShader(Utilities.loadResource("/shaders/depth_fragment.fs"));
        depthShader.link();
        // Create Depth Shader variables
        depthShader.createUniform("orthoProjectionMatrix");
        depthShader.createUniform("modelLightViewMatrix");
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
            DirectionalLight directionalLight
    ) {

        clear();

        renderDepthMap(window, camera, entities, pointLightList, spotLightList, directionalLight);

        /* We attach a callback which is invokd when we resize the window */
        glfwSetWindowSizeCallback(window.getWindowHandle(), new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                glfwSetWindowSize(window, width, height); //Set new window size
                glViewport(0, 0, width, height); //Update the Viewport with new width and height
            }
        });

        //WORKAROUND width and height are fixed because the viewport is wrong?
        //glViewport(0, 0, getWindowWidth(), getWindowHeight());
        glViewport(0, 0, 3840, 2160);

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
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        shader.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Update Light Uniforms
        renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight);

        shader.setUniform("texture_sampler", 0);
        shader.setUniform("shadowMap", 2);

        for (GameEntity entity : entities) {
            Mesh mesh = entity.getMesh();

            // Render the mes for this game item
            shader.setUniform("material", mesh.getMaterial());

            glActiveTexture(GL_TEXTURE2);
            shadowMap.getDepthMap().bind();

            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(entity, viewMatrix);
            shader.setUniform("modelViewMatrix", modelViewMatrix);

            Matrix4f modelLightViewMatrix = transformation.updateModelLightViewMatrix(entity, lightViewMatrix);
            shader.setUniform("modelLightViewMatrix", modelLightViewMatrix);

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
     * Renders the Depth Map (CHECKED OK)
     */
    private void renderDepthMap(
            GameWindow window,
            Camera camera,
            GameEntity[] entities,
            PointLight[] pointLightList,
            SpotLight[] spotLightList,
            DirectionalLight directionalLight){
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShader.bind();

        Vector3f lightDirection = directionalLight.getDirection();

        float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(
                new Vector3f(lightDirection).mul(5.0f), new Vector3f(lightAngleX, lightAngleY, 0));
        DirectionalLight.OrthoCoords orthCoords = directionalLight.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(
                orthCoords.left, orthCoords.right, orthCoords.bottom,
                orthCoords.top , orthCoords.near , orthCoords.far);

        depthShader.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        for (GameEntity entity : entities) {
            Mesh mesh = entity.getMesh();

            Matrix4f modelLightViewMatrix = transformation.getModelViewMatrix(entity, lightViewMatrix);
            depthShader.setUniform("modelLightViewMatrix", modelLightViewMatrix);

            mesh.render();
        }

        //Unbind Shader and FBO
        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Free up resources
     */
    public void terminate() {
        shader.terminate();
    }
}

