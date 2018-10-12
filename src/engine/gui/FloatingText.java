package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.animation.Animator;
import game.action.Action;
import org.joml.Vector2f;

/**
 * Basic popup. Is centered by default and the height adjusts based on the text
 * that is to be displayed. Has a default width, but this can be changed
 */
public class FloatingText extends GUIComponent {

    protected static final RGBA POPUP_COLOR_TEXT = new RGBA(255, 255, 255);

    protected static final float POPUP_DEFAULT_WIDTH = 800;
    protected static final float POPUP_MINIMUM_HEIGHT = 150;
    protected static final float POPUP_TEXT_WIDTH = 0.75f * POPUP_DEFAULT_WIDTH;

    protected String text;
    protected float textHeight;

    /**
     * Constructs a popup with the default width and some text
     * @param text text of the popup
     */
    public FloatingText(String text) {
        this(POPUP_DEFAULT_WIDTH, text);
    }

    /**
     * Constructs a popup with a custom width and some text
     * @param text text of the popup
     * @param width width of the popup
     */
    public FloatingText(float width, String text) {
        super();
        setComponentWidth(width);
        this.text = text;
        this.setCentered(false);
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.transform(this.getPosition());

        if (text != null) {
            GameWindow window = GameWindow.getGameWindow();
            // Draw the text
            float posX = window.getWindowWidth() / 2f - getComponentWidth() / 2.0f;
            float posY = window.getWindowHeight() - getComponentHeight() * 1.2f - textHeight / 2.0f;
            nano.drawHintText(new Vector2f(posX, posY), getComponentWidth(), text, POPUP_COLOR_TEXT);
        }
   }

    @Override
    public void update(MouseInput mouse, float delta) {
        NanoVG nano = NanoVG.getInstance();

        // Compute the height of the text
        if (text != null) {
            textHeight = nano.computeTextHeight(text, POPUP_TEXT_WIDTH);
            setComponentHeight(Math.max(textHeight + 70, POPUP_MINIMUM_HEIGHT));
        }

        // Center the object
        super.update(mouse, delta);
    }
}
