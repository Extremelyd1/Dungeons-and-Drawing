package game;

import engine.MouseInput;
import engine.gui.*;
import org.joml.Vector2f;

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

    public void initialize() {
        nano = NanoVG.getInstance();
        mouse.init();
    }

    public void update() {
        mouse.input();

        for (GUIComponent c : components) {
            c.update(mouse);
        }
    }

    public void render() {
        nano.createFrame();

        for (GUIComponent c : components) {
            c.render();
        }

        nano.terminateFrame();
    }

    public void terminate() {
        nano.terminateNanoVG();
    }

}
