package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import org.joml.Vector2f;

import java.util.List;

public class Menu extends GUIComponent{

    private static final float MENU_BAR_HEIGHT = 40f;
    private static final RGBA MENU_BAR_COLOR = new RGBA(0, 0, 0, 128);

    private List<Button> buttons;

    public Menu(List<Button> buttons) {
        this.buttons = buttons;
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        // Draw menu bar
        nano.drawRectangle(new Vector2f(0), GameWindow.getGameWindow().getWindowWidth(),
                MENU_BAR_HEIGHT, MENU_BAR_COLOR);
    }

    @Override
    public void update(MouseInput mouse) {

    }

}
