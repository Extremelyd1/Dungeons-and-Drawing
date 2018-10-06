package engine.gui;

import engine.GameWindow;
import engine.util.Utilities;
import org.lwjgl.nanovg.NVGColor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_STENCIL_STROKES;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
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

    private static final String SEGOE_UI = "SEGOE_UI";
    private static final String SEGOE_UI_BOLD = "SEGOE_UI_BOLD";
    private static final String SEGOE_UI_LIGHT = "SEGOE_UI_LIGHT";
    private static final String VECNA = "VECNA";

    private static final float FONT_SIZE_PARAGRAPH = 24.0f;
    private static final float FONT_SIZE_TITLE = 48.0f;


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
     * @param posX   Upper left corner of the rectangle
     * @param posY   Upper left corner of the rectangle
     * @param width  Width of the rectangle
     * @param height Height of the rectangle
     * @param rgba   Color of the rectangle (in rgba)
     */
    public void drawRectangle(float posX, float posY, float width, float height, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgRect(nanoVGHandler, posX, posY, width, height);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    /**
     * Draws a square
     *
     * @param posX Upper left corner of the rectangle
     * @param posY Upper left corner of the rectangle
     * @param size Size of the sides
     * @param rgba Color of the square
     */
    public void drawSquare(float posX, float posY, float size, RGBA rgba) {
        drawRectangle(posX, posY, size, size, rgba);
    }

    /**
     * Draws a circle
     *
     * @param posX   x coordinate of the center of the circle
     * @param posY   y coordinate of the center circle
     * @param radius radius of the circle
     * @param rgba   color of the circle
     */
    public void drawCircle(float posX, float posY, float radius, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgCircle(nanoVGHandler, posX, posY, radius);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    /**
     * Draws basic text in the default paragraph style
     *
     * @param posX x coordinate of the text
     * @param posY y coordinate of the text
     * @param text The text content
     */
    public void drawParagraphText(float posX, float posY, String text) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_PARAGRAPH);
        nvgFontFace(nanoVGHandler, SEGOE_UI);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
    }

    /**
     * Draws basic text in the default title style
     *
     * @param posX x coordinate of the text
     * @param posY y coordinate of the text
     * @param text The text content
     */
    public void drawTitleText(float posX, float posY, String text) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_TITLE);
        nvgFontFace(nanoVGHandler, VECNA);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
    }

    /**
     * Draws basic text in the default paragraph style
     *
     * @param posX     x coordinate of the text
     * @param posY     y coordinate of the text
     * @param text     The text content
     * @param fontSize Font size in pixels
     * @param font     Font
     */
    public void drawText(float posX, float posY, String text, float fontSize, Font font) {
        nvgFontSize(nanoVGHandler, fontSize);
        nvgFontFace(nanoVGHandler, font.toString());
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
    }

    /**
     * Draws a line
     *
     * @param startX x coordinate of start point
     * @param startY y coordinate of end point
     * @param endX x coordinate of start point
     * @param endY y coordinate of end point
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
     * @param startX x coordinate of start point
     * @param startY y coordinate of end point
     * @param endX x coordinate of start point
     * @param endY y coordinate of end point
     * @param rgba color of the line
     */
    public void drawCurve(float startX, float startY, float endX, float endY, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgMoveTo(nanoVGHandler, startX, startY);
        nvgQuadTo(nanoVGHandler, endX, startY, endX, endY);
        nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
        nvgStrokeWidth(nanoVGHandler, 5.0f);
        nvgStroke(nanoVGHandler);
    }

    /**
     * Draws a custom shape defined by a list of points.
     *
     * @param points List of points (x, y, and possible also control points)
     * @param rgba Color of the shape
     * @param filled Whether the shape is filled or stroked
     */
    public void drawCustomShape(float[] points, RGBA rgba, boolean curved, boolean filled) {

        if (points.length % 2 != 0) {
            throw new IllegalArgumentException("engine.gui.NanoVG.drawCustomShape() failed: " +
                    "Illegal arguments. Not an even number of points");
        }

        if (points.length < 4) {
            throw new IllegalArgumentException("engine.gui.NanoVG.drawCustomShape() failed: " +
                    "Illegal arguments. Too few points");
        }

        nvgBeginPath(nanoVGHandler);

        if (curved) {
            drawCustomCurve(points);
        } else {
            drawCustomLine(points);
        }

        if (!filled) {
            nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
            nvgStrokeWidth(nanoVGHandler, 5.0f);
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
    
    private RGBA getDefaultColor() {
        return new RGBA(255, 255, 255, 255);
    }
}
