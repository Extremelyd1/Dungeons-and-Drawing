package game;

import engine.MouseInput;
import engine.gui.*;

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
        components.add(new Menu(null));
        components.add(new Popup("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. "));
//        components.add(new Button("Crash the game!", null));

        Popup p = new Popup();
        components.add(p);
        components.remove(p);

//        TitleCard titleCard = new TitleCard();
//        titleCard.setPosition(650, 100);
//        components.add(titleCard);

//        components.add(new DrawingCanvas());
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
