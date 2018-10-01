package engine.gui;

import engine.loader.data.OBJData;
import graphics.Material;
import graphics.Mesh;
import graphics.Texture;

import java.util.List;

/**
 * Class to render a simple popup to the screen. The popup only has text.
 */
public class PopupWindow extends GUIComponentParent {

    /**
     *
     */
    public PopupWindow(Text text, int width, int height) throws Exception{
        super();
        setMesh(buildMesh(width, height));
        addChild(text);
    }

    /**
     *
     * @return
     */
    private Mesh buildMesh(int width, int height) throws Exception {

        float[] normals = new float[0];
        float[] vertices = new float[] {
                0, 0, 0,
                0, height, 0,
                width, 0, 0,
                width, height, 0,
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
