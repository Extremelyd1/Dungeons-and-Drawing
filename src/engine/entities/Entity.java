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
    private final Vector3f scale;

    public Entity(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
    }

    public Entity(Mesh mesh, Vector3f position) {
        this.mesh = mesh;
        this.position = position;
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
    }

    public Entity(Mesh mesh, Vector3f position, float scale) {
        this.mesh = mesh;
        this.position = position;
        rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(scale, scale, scale);
    }

    public Entity(Mesh mesh, Vector3f position, Vector3f rotation) {
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
        scale = new Vector3f(1, 1, 1);
    }

    public Entity(Mesh mesh, Vector3f position, Vector3f rotation, float scale) {
        this.mesh = mesh;
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector3f(scale, scale, scale);
    }

    public void update(float delta) {}

    public void render() {
        mesh.render();
    }

    public void setPosition(Vector3f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.x = rotation.x;
        this.rotation.y = rotation.y;
        this.rotation.z = rotation.z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setScale(Vector3f scale) {
        this.scale.x = scale.x;
        this.scale.y = scale.y;
        this.scale.z = scale.z;
    }

    public void setScale(float scale) {
        this.scale.x = scale;
        this.scale.y = scale;
        this.scale.z = scale;
    }

    public float getScale() {
        return scale.x;
    }

    public Vector3f getScaleVector() {
        return scale;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
