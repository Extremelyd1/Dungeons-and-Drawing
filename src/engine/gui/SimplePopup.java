package engine.gui;

import engine.loader.data.OBJData;
import graphics.Material;
import graphics.Mesh;
import graphics.Texture;

import java.util.List;

/**
 * Class to render a simple popup to the screen. The popup only has text.
 */
public class SimplePopup extends GUIComponent {

    private Text text;

    /**
     *
     */
    public SimplePopup(Text text, List<Button> buttons) throws Exception{
        super();
        setMesh(buildMesh(text));
    }

    /**
     *
     * @return
     */
    private Mesh buildMesh(Text text) throws Exception {

        float[] normals = new float[0];
        float[] vertices = new float[] {
                0, 0, 0,
                0, 50, 0,
                300, 0, 0,
                300, 50, 0,
        };

        float[] textureCoordinates = new float[] {
                0.5f, 0.5f,
                0.5f, 1,
                1, 0.5f,
                1, 1,
        };

        int[] indices = new int[] {0, 1, 2, 1, 2, 3};

        Mesh mesh = new Mesh(new OBJData(vertices, normals, textureCoordinates, indices));
        mesh.setMaterial(new Material(new Texture("/textures/grassblock.png")));

        return mesh;
    }
}
