package engine.gui;

import engine.MouseInput;

/** Class to render a button the the GUI */
public class Button extends GUIComponent {

    private int width;
    private int height;
    private String text;
    private RGBA hoverColor;
    private boolean hover;

    public Button(int width, int height, String text, RGBA color, RGBA hoverColor) {
        super(color);
        this.width = width;
        this.height = height;
        this.text = text;
        this.hoverColor = hoverColor;
        this.hover = false;
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        if (hover) {
            nano.drawRectangle(this.getPosition().x, this.getPosition().y, width, height, hoverColor);
        } else {
            nano.drawRectangle(this.getPosition().x, this.getPosition().y, width, height, getColor());
        }

        nano.drawText(this.getPosition().x + width / 2.0f,
                this.getPosition().y + height / 2.0f - 12.0f, text, Font.SEGOE_UI_BOLD, null);
    }

    @Override
    public void update(MouseInput mouse) {
        float xBoundLeft = this.getPosition().x;
        float xBoundRight = this.getPosition().x + width;
        float yBoundDown = this.getPosition().y;
        float yBoundUp = this.getPosition().y + height;

        float x = (float) mouse.getCurrentPos().x;
        float y = (float) mouse.getCurrentPos().y;

        hover = (x >= xBoundLeft && x <= xBoundRight && y >= yBoundDown && y <= yBoundUp);
    }
}
