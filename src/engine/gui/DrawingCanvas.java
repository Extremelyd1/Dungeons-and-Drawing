package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.util.Utilities;
import game.NeuralNetwork;
import org.joml.Vector2f;
import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
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

    private Graphics g;
    private BufferedImage image;
    private Graphics2D gImage;

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

        int size = Math.round(this.canvasSize);

        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();
        gImage = (Graphics2D) image.getGraphics();
        gImage.setColor(Color.WHITE);
        gImage.fillRect(0, 0, size, size);

        gImage.setColor(Color.BLACK);
        gImage.setStroke(new BasicStroke(canvasSize / 28, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        gImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
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

                // Add last subdrawing to buffered image
                if (drawing.size() > 1) {
                    addSubdrawingToImage(drawing.size() - 2);
                }
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

        // For testing uncomment
//        if (mouse.isRightButtonPressed()) {
//            System.out.println(NeuralNetwork.getBestGuess(getImage()));
//            System.out.println(NeuralNetwork.getBestGuess(getImage(), "key", "hat", "cactus")));
//        }
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

    private void addSubdrawingToImage(int index) {
        List<Float> subDrawing = drawing.get(index);
        if (subDrawing.size() >= 4) {
            Vector2i from = null;
            for (int i = 0; i < subDrawing.size(); i += 2) {
                Vector2i p = new Vector2i(Math.round(subDrawing.get(i)), Math.round(subDrawing.get(i + 1)));
                if (from != null) {
                    gImage.drawLine(from.x, from.y, p.x, p.y);
                }
                from = p;
            }
        }
    }

    /**
     * Gets the image drawn in BufferedImage format to feed to the network
     * @return the image drawn in BufferedImage format
     */
    public BufferedImage getImage() {
        addSubdrawingToImage(drawing.size() - 1);
        g.drawImage(image, 0, 0, null);
        return image;
    }

}
