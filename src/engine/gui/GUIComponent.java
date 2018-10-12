package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

/**
 * Base class for all GUI component we would want to add.
 */
public abstract class GUIComponent {

    private final Vector2f position; // Position of the GUI component in screen coordinates
    private float componentHeight;
    private float componentWidth;
    private boolean isCentered;

    /** Constructs an empty Component with the default position */
    public GUIComponent() {
        this(new Vector2f(0,0));
    }

    /** Constructs a component with a specified position */
    public GUIComponent(Vector2f position) {
        this.position = position;
    }

    /**
     * Centers an object. Assumes the origin of said object is located at the upper-left
     * corner of the component, which is the default for rectangles, but not for circles,
     * custom shapes and text for example.
     */
    private void center() {
        GameWindow window = GameWindow.getGameWindow();
        float x = window.getWindowWidth() / 2.0f - getComponentWidth() / 2.0f;
        float y = window.getWindowHeight() / 2.0f - getComponentHeight() / 2.0f;
        this.setPosition(x, y);
    }

    /**
     *  Centers the object in the middle of the screen. Assumes the origin is in
     *  the upper-left corner of the guicomponent
     */
    public void update(MouseInput mouse, float delta) {
        if (isCentered()) {
            center();
        }
    }

    /**
     * Renders all components to the screen
     */
    abstract public void render();


    // ====== GETTERS AND SETTERS ====== //

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public float getComponentHeight() {
        return componentHeight;
    }

    public void setComponentHeight(float componentHeight) {
        this.componentHeight = componentHeight;
    }

    public float getComponentWidth() {
        return componentWidth;
    }

    public void setComponentWidth(float componentWidth) {
        this.componentWidth = componentWidth;
    }

    public boolean isCentered() {
        return isCentered;
    }

    public void setCentered(boolean centered) {
        isCentered = centered;
    }
}
