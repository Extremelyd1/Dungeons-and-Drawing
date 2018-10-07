package engine.gui;

import engine.MouseInput;
import org.joml.Vector2f;

/** Class to render a button the the GUI */
public class Button extends GUIComponent {

    private int width;
    private int height;
    private String text;
    private boolean hover;

    private static final RGBA BUTTON_COLOR = new RGBA(218, 191, 148);
    private static final RGBA BUTTON_COLOR_HOVER = new RGBA(156, 121, 79);
    private static final RGBA BUTTON_COLOR_TEXT = new RGBA(55, 50, 34);

    public Button(String text) {
        this(new Vector2f(0, 0), 1, 0, 200, 50, text);
    }

    public Button(int width, int height, String text) {
        this(new Vector2f(0, 0), 1, 0, width, height, text);
    }

    public Button(Vector2f position, float scale, float rotation, int width, int height, String text) {
        super(position, scale, rotation, BUTTON_COLOR);
        this.width = width;
        this.height = height;
        this.text = text;
        this.hover = false;
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.transform(this.getPosition(), this.getRotation(), this.getScale());

        if (hover) {
            nano.drawRectangle(new Vector2f(0, 0), width, height, BUTTON_COLOR_HOVER);
        } else {
            nano.drawRectangle(new Vector2f(0, 0), width, height, BUTTON_COLOR);
        }

        nano.drawText(new Vector2f(width / 2.0f, height / 2.0f),
                text, Font.SEGOE_UI_BOLD, BUTTON_COLOR_TEXT);
    }

    @Override
    public void update(MouseInput mouse) {

        // TODO Improve to take into account rotation and scale

        float xBoundLeft = this.getPosition().x;
        float xBoundRight = this.getPosition().x + width;
        float yBoundDown = this.getPosition().y;
        float yBoundUp = this.getPosition().y + height;

        float x = (float) mouse.getCurrentPos().x;
        float y = (float) mouse.getCurrentPos().y;

        hover = (x >= xBoundLeft && x <= xBoundRight && y >= yBoundDown && y <= yBoundUp);
    }
}
