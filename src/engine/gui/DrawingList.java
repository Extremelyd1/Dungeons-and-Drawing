package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.util.Timer;
import game.action.Action;
import game.puzzle.Puzzle;
import org.joml.Vector2f;

public class DrawingList extends Popup {

    private static float TEXT_PADDING = 25f;

    private String[] options;

    private boolean isRunning;
    private float timeLeft;
    private String timeLeftString;
    private Timer timer;
    private Action action;
    private Puzzle puzzle;

    public DrawingList(Puzzle puzzle) {
        super(GameWindow.getGameWindow().getWindowHeight() * 0.25f);

        setComponentHeight(GameWindow.getGameWindow().getWindowHeight() * 0.75f);
        setComponentWidth(GameWindow.getGameWindow().getWindowHeight() * 0.25f);

        this.options = puzzle.getOptions();
        this.puzzle = puzzle;

        this.isRunning = false;
        this.timer = new Timer();
        resetCountdown();

        setCentered(false);
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
    public void update(MouseInput mouse) {
        if (isRunning) {
            timeLeft = Math.max(0, timeLeft - timer.getElapsedTime());

            if (timeLeft == 0) {
                puzzle.evaluate().execute();
                isRunning = false;
            }

            timeLeftString = "Time left: " + String.valueOf((int) Math.ceil(timeLeft));
        }
        super.update(mouse);
    }
}
