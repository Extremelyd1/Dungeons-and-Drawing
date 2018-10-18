package game.map.tile;

import graphics.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;

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
    /**
     * Tags associated with this tile
     */
    private ArrayList<String> tags;

    public Tile(Vector2i position, Vector3f rotation, Mesh mesh, boolean solid) {
        this(position, rotation, mesh, solid, new ArrayList<>());
    }

    public Tile(Vector2i position, Mesh mesh, boolean solid) {
        this(position, new Vector3f(0, 0, 0), mesh, solid, new ArrayList<>());
    }

    public Tile(Vector2i position, Mesh mesh) {
        this(position, new Vector3f(0, 0, 0), mesh, true, new ArrayList<>());
    }

    public Tile(Vector2i position, Vector3f rotation, Mesh mesh, boolean solid, ArrayList<String> tags) {
        this.position = position;
        this.rotation = rotation;
        this.mesh = mesh;
        this.solid = solid;
        this.tags = tags;
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

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }

}
