package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.util.Utilities;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Popup with the drawing canvas
 */
public class DrawingCanvas extends Popup {

    private float size;
    private boolean mouseDown;
    private int currentList;
    private List<List<Float>> drawing;
    private static final float BRUSH_SIZE = 5.0f;

    public DrawingCanvas() {
        super(GameWindow.getGameWindow().getWindowHeight() * 0.75f,
                GameWindow.getGameWindow().getWindowHeight() * 0.75f,
                "");

        this.size = GameWindow.getGameWindow().getWindowHeight() * 0.75f;
        this.drawing = new ArrayList<>();
        this.mouseDown = false;
        this.currentList = -1;
    }

    @Override
    public void update(MouseInput mouse) {

        // Compute the bounds of the canvas
        float padding = 10f;
        float xBoundLeft = this.getPosition().x + 0.075f * size + padding;
        float xBoundRight = this.getPosition().x + 0.925f * size - padding;
        float yBoundDown = this.getPosition().y + 0.925f * size - padding;
        float yBoundUp = this.getPosition().y + 0.075f * size + padding;

        // Listen to what is being drawn
        if (mouse.isLeftButtonPressed()) {

            // If mouse was not pressed before, add a new subdrawing
            if (!mouseDown) {
                mouseDown = true;
                List<Float> subDrawing = new ArrayList<>();
                drawing.add(subDrawing);
                currentList++;
            }

            // Get the x and y coordinate of the mouse
            float x = (float) mouse.getCurrentPos().x;
            float y = (float) mouse.getCurrentPos().y;

            // Check if the (x, y) is within the canvas
            if (x <= xBoundRight && x >= xBoundLeft && y <= yBoundDown && y >= yBoundUp) {
                drawing.get(currentList).add(x);
                drawing.get(currentList).add(y);
            } else {
                mouseDown = false;
            }
        } else {
            // Mouse released or mouse outside canvas
            if (mouseDown) {
                mouseDown = false;
            }
        }

        this.setPosition(GameWindow.getGameWindow().getWindowWidth() / 2.0f - size / 2.0f,
                GameWindow.getGameWindow().getWindowHeight() / 2.0f - size / 2.0f);
    }

    @Override
    public void render() {
        super.render();

        NanoVG nano = NanoVG.getInstance();
        nano.drawSquare(new Vector2f(0.075f * size, 0.075f * size),
                size * 0.85f, null);

        // Draw as lines
        for (int i = 0; i < drawing.size(); i++) {
            List<Float> subDrawing = drawing.get(i);
            if (subDrawing.size() >= 4) {
                nano.drawCustomShape(Utilities.listToArray(subDrawing),
                        new Vector2f(-this.getPosition().x, -this.getPosition().y),
                        1.0f, new RGBA(0, 0, 0, 255),
                        false, false, size / 28f);
            }
        }

        // Draw as dots
//        for (int i = 0; i < drawing.size(); i += 2) {
//            nano.drawCircle(drawing.get(i), drawing.get(i + 1),
//                   BRUSH_SIZE, new RGBA(0, 0, 0, 255));
//        }

    }

}
