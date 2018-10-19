package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.util.Timer;
import game.NeuralNetwork;
import game.puzzle.Puzzle;
import org.joml.Vector2f;

import java.awt.image.BufferedImage;

public class DrawingList extends Popup {

    private static float TEXT_PADDING = 25f;

    private String[] options;

    private boolean isRunning;
    private float timeLeft;
    private String timeLeftString;
    private Timer timer;
    private Puzzle puzzle;
    private DrawingCanvas canvas;

    public DrawingList(Puzzle puzzle, DrawingCanvas canvas) {
        super(GameWindow.getGameWindow().getWindowHeight() * 0.25f, null);

        setComponentHeight(GameWindow.getGameWindow().getWindowHeight() * 0.75f);
        setComponentWidth(GameWindow.getGameWindow().getWindowHeight() * 0.25f);

        this.options = puzzle.getOptions();
        this.puzzle = puzzle;

        this.isRunning = false;
        this.timer = new Timer();
        resetCountdown();

        setCentered(false);

        this.canvas = canvas;
        this.timeLeftString = "Time left: " + String.valueOf((int) Math.ceil(timeLeft));
    }

    public void resetCountdown() {
        timer.init();
        isRunning = true;
        timeLeft = puzzle.getTime();
    }
    
    @Override
    public void render() {
        super.render();

        NanoVG nano = NanoVG.getInstance();

        nano.computeTextHeight(timeLeftString, getComponentWidth());

        nano.drawRectangle(new Vector2f(0, 0), getComponentWidth(),
                nano.computeTextHeight(timeLeftString, getComponentWidth()) + 2 * TEXT_PADDING, Popup.POPUP_COLOR_DARK);

        nano.drawText(new Vector2f(getComponentWidth() / 2.0f, TEXT_PADDING),
                timeLeftString, Font.SEGOE_UI_BOLD, null);

        float x = getComponentWidth() / 2.0f;
        float y = nano.computeTextHeight(timeLeftString, getComponentWidth()) + 3 * TEXT_PADDING;
        for (String s : options) {
            nano.drawText(new Vector2f(x, y), s, Font.SEGOE_UI, Popup.POPUP_COLOR_TEXT);
            y += TEXT_PADDING;
        }
    }

    @Override
    public void update(MouseInput mouse, float delta) {
        if (isRunning) {
            timeLeft = Math.max(0, timeLeft - timer.getElapsedTime());

            if (timeLeft == 0 || mouse.isRightButtonPressed()) {
                BufferedImage image = canvas.getImage();
                if (image == null) {
                    puzzle.getDefaultSolution().getAction().execute("bamboozled");
                } else {
                    String networkGuess = NeuralNetwork.getBestGuess(canvas.getImage());
                    puzzle.evaluate(networkGuess).execute(networkGuess);
                }
                isRunning = false;
            }

            timeLeftString = "Time left: " + String.valueOf((int) Math.ceil(timeLeft));
        }
        super.update(mouse, delta);
    }
}
