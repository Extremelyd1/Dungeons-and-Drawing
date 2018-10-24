package engine.gui;

import engine.GameWindow;
import engine.util.Utilities;
import game.action.Action;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Helper class that simplifies drawing with the NanoVG Library
 * <p>
 * It defines and initializes all fonts and support the drawing of simple geometric shapes (i.e.
 * the square and circle) and custom shapes (either curved or not)
 * <p>
 * Singleton class
 */
public class NanoVG {

    private long nanoVGHandler;
    private static NanoVG nanoVG;

    private List<ByteBuffer> fontData; // To keep the buffers to be removed by the Garbage Collector

    private NVGColor color;

    private Action imageReloadAction;

    private static final String SEGOE_UI = "SEGOE_UI";
    private static final String SEGOE_UI_BOLD = "SEGOE_UI_BOLD";
    private static final String SEGOE_UI_LIGHT = "SEGOE_UI_LIGHT";
    private static final String VECNA = "VECNA";

    public static final float FONT_SIZE_PARAGRAPH = 24.0f;
    public static final float FONT_SIZE_HINT = 36f;
    public static final float FONT_SIZE_TITLE = 96.0f;

    public static void reload() {
        if (nanoVG != null) {
            nanoVG = new NanoVG(nanoVG.imageReloadAction);
            nanoVG.imageReloadAction.execute();
        }
    }

    /**
     * Singleton constructor
     */
    public static NanoVG getInstance() {
        if (nanoVG == null) {
            nanoVG = new NanoVG();
        }
        return nanoVG;
    }

    /**
     * Initializes the NanoVG context, the color object and the fonts
     */
    private NanoVG() {
        this.nanoVGHandler = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        if (this.nanoVGHandler == NULL) {
            throw new RuntimeException("engine.gui.NanoVG() failed: Failed to initialize" +
                    "the NanoVG context");
        }

        // Memory allocation to store the color
        color = NVGColor.create();

        // Initializes fonts
        initializeFonts();
    }

    private NanoVG(Action imageReloadAction) {
        this();
        this.imageReloadAction = imageReloadAction;
    }

    /**
     * Initializes the fonts by allocating off-heap memory. Fonts are loaded from .ttf files
     * and are identified by a string
     */
    private void initializeFonts() {

        fontData = new ArrayList<>();

        try {
            fontData.add(Utilities.ioResourceToByteBuffer("/fonts/Vecna.ttf", 150 * 1024));
            fontData.add(Utilities.ioResourceToByteBuffer("/fonts/segoeuib.ttf", 150 * 1024));
            fontData.add(Utilities.ioResourceToByteBuffer("/fonts/segoeuil.ttf", 150 * 1024));
            fontData.add(Utilities.ioResourceToByteBuffer("/fonts/segoeui.ttf", 150 * 1024));

            int font1 = nvgCreateFontMem(nanoVGHandler, VECNA, fontData.get(0), 0);
            int font2 = nvgCreateFontMem(nanoVGHandler, SEGOE_UI_BOLD, fontData.get(1), 0);
            int font3 = nvgCreateFontMem(nanoVGHandler, SEGOE_UI_LIGHT, fontData.get(2), 0);
            int font4 = nvgCreateFontMem(nanoVGHandler, SEGOE_UI, fontData.get(3), 0);

            if (font1 == -1 || font2 == -1 || font3 == -1 || font4 == -1) {
                throw new RuntimeException("engine.gui.NanoVG.intializeFonts() failed: " +
                        "One or more fonts could not be created");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes a frame. The frame is always the width and height of the game window.
     * Inside the window can be drawn, so we need to create the window before starting
     * to render the 2D gui.
     */
    public void createFrame() {
        int width = GameWindow.getGameWindow().getWindowWidth();
        int height = GameWindow.getGameWindow().getWindowHeight();
        nvgBeginFrame(nanoVGHandler, width, height, 1);
    }

    /**
     * Terminates a frame and restores the state of OpenGL (as the NanoVG
     * library can change the state
     */
    public void terminateFrame() {
        nvgEndFrame(nanoVGHandler);
        GameWindow.getGameWindow().restoreState();
    }

    /**
     * Draws a basic rectangle
     *
     * @param relativePosition relative position from gui component
     * @param width  Width of the rectangle
     * @param height Height of the rectangle
     * @param rgba   Color of the rectangle (in rgba)
     */
    public void drawRectangle(Vector2f relativePosition, float width, float height, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgRect(nanoVGHandler, relativePosition.x, relativePosition.y, width, height);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    /**
     * Draws a square
     *
     * @param relativePosition relative position from gui component
     * @param size Size of the sides
     * @param rgba Color of the square
     */
    public void drawSquare(Vector2f relativePosition, float size, RGBA rgba) {
        drawRectangle(relativePosition, size, size, rgba);
    }

    /**
     * Draws a circle
     *
     * @param relativePosition relative position from gui component
     * @param radius radius of the circle
     * @param rgba   color of the circle
     */
    public void drawCircle(Vector2f relativePosition, float radius, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgCircle(nanoVGHandler, relativePosition.x, relativePosition.y, radius);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    /**
     * Draws a circle
     *
     * @param relativePosition relative position from gui component
     * @param radius radius of the circle
     * @param rgba   color of the circle
     */
    public void drawDonut(Vector2f relativePosition, float radius, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgCircle(nanoVGHandler, relativePosition.x, relativePosition.y, radius);
        nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
        nvgStrokeWidth(nanoVGHandler, 2);
        nvgStroke(nanoVGHandler);
    }

    /**
     * Draws basic text in the default paragraph style
     *
     * @param relativePosition relative position from gui component
     * @param text The text content
     * @param rgba The color of the text
     */
    public void drawParagraphText(Vector2f relativePosition, float textWidth, String text, RGBA rgba) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_PARAGRAPH);
        nvgFontFace(nanoVGHandler, SEGOE_UI);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgTextBox(nanoVGHandler, relativePosition.x, relativePosition.y, textWidth, text);
    }

    /**
     * Draws hint text in the hint style
     *
     * @param relativePosition relative position from gui component
     * @param text The text content
     * @param rgba The color of the text
     */
    public void drawHintText(Vector2f relativePosition, float textWidth, String text, RGBA rgba) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_HINT);
        nvgFontFace(nanoVGHandler, SEGOE_UI);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgTextBox(nanoVGHandler, relativePosition.x, relativePosition.y, textWidth, text);
    }

    /**
     * Draws basic text in the default title style
     *
     * @param relativePosition relative position from gui component
     * @param text The text content
     * @param rgba The color of the text
     */
    public void drawTitleText(Vector2f relativePosition, String text, float fontSize, RGBA rgba) {
        nvgFontSize(nanoVGHandler, fontSize);
        nvgFontFace(nanoVGHandler, VECNA);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgText(nanoVGHandler, relativePosition.x, relativePosition.y, text);
    }

    public void drawTitleText(Vector2f relativePosition, String text, RGBA rgba) {
        drawTitleText(relativePosition, text, FONT_SIZE_TITLE, rgba);
    }

    /**
     * Draws basic text in the default paragraph style
     *
     * @param relativePosition relative position from gui component
     * @param text     The text content
     * @param font     Font
     * @param rgba The color of the text
     */
    public void drawText(Vector2f relativePosition, String text, Font font, RGBA rgba) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_PARAGRAPH);
        nvgFontFace(nanoVGHandler, font.toString());
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgText(nanoVGHandler, relativePosition.x, relativePosition.y, text);
    }

    /**
     * Draws a line
     *
     * @param startX x coordinate of start point, relative to GUIComponent
     * @param startY y coordinate of end point, relative to GUIComponent
     * @param endX x coordinate of start point, relative to GUIComponent
     * @param endY y coordinate of end point, relative to GUIComponent
     * @param rgba color of the line
     */
    public void drawLine(float startX, float startY, float endX, float endY, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgMoveTo(nanoVGHandler, startX, startY);
        nvgLineTo(nanoVGHandler, endX, endY);
        nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
        nvgStrokeWidth(nanoVGHandler, 5.0f);
        nvgStroke(nanoVGHandler);
    }

    /**
     * Draws an (upwards) curve
     *
     * @param startX x coordinate of start point, relative to GUIComponent
     * @param startY y coordinate of end point, relative to GUIComponent
     * @param endX x coordinate of start point, relative to GUIComponent
     * @param endY y coordinate of end point, relative to GUIComponent
     * @param rgba color of the line
     */
    public void drawCurve(float startX, float startY, float endX, float endY, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgMoveTo(nanoVGHandler, startX, startY);
        nvgQuadTo(nanoVGHandler, endX, startY, endX, endY);
        nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
        nvgStrokeWidth(nanoVGHandler, 5.0f);
        nvgLineCap(nanoVGHandler, NVG_ROUND);
        nvgStroke(nanoVGHandler);
    }

    /**
     * Draws a custom shape defined by a list of points.
     *
     * @param points List of points (x, y, and possible also control points)
     * @param rgba Color of the shape
     * @param filled Whether the shape is filled or stroked
     */
    public void drawCustomShape(float[] points, Vector2f relativePosition,
                                float scale, RGBA rgba, boolean curved,
                                boolean filled, float strokeWidth) {

        if (points.length % 2 != 0) {
            throw new IllegalArgumentException("engine.gui.NanoVG.drawCustomShape() failed: " +
                    "Illegal arguments. Not an even number of points");
        }

        if (points.length < 4) {
            throw new IllegalArgumentException("engine.gui.NanoVG.drawCustomShape() failed: " +
                    "Illegal arguments. Too few points");
        }

        float[] copy = points.clone();
        for (int i = 0; i < copy.length; i++) {
            copy[i] *= scale;
            if (i % 2 == 0) {
                copy[i] += relativePosition.x;
            } else {
                copy[i] += relativePosition.y;
            }
        }

        nvgBeginPath(nanoVGHandler);

        if (curved) {
            drawCustomCurve(copy);
        } else {
            drawCustomLine(copy);
        }

        if (!filled) {
            nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
            nvgStrokeWidth(nanoVGHandler, strokeWidth);
            nvgLineCap(nanoVGHandler, NVG_ROUND);
            nvgLineJoin(nanoVGHandler, NVG_ROUND);
            nvgStroke(nanoVGHandler);
        } else {
            nvgFillColor(nanoVGHandler, rgba(rgba, color));
            nvgFill(nanoVGHandler);
        }
    }

    /**
     * Draws a custom shape defined by a list of points. The shape is curved
     *
     * @param points List of points (x, y, and possible also control points)
     */
    private void drawCustomCurve(float[] points) {
        if (points.length < 8) {
            throw new IllegalArgumentException("engine.gui.NanoVG.drawCustomShape() failed: " +
                    "Illegal arguments. Too few points. Probably control points not defined.");
        }

        nvgMoveTo(nanoVGHandler, points[0], points[1]);

        for (int i = 0; i < points.length / 6; i++) {
            nvgBezierTo(nanoVGHandler,
                    points[i * 6 + 2], points[i * 6 + 3], // Control point for start
                    points[i * 6 + 4], points[i * 6 + 5], // Control point for end
                    points[i * 6 + 6], points[i * 6 + 7]); // End point
        }
    }

    /**
     * Draws a custom shape defined by a list of points. The shape is without curves
     *
     * @param points List of points (x, y, and possible also control points)
     */
    private void drawCustomLine(float[] points) {
        nvgMoveTo(nanoVGHandler, points[0], points[1]);
        for (int i = 1; i < points.length / 2; i++) {
            nvgLineTo(nanoVGHandler, points[i * 2], points[i * 2 + 1]);
        }
    }

    public void drawImage(Vector2f relativePosition, float width, float height, int handle) {
        nvgBeginPath(nanoVGHandler);
        nvgRect(nanoVGHandler, relativePosition.x, relativePosition.y, width, height);
        NVGPaint imgPaint = NVGPaint.create();
        imgPaint = nvgImagePattern(
                nanoVGHandler,
                relativePosition.x, relativePosition.y,
                width, height,
                0f, handle, 1f, imgPaint);
        nvgFillPaint(nanoVGHandler, imgPaint);
        nvgFill(nanoVGHandler);
    }

    public int createImage(String path, Action action) {
        int imageHandle = nvgCreateImage(nanoVGHandler, path, 0);
        imageReloadAction = action;
        return imageHandle;
    }

    /**
     * Set the color of a new drawing using RGBA channels
     *
     * @param r     The red channel of the color
     * @param g     The green channel of the color
     * @param b     The blue channel of the color
     * @param a     The alpha channels of the color
     * @param color The color object to store the colors
     * @return The color with the new RGBA channels
     */
    private NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
        color.r(r / 255f);
        color.g(g / 255f);
        color.b(b / 255f);
        color.a(a / 255f);
        return color;
    }

    /**
     * Set the color of a new drawing using RGBA channels
     *
     * @param rgba  record that contains all channels
     * @param color The color object to store the colors
     * @return The color with the new RGBA channels
     */
    private NVGColor rgba(RGBA rgba, NVGColor color) {
        if (rgba != null) {
            return rgba(rgba.r, rgba.g, rgba.b, rgba.a, color);
        } else {
            return rgba(getDefaultColor(), color);
        }
    }

    /**
     * Transform the matrix to include transformation
     * @param position Position
     */
    public void transform(Vector2f position) {
        nvgResetTransform(nanoVGHandler);
        nvgTranslate(nanoVGHandler, position.x, position.y);
    }

    /**
     * Adds a stroke to the defined path
     * @param strokeWidth width of the stroke
     * @param rgba color channels of the stroke
     */
    public void addStroke(float strokeWidth, RGBA rgba) {
        nvgStrokeWidth(nanoVGHandler, strokeWidth);
        nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
        nvgStroke(nanoVGHandler);
    }

    public float computeTextHeight(String text, float width) {
        float[] bounds = new float[4];
        nvgTextBoxBounds(nanoVGHandler, 0, 0, width, text, bounds);
        return bounds[3];
    }

    public float computeTextWidth(String text) {
        float[] bounds = new float[4];
        nvgTextBounds(nanoVGHandler, 0, 0, text, bounds);
        return bounds[2];
    }

    private RGBA getDefaultColor() {
        return new RGBA(255, 255, 255, 255);
    }

    public void terminateNanoVG() {
        nvgDelete(nanoVGHandler);
    }
}
