package engine.gui;

import com.sun.javaws.exceptions.InvalidArgumentException;
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
 */
public class NanoVG {

    private long nanoVGHandler;
    private static NanoVG nanoVG;

    private List<ByteBuffer> fontData;

    private NVGColor color;

    private static final String SEGOE_UI = "SEGOE_UI";
    private static final String SEGOE_UI_BOLD = "SEGOE_UI_BOLD";
    private static final String SEGOE_UI_LIGHT = "SEGOE_UI_LIGHT";
    private static final String VECNA = "VECNA";

    private static final float FONT_SIZE_PARAGRAPH = 24.0f;
    private static final float FONT_SIZE_TITLE = 48.0f;


    /** Singleton constructor */
    public static NanoVG getInstance() {
        if (nanoVG == null) {
            nanoVG = new NanoVG();
        }
        return nanoVG;
    }

    /**
     * Initializes the NanoVG context
     */
    private NanoVG() {
        this.nanoVGHandler = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        if (this.nanoVGHandler == NULL) {
            throw new RuntimeException("engine.gui.NanoVG() failed: Failed to initialize" +
                    "the NanoVG context");
        }

        // Memory allocation to store the color
        color = NVGColor.create();

        initializeFonts();
    }

    /**
     * Creates the fonts
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
     * Initializes a frame
     */
    public void createFrame() {
        int width = GameWindow.getGameWindow().getWindowWidth();
        int height = GameWindow.getGameWindow().getWindowHeight();
        nvgBeginFrame(nanoVGHandler, width, height, 1);
    }

    /**
     * Terminates a frame
     */
    public void terminateFrame() {
        nvgEndFrame(nanoVGHandler);
        GameWindow.getGameWindow().restoreState();
    }

    /**
     *
     * @param posX
     * @param posY
     * @param width
     * @param height
     * @param rgba
     */
    public void drawRectangle(float posX, float posY, float width, float height, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgRect(nanoVGHandler, posX, posY, width, height);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    /**
     *
     * @param posX
     * @param posY
     * @param radius
     * @param rgba
     */
    public void drawCircle(float posX, float posY, float radius, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgCircle(nanoVGHandler, posX, posY, radius);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    /**
     *
     * @param posX
     * @param posY
     * @param text
     */
    public void drawParagraphText(float posX, float posY, String text) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_PARAGRAPH);
        nvgFontFace(nanoVGHandler, SEGOE_UI);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
    }

    /**
     *
     * @param posX
     * @param posY
     * @param text
     */
    public void drawTitleText(float posX, float posY, String text) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_TITLE);
        nvgFontFace(nanoVGHandler, VECNA);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER| NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
    }

    /**
     *
     * @param posX
     * @param posY
     * @param text
     * @param fontSize
     * @param font
     */
    public void drawText(float posX, float posY, String text, float fontSize, Font font) {
        nvgFontSize(nanoVGHandler, fontSize);
        nvgFontFace(nanoVGHandler, font.toString());
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER| NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
    }

    /**
     *
     * @param posX
     * @param posY
     * @param width
     * @param rgba
     */
    public void drawLine(float posX, float posY, float width, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgMoveTo(nanoVGHandler, posX, posY);
        nvgLineTo(nanoVGHandler, posX + width, posY);
        nvgStrokeColor(nanoVGHandler, rgba(rgba, color));
        nvgStrokeWidth(nanoVGHandler, 5.0f);
        nvgStroke(nanoVGHandler);
    }

    /**
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param rgba
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
     *
     * @param points
     * @param rgba
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
     *
     * @param points
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
     *
     * @param points
     */
    private void drawCustomLine(float[] points) {
        nvgMoveTo(nanoVGHandler, points[0], points[1]);
        for (int i = 1; i < points.length / 2; i++) {
            nvgLineTo(nanoVGHandler, points[i * 2], points[i * 2 + 1]);
        }
    }

    /** Set the color of a new drawing using RGBA channels
     *
     * @param r The red channel of the color
     * @param g The green channel of the color
     * @param b The blue channel of the color
     * @param a The alpha channels of the color
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

    /** Set the color of a new drawing using RGBA channels
     *
     * @param rgba record that contains all channels
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
     *
     * @return
     */
    private RGBA getDefaultColor() {
        return new RGBA(255, 255, 255, 255);
    }
}
