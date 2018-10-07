package engine.gui;

import engine.GameWindow;
import engine.MouseInput;

public class Popup extends GUIComponent {

    private final float width;
    private final float height;
    private String text;

    private float[] ornament;
    private float[] ornamentVertical;

    public Popup(float width, float height, String text) {
        super();

        float x = GameWindow.getGameWindow().getWindowWidth() / 2.0f - width / 2.0f;
        float y = GameWindow.getGameWindow().getWindowHeight() / 2.0f - height / 2.0f;
        this.setPosition(x, y);

        this.width = width;
        this.height = height;
        this.text = text;

        ornament = new float[] {
                0, 0, 20, 0, // Point 1 + control point out
                20, 20, 0, 20, -40, 20, //  Control point in, Point 2, Control point out
                -40, -20, 0, -20, 60, -20, // Control point in, Point 3, control point out
                40, 20, 100, 20, 140, 20, // Control point in, Point 4, control point out
                140, -20, 100, -20, 80, -20, // Control point in, Point 5, control point out
                80, 0, 100, 0 // control point in + Point 6
        };

        ornamentVertical = new float[] {
                0, 0, 0, 20, // Point 1 + control point out
                20, 20, 20, 0, 20, -40, //  Control point in, Point 2, Control point out
                -20, -40, -20, 0, -20, 60, // Control point in, Point 3, control point out
                20, 40, 20, 100, 20, 140, // Control point in, Point 4, control point out
                -20, 140, -20, 100, -20, 80, // Control point in, Point 5, control point out
                0, 80, 0, 100 // control point in + Point 6
        };
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.drawRectangle(getPosition().x, getPosition().y, width, height,
                new RGBA(160, 82, 45));

        nano.drawDonut(this.getPosition().x + width - 20, this.getPosition().y + 20,
                5, new RGBA(255, 255, 255, 128));

        nano.drawDonut(this.getPosition().x + 20, this.getPosition().y + height - 20,
                5, new RGBA(255, 255, 255, 128));

        nano.drawDonut(this.getPosition().x + width - 20, this.getPosition().y + height - 20,
                5, new RGBA(255, 255, 255, 128));

        nano.drawDonut(this.getPosition().x + 20, this.getPosition().y + 20,
                5, new RGBA(255, 255, 255, 128));

        nano.drawCustomShape(ornament, this.getPosition().x + 40,
                this.getPosition().y + 20, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawCustomShape(ornament, this.getPosition().x + 40,
                this.getPosition().y + height - 20, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawCustomShape(ornament, this.getPosition().x + width - 60,
                this.getPosition().y + 20, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawCustomShape(ornament, this.getPosition().x + width - 60,
                this.getPosition().y + height - 20, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawCustomShape(ornamentVertical, this.getPosition().x + width - 20,
                this.getPosition().y + 40, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawCustomShape(ornamentVertical, this.getPosition().x + width - 20,
                this.getPosition().y + height - 60, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawCustomShape(ornamentVertical, this.getPosition().x + 20,
                this.getPosition().y + 40, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawCustomShape(ornamentVertical, this.getPosition().x + 20,
                this.getPosition().y + height - 60, 0.2f, new RGBA(255, 255, 255, 128),
                true, false);

        nano.drawParagraphText(this.getPosition().x + 120,
                this.getPosition().y + height / 4.0f, width * 0.75f, text, null);
    }

    @Override
    public void update(MouseInput mouse) {

    }
}
