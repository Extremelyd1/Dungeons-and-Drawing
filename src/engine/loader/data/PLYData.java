package engine.loader.data;

/**
 * Record data type to hold PLY data
 */
public class PLYData {

    public float[] positions;
    public float[] normals;
    public float[] vertexColors;
    public int[] indicies;

    public PLYData(float[] positions, float[] normals, float[] vertexColors, int[] indicies) {
        this.positions = positions;
        this.normals = normals;
        this.vertexColors = vertexColors;
        this.indicies = indicies;
    }
}
