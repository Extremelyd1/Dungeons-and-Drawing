package graphics;

import engine.loader.animatedModelLoader.dataStructures.MeshData;
import engine.loader.data.OBJData;
import engine.loader.data.PLYData;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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
public class AnimatedMesh extends Mesh {

    /**
     * Construct a new mesh using PLY data.
     *
     * @param meshData Data coming from a .dae file
     */
    public AnimatedMesh(MeshData meshData) {
        super(meshData);
        List<Buffer> buffers = new ArrayList<>();

        try {
            // Create VAO/VBO
            vertexCount = meshData.getIndices().length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Create buffers
            buffers.add(createVBO(meshData.getVertices(), 0, 3));
            buffers.add(createVBO(meshData.getColors(), 2, 3));
            buffers.add(createVBO(meshData.getNormals(), 3, 3));
            buffers.add(createIntVBO(meshData.getJointIds(), 4, 3));
            buffers.add(createVBO(meshData.getVertexWeights(), 5, 3));
            buffers.add(createVBO(meshData.getIndices()));

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
    private IntBuffer createIntVBO(int[] data, int index, int size) {
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
        return buffer;
    }

    /**
     * Renders the mesh
     */
    public void initRender() {
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
        glEnableVertexAttribArray(5);
    }

    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        endRender();
    }

    public void endRender() {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glDisableVertexAttribArray(5);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
