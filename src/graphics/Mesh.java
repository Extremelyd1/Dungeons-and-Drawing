package graphics;

import engine.GameEntity;
import engine.loader.animatedModelLoader.dataStructures.MeshData;
import engine.loader.data.OBJData;
import engine.loader.data.PLYData;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Class that represents a mesh that can be rendered using OpenGL. Given the
 * relevant data of its vertices (color, position, etc.), it creates the VBO
 * and VAO needed to transfer the data to the GPU and render it.
 *
 * <b> ABOUT VBOs and VAOs </b>
 * <ul>
 * <li> A <b> VBO </b> (Vertex Buffer Object) is allocated memory on the
 * graphics card which we can use to send data to our OpenGL program. </li>
 * <li> A <b> VAO </b> (Vertex Array Object) is a wrapper for one or multiple
 * VBOs that define attributes of an element we want to render. </li>
 * </ul>
 *
 * <b> ABOUT INDEX BUFFERS </b>
 * Take for example a squad of 4 vertices (A, B, C, D). To render this, one
 * could use two triangles (A, B, C and B, C, D). This however would result in
 * unnecessary data storage as you're storing B and C twice. Therefore, index
 * buffers are used. We store all vertices and their attributes just once, but
 * when drawing, use a list of indices of said vertices to determine
 * the order in which to draw them.
 *
 * <b> Something about indices </b>
 * The index used in calls such as {@code glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);}
 * correspond to memory addresses on the GPU. If create a buffer at some index, you must
 * use the same index to enable that particular buffer. The game will crash when you try to
 * enable a buffer with an index that was not created. These indices are also used in the
 * shader programs (location).
 *
 * @author Cas Wognum (TU/e, 1012585)
 */
public class Mesh {

    protected int vaoId; // Vertex Array Object (VAO)
    protected List<Integer> vboIdList;
    protected int vertexCount; // Amount of vertices we are rendering
    protected Material material;
    protected boolean isStatic = true;

    /**
     * Indicates whether vertex colors have been defined
     */
    private boolean hasVertexColors;
    /**
     * Indicates whether texture coordinates have been defined
     */
    private boolean hasTextureCoords;

    // Only used in AnimatedMesh
    protected Mesh(MeshData data) {
    }

    /**
     * Construct a new mesh using PLY data.
     *
     * @param plyData Data coming from a .ply file
     */
    public Mesh(PLYData plyData) {
        List<Buffer> buffers = new ArrayList<>();

        try {
            // Create VAO/VBO
            vertexCount = plyData.indicies.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Create buffers
            buffers.add(createVBO(plyData.positions, 0, 3));
            buffers.add(createVBO(plyData.vertexColors, 2, 3));
            buffers.add(createVBO(plyData.normals, 3, 3));
            buffers.add(createVBO(plyData.indicies));

            // Set appropriate flags
            hasVertexColors = true;
            hasTextureCoords = false;

            // Bind the VAO
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            // Remove off-heap memory since the garbage collector won't clean it up
            buffers.forEach(MemoryUtil::memFree);
        }
    }

    /**
     * Construct a new mesh using OBJ data.
     *
     * @param objData Data coming from a .obj file
     */
    public Mesh(OBJData objData) {
        List<Buffer> buffers = new ArrayList<>();

        try {
            // Create VAO/VBO
            vertexCount = objData.indicies.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Create buffers
            buffers.add(createVBO(objData.positions, 0, 3));
            buffers.add(createVBO(objData.textureCoords, 1, 2));
            buffers.add(createVBO(objData.normals, 3, 3));
            buffers.add(createVBO(objData.indicies));

            // Set appropriate flags
            hasVertexColors = false;
            hasTextureCoords = true;

            // Bind the VAO
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            // Remove off-heap memory since the garbage collector won't clean it up
            buffers.forEach(MemoryUtil::memFree);
        }
    }

    /**
     * Creates a buffer object to transfer data to the GPU
     *
     * @param data  data to transfer
     * @param index index of the VBO
     * @param size  size of each attribute (so vec3f has size 3)
     * @return handler for the FloatBuffer
     */
    protected FloatBuffer createVBO(float[] data, int index, int size) {
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
        return buffer;
    }

    /**
     * Creates a buffer object to transfer data to the GPU
     *
     * @param data data to transfer
     * @return handler for the IntBuffer
     */
    protected IntBuffer createVBO(int[] data) {
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        return buffer;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Renders the mesh
     */
    public void initRender() {
        if (material.isTextured()) {
            //Get the texture
            Texture texture = material.getTexture();
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        if (hasTextureCoords) {
            glEnableVertexAttribArray(1);
        }
        if (hasVertexColors) {
            glEnableVertexAttribArray(2);
        }
        glEnableVertexAttribArray(3);
    }

    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        endRender();
    }

    public void endRender() {
        // Restore state
        glDisableVertexAttribArray(0);
        if (hasTextureCoords) {
            glDisableVertexAttribArray(1);
        }
        if (hasVertexColors) {
            glDisableVertexAttribArray(2);
        }
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }


    /**
     * Return the 'name' or identifier of the Vertex Array Object
     *
     * @return {@code vaoId}
     */
    public int getVaoId() {
        return vaoId;
    }

    /**
     * Return the amount of vertices in the mesh
     *
     * @return {@code vertexcount}
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Free up the resources
     */
    public void terminate() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    /**
     * Needed for the level editor
     */
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
