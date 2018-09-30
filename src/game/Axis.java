package game;

import engine.entities.Entity;
import engine.loader.PLYLoader;
import graphics.Mesh;
import org.joml.Vector3f;

public class Axis  {

    private Mesh mesh = null;
    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;


    public Axis(Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        try {
            mesh = PLYLoader.loadMesh("/models/PLY/axis.ply");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void render() {
        mesh.render();
    }

}
