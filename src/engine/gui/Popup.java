package engine.gui;

import engine.MouseInput;
import org.joml.Vector2f;

/**
 * Basic popup. Is centered by default and the height adjusts based on the text
 * that is to be displayed. Has a default width, but this can be changed
 */
public class Popup extends GUIComponent {

    private static final RGBA POPUP_COLOR = new RGBA(218, 191, 148);
    private static final RGBA POPUP_COLOR_DARK = new RGBA(156, 121, 79);
    private static final RGBA POPUP_COLOR_TEXT = new RGBA(55, 50, 34);

    private static final float POPUP_DEFAULT_WIDTH = 800;
    private static final float POPUP_MINIMUM_HEIGHT = 150;
    private static final float POPUP_TEXT_WIDTH = 0.75f * POPUP_DEFAULT_WIDTH;

    private String text;
    private float textHeight;

    /**
     * Constructs a popup with a custom width and no text
     * @param width the width of the popup in pixels
     */
    public Popup(float width) {
        this(width, null);
    }

    /**
     * Constructs a popup with the default width and some text
     * @param text text of the popup
     */
    public Popup(String text) {
        this(POPUP_DEFAULT_WIDTH, text);
    }

    /**
     * Constructs a popup with a custom width and some text
     * @param text text of the popup
     * @param width width of the popup
     */
    public Popup(float width, String text) {
        super();
        setComponentWidth(width);
        this.text = text;
        this.setCentered(true);

    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.transform(this.getPosition());

        // Base background of the popup
        nano.drawRectangle(new Vector2f(0, 0), getComponentWidth(),
                getComponentHeight(), POPUP_COLOR);

        // Add stroke
        nano.addStroke(5, POPUP_COLOR_DARK);

        if (text != null) {
            // Draw the text
            float posX = getComponentWidth() / 2.0f - 0.375f * getComponentWidth();
            float posY = getComponentHeight() / 2.0f - textHeight / 2.0f;
            nano.drawParagraphText(new Vector2f(posX, posY),
                    getComponentWidth() * 0.75f, text, POPUP_COLOR_TEXT);
        }
   }

    @Override
    public void update(MouseInput mouse) {
        NanoVG nano = NanoVG.getInstance();

        // Compute the height of the text
        if (text != null) {
            textHeight = nano.computeTextHeight(text, POPUP_TEXT_WIDTH);
            setComponentHeight(Math.max(textHeight + 70, POPUP_MINIMUM_HEIGHT));
        }

        // Center the object
        super.update(mouse);
    }
}
