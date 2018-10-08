package engine.gui;

import engine.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Base class for all GUI component we would want to add. Defines a position, mesh, scale and rotation.
 *
 * As GUI graphics are 2D, the position and rotation is limited to the z-axis and to x and y coordinates.
 * Meshes are for now limited to Quads that are textured.
 *
 * GUI components are rendered in orthogonal projection (so the Z-axis is ignored)
 */
public abstract class GUIComponent {

    private final Vector2f position; // Position of the GUI component in screen coordinates
    private RGBA color;

    /** Constructs an empty Component with no mesh and default position, rotation, scale */
    public GUIComponent() {
        this.position = new Vector2f(0, 0);
        this.color = new RGBA(255, 255, 255, 255);
    }

    /**
     * Constructs a component with a color
     * @param rgba color in rgba channels
     */
    public GUIComponent(RGBA rgba) {
        this();
        this.color = rgba;
    }

    /**
     * Constructs a fully defined component
     * @param rgba color in rgba channels
     */
    public GUIComponent(Vector2f position, RGBA rgba) {
        this.position = position;
        this.color = rgba;
    }

    /* Returns the position */
    public Vector2f getPosition() {
        return position;
    }

    /* Sets the position */
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public void setColor(RGBA rgba) {
        this.color = rgba;
    }

    public RGBA getColor() {
        return color;
    }

    abstract public void render();
    abstract public void update(MouseInput mouse);
}
