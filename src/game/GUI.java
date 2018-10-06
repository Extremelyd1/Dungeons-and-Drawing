package game;

import engine.gui.GUIComponent;
import engine.gui.NanoVG;
import engine.gui.Popup;
import engine.gui.TitleCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to test GUI components. Should be extended to dynamically load and remove
 * objects in a more structured way.
 */
public class GUI {

    private List<GUIComponent> components;
    private NanoVG nano;

    public GUI() {
        components = new ArrayList<>();
    }

    public void initialize() {
        nano = NanoVG.getInstance();
        components.add(new Popup(900, 175));
        components.add(new TitleCard());
    }

    public void update() {
        for (GUIComponent c : components) {
            c.update();
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
