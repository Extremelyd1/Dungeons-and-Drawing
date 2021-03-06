package game;

import engine.GameWindow;
import engine.Transformation;
import engine.lights.SceneLight;
import engine.util.Utilities;
import graphics.Material;
import graphics.Shader;
import graphics.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ShaderManager {

    private static final int MAX_POINT_LIGHTS = 10;
    private static final int MAX_SPOT_LIGHTS = 10;

    private Shader sceneShader;
    private Shader depthShaderCube;
    private Shader depthShader;
    private Shader hdrShader;

    /**
     * Initialize the main shader for the scene
     * @throws Exception
     */
    public void setupSceneShader() throws Exception {
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
        sceneShader.createDirectionalLightUniform("directionalLight");

        // Shadow mapping related uniforms
        sceneShader.createUniform("viewPos");
        sceneShader.createUniform("model");
        sceneShader.createUniform("projectionViewModel");

        // Mode switching
        sceneShader.createUniform("mode");

        // Mode 0 (Snake morphing)
        sceneShader.createUniform("step");
        sceneShader.createUniform("headPos");

        // Mode 1 (Player animation)
        sceneShader.createUniform("jointTransforms");

        GameWindow.getGameWindow().setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Initialize the shaders required for shadow mapping
     * @throws Exception
     */
    public void setupDepthShader() throws Exception {
        // Create Depth Cube Shader
        depthShaderCube = new Shader();
        depthShaderCube.createVertexShader(Utilities.loadResource("/shaders/depth_vertex_cube.vs"));
        //depthShaderCube.createGeometryShader(Utilities.loadResource("/shaders/depth_geometry_cube.gs"));
        depthShaderCube.createFragmentShader(Utilities.loadResource("/shaders/depth_fragment_cube.fs"));
        depthShaderCube.link();
        // Create Depth Cube Shader variables
        depthShaderCube.createUniform("modelMatrix");
        depthShaderCube.createUniform("shadowMatrice");
        depthShaderCube.createUniform("lightPos");
        depthShaderCube.createUniform("far_plane");
        // Mode switching
        depthShaderCube.createUniform("mode");
        // Mode 0 (Snake morphing)
        depthShaderCube.createUniform("step");
        depthShaderCube.createUniform("headPos");
        // Mode 1 (Player animation)
        depthShaderCube.createUniform("jointTransforms");

        // Create Depth Shader
        depthShader = new Shader();
        depthShader.createVertexShader(Utilities.loadResource("/shaders/depth_vertex.vs"));
        depthShader.createFragmentShader(Utilities.loadResource("/shaders/depth_fragment.fs"));
        depthShader.link();
        // Create Depth Shader variables
        depthShader.createUniform("lightSpaceMatrix");
        depthShader.createUniform("modelMatrix");
        // Mode switching
        depthShader.createUniform("mode");
        // Mode 0 (Snake morphing)
        depthShader.createUniform("step");
        depthShader.createUniform("headPos");
        // Mode 1 (Player animation)
        depthShader.createUniform("jointTransforms");
    }

    /**
     * Initialize the HDR shader
     */
    public void setupHDRShader() throws Exception {
        hdrShader = new Shader();
        hdrShader.createVertexShader(Utilities.loadResource("/shaders/hdr.vs"));
        hdrShader.createFragmentShader(Utilities.loadResource("/shaders/hdr.fs"));
        hdrShader.link();
        // Create HDR Shader Variables
        hdrShader.createUniform("hdrTexture");
        hdrShader.createUniform("exposure");
    }

    //
    // Scene shader management Functions
    //
    public void bindSceneShader(){
        sceneShader.bind();
    }
    public void initializeSceneShader(Vector3f viewPos, boolean shadowEnable, SceneLight sceneLight, float specularPower){
        if (sceneLight == null) return;
        int numPointLights = sceneLight.pointLights != null ? sceneLight.pointLights.size() : 0;
        int numSpotLights = sceneLight.spotLights != null ? sceneLight.spotLights.size() : 0;

        // Base variables
        sceneShader.setUniform("viewPos", viewPos);
        sceneShader.setUniform("ambientLight", sceneLight.ambientLight.getLight());
        sceneShader.setUniform("specularPower", specularPower);
        // Texture for the model
        sceneShader.setUniform("texture_sampler", 0);
        // This will update the lighting parameters
        if (sceneLight.directionalLight != null) {
            sceneShader.setUniform("directionalLight", sceneLight.directionalLight);
        }
        for (int i = 0; i < numPointLights; i++) {
            sceneShader.setUniform("pointLights", sceneLight.pointLights.get(i), i);
        }
        for (int i = 0; i < numSpotLights; i++) {
            sceneShader.setUniform("spotLights", sceneLight.spotLights.get(i), i);
        }
        // This will update the shadows parameters
        for (int i = 0; i < numPointLights; i++) {
            sceneShader.setUniform("pointLights[" + i + "].staticShadowMap",  1 + i * 2);
            sceneShader.setUniform("pointLights[" + i + "].dynamicShadowMap", 1 + i * 2 + 1);
        }
        for (int i = 0; i < numSpotLights; i++) {
            sceneShader.setUniform("spotLights[" + i + "].staticShadowMap",  1 + numPointLights * 2 + i * 2);
            sceneShader.setUniform("spotLights[" + i + "].dynamicShadowMap", 1 + numPointLights * 2 + i * 2 + 1);
        }
        if (sceneLight.directionalLight != null) {
            sceneShader.setUniform("directionalLight.staticShadowMap",  1 + numPointLights * 2 + numSpotLights * 2);
            sceneShader.setUniform("directionalLight.dynamicShadowMap", 1 + numPointLights * 2 + numSpotLights * 2 + 1);
        }
    }
    public void updateSceneShader(Matrix4f model, Matrix4f projectionAndView, Material material) {
        // Compute matrix
        Matrix4f projectionViewModel = new Matrix4f(projectionAndView);
        projectionViewModel.mul(model);
        // Update the shader
        sceneShader.setUniform("model", model);
        sceneShader.setUniform("projectionViewModel", projectionViewModel);
        sceneShader.setUniform("material", material);
    }
    public void allocateTextureUnitsToSceneShader(Texture texture, SceneLight sceneLight){
        int numPointLights = sceneLight.pointLights != null ? sceneLight.pointLights.size() : 0;
        int numSpotLights = sceneLight.spotLights != null ? sceneLight.spotLights.size() : 0;

        if (texture != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        for (int i = 0; i < numPointLights; i++) {
            // Static Shadows
            if (!sceneLight.pointLights.get(i).isDynamicOnly()) {
                glActiveTexture(GL_TEXTURE1 + (i * 2));
                glBindTexture(GL_TEXTURE_CUBE_MAP, sceneLight.pointLights.get(i).getStaticShadowMap().getDepthMap());
            } else {
                glActiveTexture(GL_TEXTURE1 + (i * 2));
                glBindTexture(GL_TEXTURE_CUBE_MAP, sceneLight.pointLights.get(i).getDynamicShadowMap().getDepthMap());
            }
            // Dynamic Shadows
            glActiveTexture(GL_TEXTURE1 + (i * 2) + 1);
            glBindTexture(GL_TEXTURE_CUBE_MAP, sceneLight.pointLights.get(i).getDynamicShadowMap().getDepthMap());
        }
        for (int i = 0; i < numSpotLights; i++) {
            // Static Shadows
            glActiveTexture(GL_TEXTURE1 + numPointLights * 2 + (i * 2));
            glBindTexture(GL_TEXTURE_2D, sceneLight.spotLights.get(i).getStaticShadowMap().getDepthMap());
            // Dynamic Shadows
            glActiveTexture(GL_TEXTURE1 + numPointLights * 2 + (i * 2) + 1);
            glBindTexture(GL_TEXTURE_2D, sceneLight.spotLights.get(i).getStaticShadowMap().getDepthMap());
        }
        if (sceneLight.directionalLight != null) {
            // Static Shadows
            if (!sceneLight.directionalLight.isDynamicOnly()) {
                glActiveTexture(GL_TEXTURE1 + numPointLights * 2 + numSpotLights * 2);
                glBindTexture(GL_TEXTURE_2D, sceneLight.directionalLight.getStaticShadowMap().getDepthMap());
            }
            // Dynamic Shadows
            glActiveTexture(GL_TEXTURE1 + numPointLights * 2 + numSpotLights * 2 + 1);
            glBindTexture(GL_TEXTURE_2D, sceneLight.directionalLight.getDynamicShadowMap().getDepthMap());
        }
    }
    public void setSceneShaderMode0(float step, Vector3f headPos){
        sceneShader.setUniform("mode", 0);
        sceneShader.setUniform("step", step);
        sceneShader.setUniform("headPos", headPos);
    }
    public void setSceneShaderMode1(Matrix4f[] jointTransforms) {
        sceneShader.setUniform("mode", 1);
        sceneShader.setUniform("jointTransforms", jointTransforms, jointTransforms.length);
    }
    public void setSceneShaderModeDefault(){
        sceneShader.setUniform("mode", 99);
    }
    public void unbindSceneShader(){
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        sceneShader.unbind();
    }

    //
    // Depth Shader management Functions
    //
    public void bindDepthMapShader(){
        depthShader.bind();
    }
    public void initializeDepthShader(Matrix4f lightSpaceMatrix){
        depthShader.setUniform("lightSpaceMatrix", lightSpaceMatrix);
    }
    public void updateDepthShader(Matrix4f model) {
        depthShader.setUniform("modelMatrix", model);
    }
    public void setDepthShaderMode0(float step, Vector3f headPos){
        depthShader.setUniform("mode", 0);
        depthShader.setUniform("step", step);
        depthShader.setUniform("headPos", headPos);
    }
    public void setDepthShaderModeDefault(){
        depthShader.setUniform("mode", 99);
    }
    public void unbindDepthMapShader(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        depthShader.unbind();
    }

    //
    // Depth Cube Shader management Functions
    //
    public void bindDepthCubeMapShader(){
        depthShaderCube.bind();
    }
    public void initializeDepthCubeMapShader(Matrix4f view, Vector3f pos, Vector2f plane) {
        depthShaderCube.setUniform("shadowMatrice", view);
        depthShaderCube.setUniform("lightPos", pos);
        depthShaderCube.setUniform("far_plane",plane.y);
    }
    public void updateDepthCubeMapShader(Matrix4f model) {
        depthShaderCube.setUniform("modelMatrix", model);
    }
    public void setDepthShaderCubeMode0(float step, Vector3f headPos){
        depthShaderCube.setUniform("mode", 0);
        depthShaderCube.setUniform("step", step);
        depthShaderCube.setUniform("headPos", headPos);
    }
    public void setDepthShaderCubeMode1(Matrix4f[] jointTransforms) {
        depthShaderCube.setUniform("mode", 1);
        depthShaderCube.setUniform("jointTransforms", jointTransforms, jointTransforms.length);
    }
    public void setDepthShaderCubeModeDefault(){
        depthShaderCube.setUniform("mode", 99);
    }
    public void unbindDepthCubeMapShader(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        depthShaderCube.unbind();
    }

    //
    // HDR Shader management Functions
    //
    public void bindHDRShader(){
        hdrShader.bind();
    }
    public void setHDRExposure(float exposure) {
        hdrShader.setUniform("exposure", exposure);
    }
    public void allocateTextureUnitsToHDRShader(int texture) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        hdrShader.setUniform("hdrTexture", 0);
    }
    public void unbindHDRShader(){
        hdrShader.unbind();
    }

    //
    // Terminate Method
    //
    public void terminate(){
        if (sceneShader != null) sceneShader.terminate();
        if (depthShader != null) depthShader.terminate();
        if (depthShaderCube != null) depthShaderCube.terminate();
    }
}
