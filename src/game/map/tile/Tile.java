package game.map.tile;

import graphics.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Tile {

    /**
     * Position in map coordinates
     */
    private Vector2i position;
    /**
     * The rotation of the tile
     */
    private Vector3f rotation;
    /**
     * The actual model of the tile
     */
    private Mesh mesh;
    /**
     * Solid tiles cannot be walked on by the player
     */
    private boolean solid;

    public Tile(Vector2i position, Vector3f rotation, Mesh mesh, boolean solid) {
        this.position = position;
        this.rotation = rotation;
        this.mesh = mesh;
        this.solid = solid;
    }

    public Tile(Vector2i position, Mesh mesh, boolean solid) {
        this(position, new Vector3f(0, 0, 0), mesh, solid);
    }

    public Tile(Vector2i position, Mesh mesh) {
        this(position, new Vector3f(0, 0, 0), mesh, true);
    }

    public Vector2i getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }
}
