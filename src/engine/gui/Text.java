package engine.gui;

import engine.loader.data.OBJData;
import engine.util.Utilities;
import graphics.FontTexture;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class to render text.
 *
 * This class is capable of rendering 2D text to the screen. It is still limited to rendering just
 * a single line and a single color per text. The text has to be supplied as a String.
 *
 * To render the text, a special text texture is generated, see {@link FontTexture}, which is basically
 * a very long image with all the characters from the character set we use. In the {@code buildMesh()} method
 * we then generate the QUADS we need to map the texture to for each character by using properties like
 * the width of the character.
 */
public class Text extends GUIComponent {

    // Can be any constant as it is ignored (because of orthogonal projection)
    private static final float ZPOS = 0.0f;

    // Defines the amount of vertices per QUAD and we have one QUAD per character
    private static final int VERTICES_PER_QUAD = 4;

    // Texture with all the characters
    private final FontTexture fontTexture;

    // The text to be rendered
    private String text;

    // For better positioning
    private float width;
    private float height;

    /**
     * Constructor that defines most of the components properties on creation
     *
     * @param text Text to be rendered
     * @param fontTexture Font (texture with all characters)
     * @param color The color of the text
     * @param x The x-coordinate (in screen coordinates)
     * @param y The y-coordinate (in screen coordinates)
     * @throws Exception if texture can not be generated
     */
    public Text(String text, FontTexture fontTexture, Vector3f color, float x, float y) throws Exception {
        this(text, fontTexture);
        this.setColor(color);
        this.setPosition(x, y);
    }

    /**
     * Constructor that defines most text properties, but leaves the position as the default
     *
     * @param text Text to be rendered
     * @param fontTexture Font (texture with all characters)
     * @param color The color of the text
     * @throws Exception if texture can not be generated
     */
    public Text(String text, FontTexture fontTexture, Vector3f color) throws Exception {
        this(text, fontTexture);
        this.setColor(color);
    }

    /**
     * Constructor that defines the font and text content, but leaves the position and color
     * as the default
     *
     * @param text Text to be rendered
     * @param fontTexture Font (texture with all characters)
     * @throws Exception if texture can not be generated
     */
    public Text(String text, FontTexture fontTexture) throws Exception {
        super();
        this.text = text;
        this.fontTexture = fontTexture;
        setMesh(buildMesh());
        this.setColor(new Vector3f(1, 1, 1));
    }

    /**
     * Builds a mesh based on a string. In general, it converts the string to an array of characters.
     * For each character, it then defines a quad (Tile) by adding vertices, texture coordinates and indices.
     * The normals are not defined as we do not want the light to influence the graphics. All quads
     * are 'placed' next to each other to generate our mesh.
     *
     * @return Mesh to render the text on
     */
    private Mesh buildMesh() {
        List<Float> positions = new ArrayList<>(); // Vertex positions
        List<Float> textCoords = new ArrayList<>(); // Vertex texture coordinates
        List<Integer> indices   = new ArrayList<>(); // Face indices

        float[] normals   = new float[0]; // Normals are left blank (so no influence of lighting)

        char[] characters = text.toCharArray(); // Convert string to char array
        int numChars = characters.length; // Get the number of characters

        float startXPosition = 0; // start x of current position.

        // Loop through all characters in the string
        for(int i = 0; i < numChars; i++) {

            // Get the info of the character (width, startx, etc.)
            FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

            // For each of characters, create a Tile that consists of two Triangles

            // Left Top Vertex
            positions.add(startXPosition); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add( (float)charInfo.getStartX() / (float)fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD);

            // Left Bottom vertex
            positions.add(startXPosition); // x
            positions.add((float)fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float)charInfo.getStartX() / (float)fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add(startXPosition + charInfo.getWidth()); // x
            positions.add((float)fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add(startXPosition + charInfo.getWidth()); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD + 3);

            // Add indices for left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);

            // Update position for next character
            startXPosition += charInfo.getWidth();
        }

        height = fontTexture.getHeight();
        width = startXPosition;

        // Convert the lists to arrays so it can be used to create a Mesh
        float[] posArr = Utilities.listToArray(positions);
        float[] textCoordsArr = Utilities.listToArray(textCoords);
        int[] indicesArr = indices.stream().mapToInt(i->i).toArray();

        // Create the mesh and set the material
        Mesh mesh = new Mesh(new OBJData(posArr, normals, textCoordsArr, indicesArr));
        mesh.setMaterial(new Material(fontTexture.getTexture()));
        return mesh;
    }

    /* Get the content text */
    public String getText() {
        return text;
    }

    /* Update the content text */
    public void setText(String text) {
        this.text = text;
        this.getMesh().deleteBuffers();
        this.setMesh(buildMesh());
    }

    /* Update the color */
    public void setColor(Vector3f color) {
        this.getMesh().getMaterial().setAmbientColour(new Vector4f(color, 1.0f));
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
