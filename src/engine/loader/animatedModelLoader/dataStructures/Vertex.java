package engine.loader.animatedModelLoader.dataStructures;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private static final int NO_INDEX = -1;

    private Vector3f position;
    private int colorIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private Vertex duplicateVertex = null;
    private int index;
    private float length;
    private List<Vector3f> tangents = new ArrayList<Vector3f>();
    private Vector3f averagedTangent = new Vector3f(0, 0, 0);


    private VertexSkinData weightsData;

    public Vertex(int index, Vector3f position, VertexSkinData weightsData) {
        this.index = index;
        this.weightsData = weightsData;
        this.position = position;
        this.length = position.length();
    }

    public VertexSkinData getWeightsData() {
        return weightsData;
    }

    public void addTangent(Vector3f tangent) {
        tangents.add(tangent);
    }

    public void averageTangents() {
        if (tangents.isEmpty()) {
            return;
        }
        for (Vector3f tangent : tangents) {
            averagedTangent.add(tangent);
        }
        averagedTangent.normalize();
    }

    public Vector3f getAverageTangent() {
        return averagedTangent;
    }

    public int getIndex() {
        return index;
    }

    public float getLength() {
        return length;
    }

    public boolean isSet() {
        return colorIndex != NO_INDEX && normalIndex != NO_INDEX;
    }

    public boolean hasSameColorAndNormal(int colorIndexOther, int normalIndexOther) {
        return colorIndexOther == colorIndex && normalIndexOther == normalIndex;
    }

    public void setColorIndex(int textureIndex) {
        this.colorIndex = textureIndex;
    }

    public void setNormalIndex(int normalIndex) {
        this.normalIndex = normalIndex;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public int getNormalIndex() {
        return normalIndex;
    }

    public Vertex getDuplicateVertex() {
        return duplicateVertex;
    }

    public void setDuplicateVertex(Vertex duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }

}
