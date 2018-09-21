package engine.loader;

import engine.util.Utilities;
import engine.loader.data.PLYData;
import graphics.Mesh;
import org.joml.Vector3f;

import java.util.ArrayList;
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
 * <li> A header that specifies the elements of the mesh and their types </li>
 * <li> The body which contains a list of said elements (e.g. vertices and faces) </li>
 * </ol>
 *
 * <h2> The header </h2>
 * <ol>
 * <li>The first line starts with {@code ply}</li>
 * <li>The second line indicates the version. For us, this should always be {@code format ascii 1.0}</li>
 * <li>In the header, a comment can be added by starting the line with {@code comment}</li>
 * <li>An element is introduced with the {@code element <name> <amount>} line. <amount> is the amount of elements
 * there are of this type and <name> is, as can be expected, the name of this type of element (e.g. 'vertex').</li>
 * <li>An property of an element is introduced with the {@code property <type> <name>} line. Where the <type> is the
 * type of the variable (either char, uchar, short, ushort, int, uint, float, or double) and <name> is the name of
 * the property. The {@code list <amount>} keyword can be used to indicate a list of <amount> values.</li>
 * <li>The header is ended with the {@code end_header} keyword</li>
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
     * Starts the parsing of a new .ply file. Opens the file as a list of strings where each String corresponds
     * to a line in the .ply file. Each line is then processed one by one and at the end the data is restructured
     * to be usable by the Mesh class.
     *
     * @param fileName the path to the .ply file to parse
     * @return a Mesh object that stores the information of .ply format
     * @throws Exception if file not found
     * @throws Exception if file format not supported
     */
    public static Mesh loadMesh(String fileName) throws Exception {

        // Open the file as a list of strings
        List<String> lines = Utilities.readAllLines(fileName);

        // Stores the data
        List<Face> faces = new ArrayList<>();
        List<Vertex> vertices = new ArrayList<>();

        // Check if the file format is correct
        int endHeader = lines.indexOf("end_header") + 1; // exclusive index

        if (endHeader == 0) {
            throw new Exception("PLYLoader.loadMesh() failed: Unsupported file format. " +
                    "'end_header' keyword is missing");
        }

        List<String> header = new ArrayList<>(lines.subList(0, endHeader));
        List<String> body = new ArrayList<>(lines.subList(endHeader, lines.size()));

        // Check the header and query the amount of vertices and faces
        int[] amounts = checkFileFormat(header);

        // Parse all vertices
        for (int i = 0; i < amounts[0]; i++) {
            vertices.add(parseVertex(body.get(i), i));
        }

        // Parse all faces
        for (int i = amounts[0]; i < amounts[0] + amounts[1]; i++) {
            faces.add(parseFace(body.get(i)));
        }

        // Restructure the data and create the Mesh object
        return reorderLists(faces, vertices);
    }

    /**
     * Checks the file format to see if it is a format that is currently supported. It checks if the specifications
     * in the header coincide with the specifications we expect. Check is far from perfect, but helps in debugging.
     * Limitations; Correctness of properties is not checked and the amount of properties per element neither is
     * checked.
     *
     * @param header The lines that make up the header
     * @return two integers that contain the number of vertices and faces
     */
    private static int[] checkFileFormat(List<String> header) throws Exception {
        int[] amounts = new int[2];
        int numberOfProperties = 0;

        for (String line : header) {
            String[] tokens = line.split("\\s+");

            switch (tokens[0]) {
                case "ply":
                case "comment":
                case "end_header":
                    break;
                case "format":
                    if (!tokens[1].equals("ascii")) {
                        throw new Exception("PLYLoader.checkFileFormat() failed: Unsupported file format. " +
                                "Not the ASCII format, but " + tokens[1]);
                    }
                    break;
                case "element":
                    if (tokens[1].equals("vertex")) {
                        amounts[0] = Integer.parseInt(tokens[2]);
                    } else if (tokens[1].equals("face")) {
                        amounts[1] = Integer.parseInt(tokens[2]);
                    } else {
                        throw new Exception("PLYLoader.checkFileFormat() failed: Unsupported file format. " +
                                "Unsupported element " + tokens[1]);
                    }
                    break;
                case "property":
                    numberOfProperties++;
                    break;
                default:
                    throw new Exception("PLYLoader.checkFileFormat() failed: Unsupported file format. " +
                            "Unsupported keyword " + tokens[0]);
            }
        }

        if (numberOfProperties != 10) {
            throw new Exception("PLYLoader.checkFileFormat() failed: Unsupported file format. " +
                    "Wrong number of properties " + numberOfProperties);
        }

        return amounts;
    }

    /**
     * Reorders the data we gathered from the .ply file such that the Mesh class understands it
     *
     * @param facesList    A list of faces that were parsed from the file
     * @param verticesList A list of vertices that were parsed from the file
     * @return A mesh with the data from the .ply file
     */
    private static Mesh reorderLists(List<Face> facesList, List<Vertex> verticesList) {

        int noOfVertices = verticesList.size();
        int noOfFaces = facesList.size();

        float[] positions = new float[noOfVertices * 3];
        float[] normals = new float[noOfVertices * 3];
        float[] colors = new float[noOfVertices * 3];
        List<Integer> indicesList = new ArrayList<>();

        int vertex = 0;
        for (Vertex v : verticesList) {
            // Have to switch the x and y coordinate as Blender interprets
            // the axes differently than we do.
            positions[vertex * 3] = v.getPositionData().x;
            positions[vertex * 3 + 2] = v.getPositionData().y; // actually our z
            positions[vertex * 3 + 1] = v.getPositionData().z; // actually our y

            normals[vertex * 3] = v.getNormalData().x;
            normals[vertex * 3 + 1] = v.getNormalData().y;
            normals[vertex * 3 + 2] = v.getNormalData().z;

            colors[vertex * 3] = v.getColorData().x;
            colors[vertex * 3 + 1] = v.getColorData().y;
            colors[vertex * 3 + 2] = v.getColorData().z;
            vertex++;
        }

        for (Face f : facesList) {
            for (int index : f.getIndices()) {
                indicesList.add(index);
            }
        }

        int[] indices = indicesList.stream().mapToInt((Integer v) -> v).toArray();

        return new Mesh(new PLYData(positions, normals, colors, indices));
    }

    /**
     * Parses a line that contains the information of a vertex object
     *
     * @param line the line all the data is on
     * @return Vertex object
     */
    private static Vertex parseVertex(String line, int lineNumber) {

        String[] tokens = line.split("\\s");

        // Position vector data
        Vector3f pos = new Vector3f(
                Float.parseFloat(tokens[0]),
                Float.parseFloat(tokens[1]),
                Float.parseFloat(tokens[2])
        );

        // Normal vector data
        Vector3f norm = new Vector3f(
                Float.parseFloat(tokens[3]),
                Float.parseFloat(tokens[4]),
                Float.parseFloat(tokens[5])
        );

        // Color data
        Vector3f col = new Vector3f(
                Float.parseFloat(tokens[6]) / 255,
                Float.parseFloat(tokens[7]) / 255,
                Float.parseFloat(tokens[8]) / 255
        );

        return new Vertex(pos, norm, col, lineNumber);
    }

    /**
     * Parses a line that contains the information of a face object
     *
     * @param line the line all the data is on
     * @return Face object
     */
    private static Face parseFace(String line) throws Exception {
        String[] tokens = line.split("\\s");

        int noOfVertices = Integer.parseInt(tokens[0]);
        int[] indices = new int[noOfVertices];

        for (int i = 0; i < noOfVertices; i++) {
            indices[i] = Integer.parseInt(tokens[i + 1]);
        }

        return new Face(indices);

    }

    /**
     * Stores the information of a .ply line that defines a face. It consists of a list of indices that point to
     * vertices that, if connected, form the face.
     */
    private static class Face {
        int[] indices;

        private Face(int[] idx) throws Exception {
            if (idx.length > 4) {
                throw new Exception("PLYLoader.loadMesh().Face(): Polygon of length > 4");
            } else if (idx.length == 3) {
                this.indices = idx;
            } else {
                this.indices = new int[6];
                this.indices[0] = idx[0];
                this.indices[1] = idx[1];
                this.indices[2] = idx[2];
                this.indices[3] = idx[3];
                this.indices[4] = idx[2];
                this.indices[5] = idx[0];

            }
        }

        private int[] getIndices() {
            return indices;
        }

    }

    /**
     * Stores the information of a .ply line that defines a vertex. Contains rgb color data, the position vector,
     * the normal vector and an identifier
     */
    private static class Vertex {
        Vector3f positionData;
        Vector3f normalData;
        Vector3f colorData;
        int id;

        private Vertex(Vector3f pos, Vector3f norm, Vector3f col, int id) {
            this.colorData = col;
            this.normalData = norm;
            this.positionData = pos;
            this.id = id;
        }

        private Vector3f getPositionData() {
            return positionData;
        }

        private Vector3f getNormalData() {
            return normalData;
        }

        private Vector3f getColorData() {
            return colorData;
        }
    }

}
