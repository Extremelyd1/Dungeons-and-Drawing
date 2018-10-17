package engine.loader.animatedModelLoader.dataStructures;

/**
 * This object contains all the mesh data for an animated model that is to be loaded into the VAO.
 * 
 * @author Karl
 *
 */
public class MeshData {

	private static final int DIMENSIONS = 3;

	private float[] vertices;
	private float[] colors;
	private float[] normals;
	private int[] indices;
	private int[] jointIds;
	private float[] vertexWeights;

	public MeshData(float[] vertices, float[] colors, float[] normals, int[] indices,
			int[] jointIds, float[] vertexWeights) {
		this.vertices = vertices;
		this.colors = colors;
		this.normals = normals;
		this.indices = indices;
		this.jointIds = jointIds;
		this.vertexWeights = vertexWeights;
	}

	public int[] getJointIds() {
		return jointIds;
	}
	
	public float[] getVertexWeights(){
		return vertexWeights;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getColors() {
		return colors;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVertexCount() {
		return vertices.length / DIMENSIONS;
	}

}
