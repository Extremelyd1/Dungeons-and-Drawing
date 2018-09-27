package game;

import engine.GameWindow;
import engine.gui.GUIComponent;
import engine.gui.Text;
import graphics.FontTexture;
import org.joml.Vector4f;

import java.awt.*;

/**
 * Class to test GUI components. Should be extended to dynamically load and remove
 * objects in a more structured way.
 */
public class GUI {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private final GUIComponent[] components;

    private final Text text;

    public GUI(String initText) throws Exception {

        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.text = new Text(initText, fontTexture, new Vector4f(1, 0, 0, 0));

        // Create list that holds the items that compose the HUD
        components = new GUIComponent[]{this.text};

    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public GUIComponent[] getGUIComponents() {
        return components;
    }

    public void updateSize() {
        this.text.setPosition(10f, GameWindow.getGameWindow().getWindowHeight() - 50f);
    }

    public void terminate() {
        for (GUIComponent c : getGUIComponents()) {
            c.getMesh().terminate();
        }
    }
}
