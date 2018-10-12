package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import game.action.Action;
import org.joml.Vector2f;

/**
 * Basic popup. Is centered by default and the height adjusts based on the text
 * that is to be displayed. Has a default width, but this can be changed
 */
public class FloatingText extends GUIComponent {

    static final RGBA POPUP_COLOR_TEXT = new RGBA(255, 255, 255);

    private static final float POPUP_DEFAULT_WIDTH = 800;
    private static final float POPUP_MINIMUM_HEIGHT = 150;
    private static final float POPUP_TEXT_WIDTH = 0.75f * POPUP_DEFAULT_WIDTH;

    private String text;
    private float textHeight;

    private Action action;

    /**
     * Constructs a popup with the default width and some text
     * @param text text of the popup
     */
    public FloatingText(String text, Action action) {
        this(POPUP_DEFAULT_WIDTH, text, action);
    }

    /**
     * Constructs a popup with a custom width and some text
     * @param text text of the popup
     * @param width width of the popup
     * @param action the action to execute when user closes the popup
     */
    public FloatingText(float width, String text, Action action) {
        super();
        setComponentWidth(width);
        this.text = text;
        this.setCentered(false);
        this.action = action;

    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.transform(this.getPosition());

        if (text != null) {
            // Draw the text
            nano.drawHintText(getPosition(),
                    getComponentWidth(), text, POPUP_COLOR_TEXT);
        }
   }

    @Override
    public void update(MouseInput mouse) {
        NanoVG nano = NanoVG.getInstance();

        // Compute the height of the text
        if (text != null) {
            GameWindow window = GameWindow.getGameWindow();

            textHeight = nano.computeTextHeight(text, POPUP_TEXT_WIDTH);
            setComponentHeight(Math.max(textHeight + 70, POPUP_MINIMUM_HEIGHT));

            float posX = window.getWindowWidth() / 2f - getComponentWidth() / 2.0f;
            float posY = window.getWindowHeight() - getComponentHeight() * 1.2f - textHeight / 2.0f;
            setPosition(posX, posY);
        }

        if (mouse.isLeftButtonPressed() && action != null) {
            action.execute();
        }

        // Center the object
        super.update(mouse);
    }
}
