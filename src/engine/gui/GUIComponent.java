package engine.gui;

import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all GUI component we would want to add. Defines a position, mesh, scale and rotation.
 *
 * As GUI graphics are 2D, the position and rotation is limited to the z-axis and to x and y coordinates.
 * Meshes are for now limited to Quads that are textured.
 *
 * GUI components are rendered in orthogonal projection (so the Z-axis is ignored)
 */
public class GUIComponent {

    private Mesh mesh; // Mesh to display the texture, often a quad
    private final Vector3f position; // Position of the GUI component in screen coordinates
    private float scale; // Scale of the GUI component
    private final Vector3f rotation; // Rotation (in degrees) of the GUI Component

    /** Constructs an empty Component with no mesh and default position, rotation, scale */
    public GUIComponent() { ;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = 1.0f;
    }

    /**
     * Constructs a component with a mesh and default position, rotation, scale
     * @param mesh mesh of the component
     */
    public GUIComponent(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    /**
     * Creates a GUI component with a different mesh and a custom position, rotation, scale
     * @param mesh Mesh of the GUI Component (probably a quad)
     * @param position Custom position (in screen coordinates) of the component
     * @param rotation Custom rotation (in degrees, around the z-axis) of the component
     */
    public GUIComponent(Mesh mesh, Vector2f position, float rotation) {
        this.position = new Vector3f(position, 0);
        this.rotation = new Vector3f(0, 0, rotation);
        this.scale = 1.0f;

        this.mesh = mesh;
    }

    /* Returns the position */
    public Vector3f getPosition() {
        return position;
    }

    /* Sets the position */
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    /* Returns the scale */
    public float getScale() {
        return scale;
    }

    /* Sets the scale */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /* Returns the rotation */
    public Vector3f getRotation() {
        return rotation;
    }

    /* Sets the rotation */
    public void setRotation(float amount) {
        this.rotation.z = amount;
    }

    /* Returns the mesh */
    public Mesh getMesh() {
        return mesh;
    }


    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

}
