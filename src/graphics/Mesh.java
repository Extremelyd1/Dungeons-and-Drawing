package graphics;

import graphics.Texture;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 *  Class that represents a mesh that can be rendered using OpenGL. Given the
 *  relevant data of its vertices (color, position, etc.), it creates the VBO
 *  and VAO needed to transfer the data to the GPU and render it. 
 * 
 *  <b> ABOUT VBOs and VAOs </b>
 *  <ul> 
 *      <li> A <b> VBO </b> (Vertex Buffer Object) is allocated memory on the 
 *      graphics card which we can use to send data to our OpenGL program. </li>
 *      <li> A <b> VAO </b> (Vertex Array Object) is a wrapper for one or multiple 
 *      VBOs that define attributes of an element we want to render. </li>
 *  </ul>
 * 
 *  <b> ABOUT INDEX BUFFERS </b>
 *  Take for example a squad of 4 vertices (A, B, C, D). To render this, one 
 *  could use two triangles (A, B, C and B, C, D). This however would result in
 *  unnecessary data storage as you're storing B and C twice. Therefore, index 
 *  buffers are used. We store all vertices and their attributes just once, but
 *  when drawing, use a list of indices of said vertices to determine 
 *  the order in which to draw them. 
 * 
 *  @author Cas Wognum (TU/e, 1012585)
 */
public class Mesh {

    private final int vaoId; // Vertex Array Object (VAO)
    private final List<Integer> vboIdList;
    private final int vertexCount; // Amount of vertices we are rendering
    private Material material;
    
    
    /**
     * Constructor for the Mesh class. Creates all VBOs and the VAO. 
     * 
     * @param positions floats that give the position of the vertices
     * @param textCoords textures to map the texture to the mesh
     * @param normals
     * @param indices   integers that give the order of how to draw the vertices
     */
    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        
        // FloatBuffers and IntBuffers need to be used to transfer data to the GPU
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        
        try {
                      
            vertexCount = indices.length;

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);
            
            vboIdList = new ArrayList();
            
            /* Structure of the creation of the VBOs is similar
             * -> Allocate memory off-heap: memAllocxxx()
             * -> Put the data in it: put()
             * -> Flip the pointer back to position 0: flip()
             * -> Explain the structure of the data: glVertexAtrribPointer()
             */
            
            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            
            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            // Bind the VAO
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
            
        } finally {
            
            // Free up the memory
            // Had to be done manually as the memory is allocated off-heap
            // Thus, the standard Java Garbage Collector won't clean it up
            
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
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
    public void render() {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
    }

    
    /**
     * Return the 'name' or identifier of the Vertex Array Object
     * @return {@code vaoId}  
     */
    public int getVaoId() {
        return vaoId;
    }

    /**
     * Return the amount of vertices in the mesh
     * @return {@code vertexcount}  
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /** Free up the resources */
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
}
