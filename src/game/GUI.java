package game;

import engine.MouseInput;
import engine.gui.GUIComponent;
import engine.gui.NanoVG;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to test GUI components. Should be extended to dynamically load and remove
 * objects in a more structured way.
 */
public class GUI {

    private MouseInput mouse = new MouseInput();
    /**
     * The current component this GUI handles
     * Can only be one component
     */
    private GUIComponent component;
    private NanoVG nano;

    public GUI() {

    }

    /**
     * Gets the GUIComponent
     *
     * @return Current component
     */
    public GUIComponent getComponent() {
        return component;
    }

    /**
     * Gets whether the GUI has a component that is being rendered
     *
     * @return whether the GUI has a component
     */
    public boolean hasComponent() {
        return component != null;
    }

    /**
     * Changed the current GUIComponent
     *
     * @param component the component to change to
     */
    public void setComponent(GUIComponent component) {
        this.component = component;
    }

    public void removeComponent() {
        this.component = null;
    }

    /**
     * Initialises nano vg
     */
    public void initialize() {
        nano = NanoVG.getInstance();
        mouse.init();
    }

    /**
     * Updates the gui
     */
    public void update() {
        mouse.input();

        if (component != null) {
            component.update(mouse);
        }
    }

    /**
     * Renders all components
     */
    public void render() {
        if (component == null) {
            return;
        }
        nano.createFrame();

        component.render();

        nano.terminateFrame();
    }

    /**
     * Free resources
     */
    public void terminate() {
        nano.terminateNanoVG();
    }
}
