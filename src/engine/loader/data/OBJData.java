package engine.loader.data;

/**
 * Record data type to hold OBJ data
 */
public class OBJData {

    public float[] positions;
    public float[] normals;
    public float[] textureCoords;
    public int[] indicies;

    public OBJData(float[] positions, float[] normals, float[] textureCoords, int[] indicies) {
        this.positions = positions;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.indicies = indicies;
    }
}
