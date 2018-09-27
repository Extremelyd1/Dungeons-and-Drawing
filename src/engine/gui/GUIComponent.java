package engine.gui;

import graphics.Mesh;
import org.joml.Vector3f;

public class GUIComponent {

    private Mesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public GUIComponent() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = 1.0f;
    }

    public GUIComponent(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float amount) {
        this.rotation.z = amount;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
