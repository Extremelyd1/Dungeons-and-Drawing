package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.util.Utilities;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Popup with the drawing canvas. The drawing canvas is centered by default. The drawing
 * made by the user is stored as a List of lists of floats. Each sublist of floats stores
 * a subpart of the drawing. Each point of a drawing is defined by two floats (the x and y)
 * which are stored next to each other in the sublist
 */
public class DrawingCanvas extends Popup {

    private final float canvasSize;
    private boolean mouseDown;
    private int currentList;
    private List<List<Float>> drawing;

    /**
     * Constructs the drawing canvas
     */
    public DrawingCanvas() {

        // Canvas is by default centered and has a default size
        super(GameWindow.getGameWindow().getWindowHeight() * 0.75f);
        this.setCentered(true);

        // Stores the canvas size
        this.canvasSize = GameWindow.getGameWindow().getWindowHeight() * 0.75f;
        setComponentHeight(this.canvasSize);
        setComponentWidth(this.canvasSize);

        // Used for drawing on the canvas
        this.drawing = new ArrayList<>();
        this.currentList = -1;
        this.mouseDown = false;
    }

    @Override
    public void update(MouseInput mouse) {

        // Listen to what is being drawn
        if (mouse.isLeftButtonPressed()) {

            // Compute the bounds of the canvas
            float padding = 10f;
            float xBoundLeft = this.getPosition().x + 0.075f * canvasSize + padding;
            float xBoundRight = this.getPosition().x + 0.925f * canvasSize - padding;
            float yBoundDown = this.getPosition().y + 0.925f * canvasSize - padding;
            float yBoundUp = this.getPosition().y + 0.075f * canvasSize + padding;

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
                drawing.get(currentList).add(x - getPosition().x);
                drawing.get(currentList).add(y - getPosition().y);
            } else {
                mouseDown = false;
            }
        } else {
            // Mouse released or mouse outside canvas
            if (mouseDown) {
                mouseDown = false;
            }
        }

        // Center the canvas
        super.update(mouse);
    }

    @Override
    public void render() {

        NanoVG nano = NanoVG.getInstance();

        // Transform the 2D components
        nano.transform(this.getPosition());

        // Render the popup
        super.render();

        // Render the canvas
        nano.drawSquare(new Vector2f(0.075f * canvasSize),
                canvasSize * 0.85f, null);

        // Render the drawing
        for (int i = 0; i < drawing.size(); i++) {
            List<Float> subDrawing = drawing.get(i);
            if (subDrawing.size() >= 4) {
                nano.drawCustomShape(Utilities.listToArray(subDrawing),
                        new Vector2f(0, 0),1.0f, new RGBA(0, 0, 0, 255),
                            false, false, canvasSize / 28f);
            }
        }
    }

}