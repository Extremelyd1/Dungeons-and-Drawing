package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.util.Utilities;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Popup with the drawing canvas. The drawing canvas is not centered by default. The drawing
 * made by the user is stored as a List of lists of floats. Each sublist of floats stores
 * a subpart of the drawing. Each point of a drawing is defined by two floats (the x and y)
 * which are stored next to each other in the sublist
 */
public class GUIImage extends GUIComponent {

    private int imageHandle;

    /**
     * Constructs an image to be drawn on the GUI
     */
    public GUIImage(float width, float height, String path) {
        super();
        setCentered(true);
        setComponentWidth(width);
        setComponentHeight(height);
        imageHandle = NanoVG.getInstance().createImage(path);
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        // Transform the 2D components
        nano.transform(this.getPosition());

        nano.drawImage(new Vector2f(0, 0), getComponentWidth(), getComponentHeight(), imageHandle);
    }

}
