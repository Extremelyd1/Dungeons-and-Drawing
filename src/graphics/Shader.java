package graphics;

import engine.GameEngine;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Cas Wognum (TU/e, 1012585)
 */
public class Shader {
    
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private int geometryShaderId;
    
    // Holds all uniforms used in the shaders
    private final Map<String, Integer> uniforms;

    public Shader() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("engine.Shader constructor: "
                    + "Could not create shader");
        }
        uniforms = new HashMap<>();
    }

    /* Specify which vertex shader this shader should use */
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }
    
    /* Specify which fragement shader this shader should use */
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    /* Specify which geometry shader this shader should use */
    public void createGeometryShader(String shaderCode) throws Exception {
        geometryShaderId = createShader(shaderCode, GL_GEOMETRY_SHADER);
    }

    /* Create, compile and attach the shader given its id and type */
    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("engine.Shader.createShader():"
                    + "Error creating shader of type " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("engine.Shader.createShader():"
                    + "Error compiling shader code: " + 
                    glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    /* Link and verify the program with all compiled shaders */
    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("engine.Shader.link(): "
                    + "Error linking shader code: " + 
                    glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        if (geometryShaderId != 0) {
            glDetachShader(programId, geometryShaderId);
        }
        
        if (GameEngine.DEBUG_MODE) {
            glValidateProgram(programId);
            if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
                System.err.println("engine.Shader.link(): "
                        + "Warning validating shader code: " + 
                        glGetProgramInfoLog(programId, 1024));
            }
        }

    }

    /* Create, compile and attach the shader given its id and type */
    public void bind() {
        glUseProgram(programId);
    }

    /* Create, compile and attach the shader given its id and type */
    public void unbind() {
        glUseProgram(0);
    }
    
    /**
     * Updates the HashMap so that the identifier of a certain uniform used
     * in the vertex shader or fragment shader is matched to the name of that
     * uniform
     * 
     * @param uniformName name of the uniform as used in .vs or .fs
     * @throws Exception if a uniform with that name can not be found
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId,
            uniformName);
        if (uniformLocation < 0) {
            throw new Exception("engine.Shader.createUniform(): "
                    + "Could not find uniform " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }
    
    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
        createUniform(uniformName + ".shadowMap");
        createUniform(uniformName + ".plane");
    }

    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
        createUniform(uniformName + ".shadowMap");
        createUniform(uniformName + ".conedir");
        createUniform(uniformName + ".cutoff");
        createUniform(uniformName + ".outerCutoff");
        createUniform(uniformName + ".lightSpaceMatrix");
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".shadowMap");
        createUniform(uniformName + ".lightSpaceMatrix");
        createUniform(uniformName + ".shadowEnable");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".isColored");
        createUniform(uniformName + ".reflectance");
    }
    
    /**
     * Stores the data of a matrix in a uniform that can be used in the shaders
     * 
     * @param uniformName the name of the uniform in the .vs or .fs
     * @param data the data to store in the uniform
     */
    public void setUniform(String uniformName, Matrix4f data) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            data.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }
    
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, boolean value) {
        glUniform1i(uniforms.get(uniformName), value ? 1 : 0);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector2f value) {
        glUniform2f(uniforms.get(uniformName), value.x, value.y);
    }
    
    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }
    
    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, Matrix4f[] data, int size) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = BufferUtils.createFloatBuffer(16 * size);
            for (int i = 0; i < size; i++) {
                data[i].get(16 * i, fb);
            }
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }
    
    public void setUniform(String uniformName, PointLight[] pointLights) {
        int numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(uniformName, pointLights[i], i);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", pointLight);
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColor());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.getConstant());
        setUniform(uniformName + ".att.linear", att.getLinear());
        setUniform(uniformName + ".att.exponent", att.getExponent());
        setUniform(uniformName + ".plane", pointLight.getPlane());
    }

    public void setUniform(String uniformName, SpotLight[] spotLights) {
        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(uniformName, spotLights[i], i);
        }
    }

    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".colour", spotLight.getColor());
        setUniform(uniformName + ".position", spotLight.getPosition());
        setUniform(uniformName + ".intensity", spotLight.getIntensity());
        PointLight.Attenuation att = spotLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.getConstant());
        setUniform(uniformName + ".att.linear", att.getLinear());
        setUniform(uniformName + ".att.exponent", att.getExponent());
        setUniform(uniformName + ".conedir", spotLight.getConeDirection());
        setUniform(uniformName + ".cutoff", spotLight.getCutOff());
        setUniform(uniformName + ".outerCutoff", spotLight.getOuterCutOff());
        setUniform(uniformName + ".lightSpaceMatrix", spotLight.getLightSpaceMatrix());
    }

    public void setUniform(String uniformName, DirectionalLight dirLight) {
        setUniform(uniformName + ".colour", dirLight.getColor());
        setUniform(uniformName + ".direction", dirLight.getDirection());
        setUniform(uniformName + ".intensity", dirLight.getIntensity());
        setUniform(uniformName + ".lightSpaceMatrix", dirLight.getLightSpaceMatrix());
        setUniform(uniformName + ".shadowEnable", dirLight.isShadowEnabled());
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.getAmbientColour());
        setUniform(uniformName + ".diffuse", material.getDiffuseColour());
        setUniform(uniformName + ".specular", material.getSpecularColour());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".isColored", material.isColored() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    
    public void terminate() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
