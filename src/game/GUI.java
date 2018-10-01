package game;

import engine.GameWindow;
import engine.gui.GUIComponent;
import engine.gui.Layer;
import engine.gui.PopupWindow;
import engine.gui.Text;
import graphics.FontTexture;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to test GUI components. Should be extended to dynamically load and remove
 * objects in a more structured way.
 */
public class GUI {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private final Text text;

    private List<Layer> layers;

    public GUI(String initText) throws Exception {

        layers = new ArrayList<>();

        Layer layer1 = Layer.getLayer(0);
        Layer layer2 = Layer.getLayer(1);

        FontTexture fontTexture = new FontTexture(FONT, CHARSET);

        text = new Text(initText, fontTexture, new Vector3f(0.25f, 0.2f, 0.2f));
        PopupWindow pop = new PopupWindow(text, 300, 100);

        layer1.add(pop);
        layer2.add(text);

        layers.add(layer1);
        layers.add(layer2);

    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void updateSize() {
        this.text.setPosition(10f, GameWindow.getGameWindow().getWindowHeight() - 50f);
    }

    public void terminate() {
        for (Layer layer : layers) {
            for (GUIComponent c : layer.getElements()) {
                c.getMesh().terminate();
            }
        }
    }

    public List<Layer> getLayers() {
        return layers;
    }
}
