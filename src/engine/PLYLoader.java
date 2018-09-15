package engine;

import graphics.Mesh;
import org.joml.Vector3f;

import java.util.List;

/**
 * <h1> Introduction </h1>
 * Class to load 3D models (meshes) that have been stored in the Poligon File Format (.ply). The advantage
 * of PLY over for example OBJ is that PLY by default also contains vertex color information. For the
 * Dungeons and Drawings game, this makes it easier to maintain a low poly style. After either finding
 * or creating a low-poly model in Blender, the model can be colored using Blenders vertex-paint mode.
 * All the vertex data (texture coordinates, color data, position, etc.) can then be exported to a
 * .ply file.
 *
 * <h1> The file format </h1>
 * The .ply format is a relatively simple, text-based (ASCII) format. There is also a binary version,
 * but this is for now not supported. The file is organised in two parts.
 *
 * <ol>
 *     <li> A header that specifies the elements of the mesh and their types </li>
 *     <li> The body which contains a list of said elements (e.g. vertices and faces) </li>
 * </ol>
 *
 * <h2> The header </h2>
 * <ol>
 *     <li>The first line starts with {@code ply}</li>
 *     <li>The second line indicates the version. For us, this should always be {@code format ascii 1.0}</li>
 *     <li>In the header, a comment can be added by starting the line with {@code comment}</li>
 *     <li>An element is introduced with the {@code element <name> <amount>} line. <amount> is the amount of elements
 *     there are of this type and <name> is, as can be expected, the name of this type of element (e.g. 'vertex').</li>
 *     <li>An property of an element is introduced with the {@code property <type> <name>} line. Where the <type> is the
 *     type of the variable (either char, uchar, short, ushort, int, uint, float, or double) and <name> is the name of
 *     the property. The {@code list <amount>} keyword can be used to indicate a list of <amount> values.</li>
 *     <li>The header is ended with the {@code end_header} keyword</li>
 * </ol>
 *
 * <h2> The body </h2>
 * The body then lists the elements in the order they are introduced in the header. Each element has its own line which
 * consists of the values for all properties separated by a space.
 *
 * <h1> Limitations </h1>
 * For now, the file format we can read is constrained to the following:
 * Object vertex = (float[3] position, float[3] normal, uchar[3] rgb_color)
 * Object face = (list<int> vertext_indices)
 */
public class PLYLoader {

    /**
     * Starts the parsing of a new .ply file
     *
     * @param filename the path to the .ply file to parse
     * @return a Mesh object that stores the information of .ply format
     * @throws Exception if file not found
     * @throws Exception if file format not supported
     */
    public static Mesh loadMesh(String filename) throws Exception {}

    /**
     * Checks the file format to see if it is a format that is currently supported. It checks if the specifications
     * in the header coincide with the specifications we expect.
     *
     * @param header The lines that make up the header
     */
    private static checkFileFormat(String[] header) {}

    /**
     * Reorders the data we gathered from the .ply file such that the Mesh class understands it
     *
     * @param facesList A list of faces that were parsed from the file
     * @param verticesList A list of vertices that were parsed from the file
     * @return A mesh with the data from the .ply file
     */
    private static Mesh reorderLists(List<Face> facesList, List<Vertex> verticesList) {}

    /**
     * Parses a line that contains the information of a vertex object
     * @return Vertex object
     */
    private static Vertex parseVertex() {}

    /**
     * Parses a line that contains the information of a face object
     * @return Face object
     */
    private static Face parseFace() {}

    /**
     * Stores the information of a .ply line that defines a face. It consists of a list of indices that point to
     * vertices that, if connected, form the face.
     */
    private static class Face {
        int[] indices;

        public Face(int i) {}

    }

    /**
     * Stores the information of a .ply line that defines a vertex. Contains rgb color data, the position vector,
     * the normal vector and an identifier
     */
    private static class Vertex {
        Vector3f positionData;
        Vector3f normalData;
        Vector3f colorData;
        int vertexId;

        public Vertex(Vector3f pos, Vector3f norm, Vector3f col, int id) {}

    }

}
