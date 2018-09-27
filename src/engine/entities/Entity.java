package engine.entities;

import graphics.Mesh;
import org.joml.Vector3f;

/**
 * Basic Entity class
 */
public class Entity {

    private final Mesh mesh;
    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;

    public Entity(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public Entity(Mesh mesh, Vector3f position) {
        this.mesh = mesh;
        this.position = position;
        rotation = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public Entity(Mesh mesh, Vector3f position, float scale) {
        this.mesh = mesh;
        this.position = position;
        rotation = new Vector3f(0, 0, 0);
        this.scale = scale;
    }

    public Entity(Mesh mesh, Vector3f position, Vector3f rotation) {
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
        scale = 1;
    }

    public Entity(Mesh mesh, Vector3f position, Vector3f rotation, float scale) {
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void update(float delta) {}

    public void render() {
        mesh.render();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
