package game;

import engine.IGameLogic;
import engine.MouseInput;
import engine.input.KeyBinding;
import engine.util.Timer;
import game.level.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelController implements IGameLogic {

    private List<Level> levels;
    private int active;
    private Timer timer;

    public LevelController() {
        this.levels = new ArrayList<>(Arrays.asList(
                new TutorialDrawingLevel(this),
                new levelEditor(this),
                new FullLevel1(this)
        ));
        this.active = 0;
        this.timer = new Timer();
    }

    @Override
    public void init() throws Exception {
        timer.init();
        levels.get(active).init();
    }

    @Override
    public void input(MouseInput mouseInput) {
        levels.get(active).input(mouseInput);
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Timed check so that we don't switch levels too quickly
        if (KeyBinding.isPreviousLevelPressed() && timer.peekElapsedTime() > 1) {
            previous();
            timer.init();
        }
        if (KeyBinding.isRestartLevelPressed() && timer.peekElapsedTime() > 1) {
            restart();
            timer.init();
        }
        if (KeyBinding.isNextLevelPressed() && timer.peekElapsedTime() > 1) {
            next();
            timer.init();
        }

        levels.get(active).update(interval, mouseInput);
    }

    @Override
    public void render() {
        levels.get(active).render();
    }

    @Override
    public void terminate() {
        levels.get(active).terminate();
    }

    public void switchToLevel(int levelIndex) {
        // Unload level to release resources
        levels.get(active).terminate();

        // Switch context
        try {
            levels.get(levelIndex).init();
            active = levelIndex;
        } catch (Exception e) {
            System.err.println("Could not load level " + levelIndex);
            e.printStackTrace();
        }
    }

    public void next() {
        if (active < levels.size() - 1) {
            switchToLevel(active + 1);
        }
    }

    public void previous() {
        if (active > 0) {
            switchToLevel(active - 1);
        }
    }

    public void restart() {
        try {
            levels.get(active).init();
        } catch (Exception e) {
            System.err.println("Could not load level " + active);
            e.printStackTrace();
        }
    }
}
