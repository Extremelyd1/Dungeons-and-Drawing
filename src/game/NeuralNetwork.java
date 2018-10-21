package game;

import javafx.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class NeuralNetwork {

    private static MultiLayerNetwork model;

    private static float threshold = 0.6f;

    private static String[] labels = new String[]{
            "airplane",
            "alarm_clock",
            "apple",
            "baseball_bat",
            "books",
            "brain",
            "butterfly",
            "cactus",
            "cake",
            "dolphin",
            "flashlight",
            "flower",
            "flying_saucer",
            "golf_club",
            "hat",
            "key",
            "lightning",
            "lobster",
            "mailbox",
            "matches",
            "mug",
            "octopus",
            "palm_tree",
            "panda",
            "penguin",
            "radio",
            "sandwich",
            "saw",
            "shark",
            "shoe",
            "submarine"
    };

    /**
     * Return the best guess for the given image
     *
     * @param image the image to base the guess on
     * @return the label for the best guess
     */
    public static String getBestGuess(BufferedImage image) {
        INDArray output = getOutput(image);

        Pair<String, Float> bestGuess = null;

        for (int i = 0; i < output.length(); i++) {
            if (bestGuess == null || output.getFloat(i) > bestGuess.getValue()) {
                bestGuess = new Pair<>(labels[i], output.getFloat(i));
            }
        }

        return bestGuess == null ? "bamboozled" : bestGuess.getKey();
    }

    /**
     * Return the best guess for the given image from the given options
     *
     * @param image   the image to base the guess on
     * @param options the available options to choose from
     * @return the label for the best guess
     */
    public static String getBestGuess(BufferedImage image, String... options) {
        return getBestGuess(image, new HashSet<>(Arrays.asList(options)));
    }

    /**
     * Return the best guess for the given image from the given options
     *
     * @param image   the image to base the guess on
     * @param options the available option set to choose from
     * @return the label for the best guess
     */
    public static String getBestGuess(BufferedImage image, Set<String> options) {
        INDArray output = getOutput(image);
        Pair<String, Float> setBest = null;
        Pair<String, Float> globalBest = null;

        for (int i = 0; i < output.length(); i++) {
            if (globalBest == null || output.getFloat(i) > globalBest.getValue()) {
                globalBest = new Pair<>(labels[i], output.getFloat(i));
            }

            if (options.contains(labels[i])) {
                if (output.getFloat(i) > threshold) {
                    if (setBest == null || output.getFloat(i) > setBest.getValue()) {
                        setBest = new Pair<>(labels[i], output.getFloat(i));
                    }
                }
            }
        }

        return (setBest == null ? globalBest.getKey() : setBest.getKey()).replace("_", " ");
    }

    /**
     * Gets the result of the network based on the image
     *
     * @param image Input image
     */
    public static Map<String, Float> getResult(BufferedImage image) {
        INDArray output = getOutput(image);
        Map<String, Float> result = new HashMap<>();

        for (int i = 0; i < output.length(); i++) {
            result.put(labels[i], output.getFloat(i));
        }

        return result;
    }

    private static INDArray getOutput(BufferedImage image) {
        image = transformImage(image);

        INDArray imageData = imageToNDArray(image);
        return model.output(imageData);
    }

    /**
     * Transforms the image into a 28 by 28 image. It also boosts the
     * black colours a bit to make the image the same as the data set.
     *
     * @param image Original image
     * @return Resized image
     */
    private static BufferedImage transformImage(BufferedImage image) {
        // Scale image to desired dimension (28 x 28)
        Image tmp = image.getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        BufferedImage scaledImage = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);

        // Loop through each pixel of the new image
        for (int x = 0; x < 28; x++) {
            for (int y = 0; y < 28; y++) {
                // Get original color
                Color color = new Color(scaledImage.getRGB(x, y));

                // Ignore white values
                if (color.getRGB() == -1) {
                    continue;
                }

                // 'Boost' the grey values so they become more black
                float[] hsv = new float[3];
                Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
                hsv[2] = (float) 0.5 * hsv[2];
                int newColor = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);

                // Save new color
                scaledImage.setRGB(x, y, newColor);
            }
        }

        // Free resources
        g2d.dispose();

        return scaledImage;
    }

    /**
     * Converts a buffered image into a NDArray.
     *
     * @param image Input image
     * @return 1D NDArray containing the image data
     */
    private static INDArray imageToNDArray(BufferedImage image) {
        float[][][][] data = new float[1][1][image.getWidth()][image.getHeight()];

        // Loop through each pixel of the image
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // Get color components (r, g, b)
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Calculate grey scale value and normalise
                float grey = (float) (red + green + blue) / 3;
                float normalised = 1 - grey / 255.0f;

                // Save value into array
                data[0][0][y][x] = normalised;
            }
        }

        // Convert java array to NDArray
        return Nd4j.create(data);
    }

    /**
     * Loads the model
     *
     * @throws Exception If the model could not be laoded
     */
    public static void loadModel() throws Exception {
        // Load our model
        System.out.println("Loading model...");
        File saveLocation = new File("trained_qd_model.zip");
        model = ModelSerializer.restoreMultiLayerNetwork(saveLocation);
    }

}
