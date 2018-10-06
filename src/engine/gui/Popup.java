package engine.gui;

import engine.GameWindow;

public class Popup extends GUIComponent {

    private final int width;
    private final int height;

    private float[] ornament;

    public Popup(int width, int height) {
        super();

        float x = GameWindow.getGameWindow().getWindowWidth() / 2.0f - width / 2.0f;
        float y = GameWindow.getGameWindow().getWindowHeight() / 2.0f - height / 2.0f;
        this.setPosition(x, y);

        this.width = width;
        this.height = height;

        ornament = new float[] {
            // TODO
        };
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.drawRectangle(getPosition().x, getPosition().y, width, height,
                new RGBA(160, 82, 45));

        nano.drawCircle(this.getPosition().x + width - 20, this.getPosition().y + 20,
                5, new RGBA(139, 69, 19));

        nano.drawCircle(this.getPosition().x + 20, this.getPosition().y + height - 20,
                5, new RGBA(139, 69, 19));

        nano.drawCircle(this.getPosition().x + width - 20, this.getPosition().y + height - 20,
                5, new RGBA(139, 69, 19));

        nano.drawCircle(this.getPosition().x + 20, this.getPosition().y + 20,
                5, new RGBA(139, 69, 19));
    }

    @Override
    public void update() {

    }
}
