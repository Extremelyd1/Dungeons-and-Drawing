package engine.gui;

import engine.MouseInput;
import game.action.Action;
import org.joml.Vector2f;

/**
 * Class to render a button the the GUI. A button can detect when the mouse is hovering over
 * it, when it is clicked and has an associated action that is executed when clicked. By
 * default, a button is not centered.
 */
public class Button extends GUIComponent {

    private String text; // The text on the button
    private boolean hover; // Whether the mouse is hovering over the button
    private final Action action; // The Action to execute when clicked

    // Defaults
    private static final RGBA BUTTON_COLOR = new RGBA(218, 191, 148);
    private static final RGBA BUTTON_COLOR_HOVER = new RGBA(156, 121, 79);
    private static final RGBA BUTTON_COLOR_TEXT = new RGBA(55, 50, 34);
    private static final float BUTTON_DEFAULT_WIDTH = 200;
    private static final float BUTTON_DEFAULT_HEIGHT = 50;

    /**
     * Constructs a button with the default width and height, at (0,0) and with a custom
     * text and action.
     *
     * @param text Text on the button
     * @param action Action to execute
     */
    public Button(String text, Action action) {
        this(BUTTON_DEFAULT_WIDTH, BUTTON_DEFAULT_HEIGHT, text, action);
    }

    /**
     * Constructs a button with a custom width and height, at (0,0) and with a custom
     * text and action.
     *
     * @param width Width of the button
     * @param height Height of the button
     * @param text Text on the button
     * @param action Action to execute
     */
    public Button(float width, float height, String text, Action action) {
        this(new Vector2f(0, 0), width, height, text, action);
    }

    /**
     * Constructs a button for which all properties are customly defined
     * @param position position of the button
     * @param width width of the button
     * @param height height of the button
     * @param text text of the button
     * @param action action to execute when clicked
     */
    public Button(Vector2f position, float width, float height, String text, Action action) {
        super(position);
        this.setComponentHeight(height);
        this.setComponentWidth(width);
        this.text = text;
        this.hover = false;
        this.action = action;

        setComponentHeight(height);
    }

    @Override
    public void update(MouseInput mouse) {

        // Compute the bounds of the button object
        float xBoundLeft = this.getPosition().x;
        float xBoundRight = this.getPosition().x + getComponentWidth();
        float yBoundDown = this.getPosition().y;
        float yBoundUp = this.getPosition().y + getComponentHeight();

        // Get the position of the mouse
        float x = (float) mouse.getCurrentPos().x;
        float y = (float) mouse.getCurrentPos().y;

        // Check whether the mouse is on the button
        hover = (x >= xBoundLeft && x <= xBoundRight && y >= yBoundDown && y <= yBoundUp);

        // If button is clicked, execute the associated action
        if (hover && mouse.isLeftButtonPressed()) {
            action.execute();
        }

        // Handles the centering of the object if needed
        super.update(mouse);

    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        // Move the popup based on the position
        nano.transform(this.getPosition());

        // Draw the button (origin at upper left corner)
        if (hover) {
            // Mouse is hovering over the button, use the hover color
            nano.drawRectangle(new Vector2f(0, 0), getComponentWidth(),
                    getComponentHeight(), BUTTON_COLOR_HOVER);
        } else {
            // Mouse is not hovering over the button, use the normal color
            nano.drawRectangle(new Vector2f(0, 0), getComponentWidth(),
                    getComponentHeight(), BUTTON_COLOR);
        }

        // Draw (single line of) text (origin at center of text)
        nano.drawText(new Vector2f(getComponentWidth() / 2.0f, getComponentHeight()/ 2.0f),
                text, Font.SEGOE_UI_BOLD, BUTTON_COLOR_TEXT);
    }
}
