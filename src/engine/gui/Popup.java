package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import org.joml.Vector2f;

public class Popup extends GUIComponent {

    private static final RGBA POPUP_COLOR = new RGBA(218, 191, 148);
    private static final RGBA POPUP_COLOR_DARK = new RGBA(156, 121, 79);
    private static final RGBA POPUP_COLOR_TEXT = new RGBA(55, 50, 34);

    private static final float DEFAULT_WIDTH = 800;
    private static final float DEFAULT_HEIGHT = 200;

    private final float width;
    private float height;
    private String text;

    private float[] ornament;
    private float[] ornamentVertical;

    public Popup(String text) {
        this(DEFAULT_WIDTH, text);
    }

    public Popup(float width, String text) {
        super();

        float x = GameWindow.getGameWindow().getWindowWidth() / 2.0f - width / 2.0f;
        float y = GameWindow.getGameWindow().getWindowHeight() / 2.0f - height / 2.0f;
        this.setPosition(x, y);

        this.width = width;
        this.height = DEFAULT_HEIGHT;
        this.text = text;

        ornament = new float[] {
                40, 20, 40, 20, // Point 1 + control point out
                60, 40, 40, 40, 0, 40, //  Control point in, Point 2, Control point out
                0, 0, 40, 0, 100, 0, // Control point in, Point 3, control point out
                80, 40, 140, 40, 180, 40, // Control point in, Point 4, control point out
                180, 0, 140, 0, 120, 0, // Control point in, Point 5, control point out
                120, 20, 140, 20 // control point in + Point 6
        };

        ornamentVertical = new float[] {
                20, 40, 20, 40, // Point 1 + control point out
                40, 60, 40, 40, 40, 0, //  Control point in, Point 2, Control point out
                0, 0, 0, 40, 0, 100, // Control point in, Point 3, control point out
                40, 80, 40, 140, 40, 180, // Control point in, Point 4, control point out
                0, 180, 0, 140, 0, 120, // Control point in, Point 5, control point out
                20, 120, 20, 140 // control point in + Point 6
        };
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.transform(this.getPosition());

        float textHeight = 0;

        if (text != null) {
            textHeight = nano.computeTextHeight(text, width * 0.75f);
            float padding = 100f;
            height = textHeight + padding;
        }

        // Base background of the popup
        nano.drawRectangle(new Vector2f(0, 0), width, height, POPUP_COLOR);
        nano.addStroke(5, POPUP_COLOR_DARK);

        nano.drawCircle(new Vector2f(20, 20), 5, POPUP_COLOR_DARK);
        nano.drawCircle(new Vector2f(width - 20, 20), 5, POPUP_COLOR_DARK);
        nano.drawCircle(new Vector2f(width - 20, height - 20), 5, POPUP_COLOR_DARK);
        nano.drawCircle(new Vector2f(20, height - 20), 5, POPUP_COLOR_DARK);

        // Draw horizontal ornaments
        nano.drawCustomShape(ornament, new Vector2f(40, 15), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);
        nano.drawCustomShape(ornament, new Vector2f(width - 76, 15), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);
        nano.drawCustomShape(ornament, new Vector2f(40, height - 23), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);
        nano.drawCustomShape(ornament, new Vector2f(width - 76, height - 23), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);

        // Draw vertical ornaments
        nano.drawCustomShape(ornamentVertical, new Vector2f(15, 40), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);
        nano.drawCustomShape(ornamentVertical, new Vector2f(width - 23, 40), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);
        nano.drawCustomShape(ornamentVertical, new Vector2f(15, height - 76), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);
        nano.drawCustomShape(ornamentVertical, new Vector2f(width - 23, height - 76), 0.2f,
                POPUP_COLOR_DARK, true, false, 2);

        if (text != null) {
            // Draw the text
            nano.drawParagraphText(new Vector2f(width / 2.0f - width * 0.375f, height / 2.0f - textHeight),
                    width * 0.75f, text, POPUP_COLOR_TEXT);
        }
   }

    @Override
    public void update(MouseInput mouse) {
        float x = GameWindow.getGameWindow().getWindowWidth() / 2.0f - width / 2.0f;
        float y = GameWindow.getGameWindow().getWindowHeight() / 2.0f - height / 2.0f;
        this.setPosition(x, y);
    }
}
