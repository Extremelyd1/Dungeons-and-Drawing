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

    /**
     * The current component this GUI handles
     * Can only be one component
     */
    private List<GUIComponent> components;
    private List<GUIComponent> toRemove;

    public GUI() {
        this.components = new ArrayList<>();
        this.toRemove = new ArrayList<>();
    }

    /**
     * Gets the GUIComponent
     *
     * @return Current component
     */
    public List<GUIComponent> getComponents() {
        return components;
    }

    /**
     * Old hasComponent() method to check whether there is a component on the GUI
     * @return whether the GUI has a component
     */
    public boolean hasComponent() {
        return hasComponents();
    }

    /**
     * Gets whether the GUI has components
     *
     * @return whether the GUI has components
     */
    public boolean hasComponents() {
        return !components.isEmpty();
    }

    public void setComponent(GUIComponent component) {
        this.components.clear();
        this.components.add(component);
    }

    /**
     * Add a GUIComponent
     *
     * @param component the component to add
     */
    public void addComponent(GUIComponent component) {
        this.components.add(component);
    }

    /**
     * Old remove component method
     */
    public void removeComponent() {
        this.toRemove.addAll(components);
    }

    /**
     * Remove a specific component
     * @param component to remove
     */
    public void removeComponent(GUIComponent component) {
        this.toRemove.add(component);
    }

    /**
     * Initialises nano vg
     */
    public void initialize() {
    }

    /**
     * Updates the gui
     */
    public void update(float delta, MouseInput mouse) {
        mouse.input();

        for (GUIComponent component : toRemove) {
            components.remove(component);
        }
        toRemove.clear();

        for (GUIComponent component : components) {
            component.update(mouse, delta);
        }
    }

    /**
     * Renders all components
     */
    public void render() {
        if (!hasComponents()) {
            return;
        }
        NanoVG nano = NanoVG.getInstance();

        nano.createFrame();

        for (GUIComponent component : components) {
            component.render();
        }

        nano.terminateFrame();
    }

    /**
     * Free resources
     */
    public void terminate() {
        NanoVG.getInstance().terminateNanoVG();
    }
}
