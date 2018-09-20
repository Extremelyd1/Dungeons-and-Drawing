package game.map.tile;

import graphics.Mesh;
import org.joml.Vector3f;

public class Tile {

    /**
     * The rotation of the tile
     */
    private Vector3f rotation;
    /**
     * The scale of the tile
     */
    private float scale;
    /**
     * The actual model of the tile
     */
    private Mesh mesh;

    public Tile(Vector3f rotation, float scale, Mesh mesh) {
        this.rotation = rotation;
        this.scale = scale;
        this.mesh = mesh;
    }

    public Tile(Mesh mesh) {
        this(new Vector3f(0, 0, 0), 1, mesh);
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
