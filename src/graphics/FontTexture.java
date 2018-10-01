package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Class to generate a texture image from a Font. All font information is retrieved from a java.awt.Font
 * class. The characters that we want to be part of our font are decided based on a character set.
 *
 * The FontTexture generates a very wide image (as an input stream) with all the characters next to
 * each other which then can be used to display text
 */
public class FontTexture {

    // The format of the image we are generating
    private static final String IMAGE_FORMAT = "png";

    // The font object that contains all properties of the font
    private final Font font;

    // The character set that defines all characters we want
    private final String charSetName;

    // A mapping from character to character info.
    // The character info concerns the position and width of a character in the texture
    private final Map<Character, CharInfo> charMap;

    // Texture
    private Texture texture;

    // Height of the texture picture
    private int height;

    // Width of the texture picture
    private int width;

    /**
     * Constructs a texture based on a character set and Font details
     * @param font The properties of the Font
     * @param charSetName The set of characters
     * @throws Exception if inputstream fails
     */
    public FontTexture(Font font, String charSetName) throws Exception {
        this.font = font;
        this.charSetName = charSetName;
        charMap = new HashMap<>();

        buildTexture();
    }

    /**
     * Generates a string with all characters in the character set
     * @param charsetName a set of characters
     * @return A string with all characters concatenated
     */
    private String getAllAvailableChars(String charsetName) {
        CharsetEncoder ce = Charset.forName(charsetName).newEncoder();
        StringBuilder result = new StringBuilder();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (ce.canEncode(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Generates the image texture
     * @throws Exception if inputstream fails
     */
    private void buildTexture() throws Exception {

        // Get the font metrics for each character for the selected font by using image
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2D = img.createGraphics();
        g2D.setFont(font);
        FontMetrics fontMetrics = g2D.getFontMetrics();

        String allChars = getAllAvailableChars(charSetName);

        // Define spacing between the characters
        final int spacing = 5;

        this.width = spacing;
        this.height = fontMetrics.getHeight();

        // Loop through all characters and update width and height variables
        for (char c : allChars.toCharArray()) {
            CharInfo charInfo = new CharInfo(this.width, fontMetrics.charWidth(c));
            charMap.put(c, charInfo);

            width += charInfo.getWidth() + spacing;
        }

        g2D.dispose();

        // Create the image associated to the charset
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2D = img.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setFont(font);
        fontMetrics = g2D.getFontMetrics();
        g2D.setColor(Color.WHITE);

        int drawX = spacing;
        int drawY = fontMetrics.getAscent();
        // Draw all characters individually on the texture image
        for (char c : allChars.toCharArray()) {
            String charString = String.valueOf(c);

            g2D.drawString(charString, drawX, drawY);
            drawX += charMap.get(c).getWidth() + spacing;
        }
        g2D.dispose();

        // If you would want to, you can save the generated image with the following line
        //ImageIO.write(img, IMAGE_FORMAT, new java.io.File("Temp.png"));

        // Dump image to a byte buffer
        InputStream is;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, IMAGE_FORMAT, out);
        out.flush();
        is = new ByteArrayInputStream(out.toByteArray());

        texture = new Texture(is);
    }

    /* Get the width of the image */
    public int getWidth() {
        return width;
    }

    /* Get the height of the image */
    public int getHeight() {
        return height;
    }

    /* Get the texture of the image */
    public Texture getTexture() {
        return texture;
    }

    /* Get info, width and startX, of the character in the image */
    public CharInfo getCharInfo(char c) {
        return charMap.get(c);
    }

    /**
     * Data structure that stores the start position and width of the character
     * within the generated image
     */
    public static class CharInfo {

        private final int startX;

        private final int width;

        public CharInfo(int startX, int width) {
            this.startX = startX;
            this.width = width;
        }

        public int getStartX() {
            return startX;
        }

        public int getWidth() {
            return width;
        }
    }
}