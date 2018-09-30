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

    private Shader shader;
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

    public void init(GameWindow window) throws Exception {
        // Setup Depth Shader
        setupDepthShader();

        // Create a shader program
        shader = new Shader();
        shader.createVertexShader(Utilities.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utilities.loadResource("/shaders/fragment.fs"));
        shader.link();

        // Create uniforms
        shader.createUniform("texture_sampler");

        // Create uniform for material
        shader.createMaterialUniform("material");

        // Create lighting related uniforms
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");
        shader.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        //shader.createDirectionalLightUniform("directionalLight"); // NOT IMPLEMENTED

        // Shadow mapping related uniforms
        shader.createUniform("viewPos");

        shader.createUniform("model");
        shader.createUniform("view");
        shader.createUniform("projection");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Reset the screen to the clear color
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    long shadowRenderTime = 0;
    long sceneRenderTime = 0;
    int frameCounter = 0;
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
        long time;
        clear();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(
                FOV,
                window.getWindowWidth(),
                window.getWindowHeight(),
                Z_NEAR,
                Z_FAR
        );

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        time = System.nanoTime();
        // Update Shadows
        renderDepthMap(window, camera, entities, pointLightList, spotLightList, directionalLight);
        shadowRenderTime += System.nanoTime() - time;

        /* We attach a callback which is invokd when we resize the window */
        glfwSetWindowSizeCallback(window.getWindowHandle(), new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                glfwSetWindowSize(window, width, height); //Set new window size
                glViewport(0, 0, width, height); //Update the Viewport with new width and height
            }
        });

        //WORKAROUND width and height are fixed because the viewport is wrong?
        glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
        //glViewport(0, 0, 3840, 2160);

        shader.bind();
        // Update Lights
        renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight);

        shader.setUniform("projection", projectionMatrix);
        shader.setUniform("view", viewMatrix);
        shader.setUniform("texture_sampler", 0);
        shader.setUniform("viewPos", camera.getPosition());

        int numPointLights = pointLightList != null ? pointLightList.length : 0;
        int numSpotLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numPointLights; i++) {
            shader.setUniform("pointLights[" + i + "].shadowMap", 1 + i);
        }
        for (int i = 0; i < numSpotLights; i++) {
            shader.setUniform("spotLights[" + i + "].shadowMap", 1 + numPointLights + i);
        }

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        time = System.nanoTime();
        for (GameEntity entity : entities) {
            Mesh mesh = entity.getMesh();

            shader.setUniform("material", mesh.getMaterial());
            shader.setUniform("model",
                    transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScale()));

            for (int i = 0; i < numPointLights; i++) {
                glActiveTexture(GL_TEXTURE1 + i);
                glBindTexture(GL_TEXTURE_CUBE_MAP, pointLightList[i].getShadowMap().getDepthMap());
            }
            for (int i = 0; i < numSpotLights; i++) {
                glActiveTexture(GL_TEXTURE1 + numPointLights + i);
                glBindTexture(GL_TEXTURE_2D, spotLightList[i].getShadowMap().getDepthMap());
            }

            mesh.render();
        }
        sceneRenderTime += System.nanoTime() - time;
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        shader.unbind();

        frameCounter++;
        if (frameCounter == 60) {
            shadowRenderTime /= 60;
            sceneRenderTime /= 60;
            Debug.println("Render time", (shadowRenderTime / 1000) + "us " + (sceneRenderTime / 1000) + "us");
            frameCounter = 0;
            shadowRenderTime = 0;
            sceneRenderTime = 0;
        }
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
            shader.setUniform("pointLights", pointLightList[i], i);
        }
        // Process Spot Ligths
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("spotLights", spotLightList[i], i);
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
        int numLights;
        // Loop through all point light sources
        glDisable(GL_CULL_FACE);
        // Point Light Depth Shader
        numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            ShadowMap shadowMap = pointLightList[i].getShadowMap();

            glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
            glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
            glClear(GL_DEPTH_BUFFER_BIT);

            Matrix4f shadowProj = new Matrix4f();
            shadowProj.setPerspective((float) Math.toRadians(90), 1.0f,
                    pointLightList[i].getPlane().x,
                    pointLightList[i].getPlane().y);
            Matrix4f views[] = new Matrix4f[6];

            views[0] = transformation.getProjectionWithDirection(
                    pointLightList[i].getPosition(),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));
            views[1] = transformation.getProjectionWithDirection(
                    pointLightList[i].getPosition(),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));
            views[2] = transformation.getProjectionWithDirection(
                    pointLightList[i].getPosition(),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, 0.0f, 1.0f));
            views[3] = transformation.getProjectionWithDirection(
                    pointLightList[i].getPosition(),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    shadowProj,
                    new Vector3f(0.0f, 0.0f, -1.0f));
            views[4] = transformation.getProjectionWithDirection(
                    pointLightList[i].getPosition(),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));
            views[5] = transformation.getProjectionWithDirection(
                    pointLightList[i].getPosition(),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    shadowProj,
                    new Vector3f(0.0f, -1.0f, 0.0f));

            depthShaderCube.bind();
            depthShaderCube.setUniform("shadowMatrices", views, 6);
            depthShaderCube.setUniform("lightPos", pointLightList[i].getPosition());
            depthShaderCube.setUniform("far_plane", pointLightList[i].getPlane().y);
            for (GameEntity entity : entities) {
                Mesh mesh = entity.getMesh();
                depthShaderCube.setUniform("modelMatrix",
                        transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScale()));
                mesh.render();
            }
            //Unbind FBO and shader
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            depthShaderCube.unbind();
        }
        // Spot Light Depth Shader
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            ShadowMap shadowMap = spotLightList[i].getShadowMap();

            glViewport(0, 0, shadowMap.getResolution(), shadowMap.getResolution());
            glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
            glClear(GL_DEPTH_BUFFER_BIT);

            depthShader.bind();
            depthShader.setUniform("lightSpaceMatrix", spotLightList[i].getLightSpaceMatrix());
            for (GameEntity entity : entities) {
                Mesh mesh = entity.getMesh();
                depthShader.setUniform("modelMatrix",
                        transformation.getWorldMatrix(entity.getPosition(), entity.getRotation(), entity.getScale()));
                mesh.render();
            }
            //Unbind FBO and shader
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            depthShader.unbind();
        }
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
     * Free up resources
     */
    public void terminate() {
        shader.terminate();
    }
}

