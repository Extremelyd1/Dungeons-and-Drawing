package engine.gui;

import engine.GameWindow;
import engine.loader.data.OBJData;
import graphics.Material;
import graphics.Mesh;
import graphics.Texture;

/**
 * Class to render a simple popup to the screen. The popup only has text.
 * SimplePopup is the parent of the Text object. Text is centered in the center
 * of the popup.
 */
public class SimplePopup extends GUIComponentParent {

    private float width;
    private float height;

    public SimplePopup(Text text, int width, int height) throws Exception{
        super();

        this.height = height;
        this.width = width;

        setMesh(buildMesh(width, height));
        addChild(text);
        text.setPosition(width / 2.0f - text.getWidth() / 2.0f,
                height / 2.0f - text.getHeight() / 2.0f);

    }

    /**
     * Creates the Mesh for the Popup background
     * @return Mesh object
     */
    private Mesh buildMesh(int width, int height) throws Exception {

        // Normals are left empty as we do not want the light to have an influence
        float[] normals = new float[0];

        // Vertex position are based on the width and height
        float[] vertices = new float[] {
                0, 0, 0,
                0, height, 0,
                width, 0, 0,
                width, height, 0,
        };

        // Texture coordinates
        float[] textureCoordinates = new float[] {
                0.5f, 0.5f,
                0.5f, 1,
                1, 0.5f,
                1, 1,
        };

        // Indices array
        int[] indices = new int[] {0, 1, 2, 1, 2, 3};

        // Create the mesh and add the texture
        Mesh mesh = new Mesh(new OBJData(vertices, normals, textureCoordinates, indices));
        mesh.setMaterial(new Material(new Texture("/textures/grassblock.png")));

        return mesh;
    }

    public void center() {
        float x = GameWindow.getGameWindow().getWindowWidth() / 2.0f - width / 2.0f;
        float y = GameWindow.getGameWindow().getWindowHeight() / 2.0f - height / 2.0f;
        setPosition(x, y);
    }


}
