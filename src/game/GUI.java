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
    private List<GUIComponent> components;
    private NanoVG nano;

    public GUI() {
        components = new ArrayList<>();
    }

    /**
     * Exposes the components list to mutate.
     *
     * @return Component list
     */
    public List<GUIComponent> getComponents() {
        return components;
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

        for (GUIComponent c : components) {
            c.update(mouse);
        }
    }

    /**
     * Renders all components
     */
    public void render() {
        nano.createFrame();

        for (GUIComponent c : components) {
            c.render();
        }

        nano.terminateFrame();
    }

    /**
     * Free resources
     */
    public void terminate() {
        nano.terminateNanoVG();
    }
}
