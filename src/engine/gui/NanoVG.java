package engine.gui;

import engine.GameWindow;
import engine.util.Utilities;
import org.lwjgl.nanovg.NVGColor;

import java.io.IOException;
import java.nio.ByteBuffer;

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

    private NVGColor color;

    private static final String FONT_PARAGRAPH = "SEGOE_UI";
    private static final String FONT_PARAGRAPH_BOLD = "SEGOE_UI_BOLD";
    private static final String FONT_PARAGRAPH_LIGHT = "SEGOE_UI_LIGHT";
    private static final String FONT_TITLE = "VECNA";

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

        try {
            ByteBuffer fontBuffer1 = Utilities.ioResourceToByteBuffer("/fonts/Vecna.ttf", 150 * 1024);
            ByteBuffer fontBuffer2 = Utilities.ioResourceToByteBuffer("/fonts/segoeuib.ttf", 150 * 1024);
            ByteBuffer fontBuffer3 = Utilities.ioResourceToByteBuffer("/fonts/segoeuil.ttf", 150 * 1024);
            ByteBuffer fontBuffer4 = Utilities.ioResourceToByteBuffer("/fonts/segoeui.ttf", 150 * 1024);

            int font1 = nvgCreateFontMem(nanoVGHandler, FONT_TITLE, fontBuffer1, 0);
            int font2 = nvgCreateFontMem(nanoVGHandler, FONT_PARAGRAPH_BOLD, fontBuffer2, 0);
            int font3 = nvgCreateFontMem(nanoVGHandler, FONT_PARAGRAPH_LIGHT, fontBuffer3, 0);
            int font4 = nvgCreateFontMem(nanoVGHandler, FONT_PARAGRAPH, fontBuffer4, 0);

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

    public void drawRectangle(int posX, int posY, int width, int height, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgRect(nanoVGHandler, posX, posY, width, height);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    public void drawCircle(int posX, int posY, int radius, RGBA rgba) {
        nvgBeginPath(nanoVGHandler);
        nvgCircle(nanoVGHandler, posX, posY, radius);
        nvgFillColor(nanoVGHandler, rgba(rgba, color));
        nvgFill(nanoVGHandler);
    }

    public void drawParagraphText(int posX, int posY, String text) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_PARAGRAPH);
        nvgFontFace(nanoVGHandler, FONT_PARAGRAPH);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
    }

    public void drawTitleText(int posX, int posY, String text) {
        nvgFontSize(nanoVGHandler, FONT_SIZE_TITLE);
        nvgFontFace(nanoVGHandler, FONT_TITLE);
        nvgTextAlign(nanoVGHandler, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        nvgFillColor(nanoVGHandler, rgba(null, color));
        nvgText(nanoVGHandler, posX, posY, text);
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

    private RGBA getDefaultColor() {
        return new RGBA(255, 255, 255, 255);
    }
}
