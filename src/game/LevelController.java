package game;

import engine.GameWindow;
import engine.IGameLogic;
import engine.MouseInput;
import game.level.Level;
import game.level.TestLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelController implements IGameLogic {

    private List<Level> levels;
    private Level active;

    public LevelController() {
        this.levels = new ArrayList<>(Arrays.asList(
                new TestLevel(this)
        ));
        this.active = levels.get(0);
    }

    @Override
    public void init(GameWindow window) throws Exception {
        active.init(window);
    }

    @Override
    public void input(GameWindow window, MouseInput mouseInput) {
        active.input(window, mouseInput);
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        active.update(interval, mouseInput);
    }

    @Override
    public void render(GameWindow window) {
        active.render(window);
    }

    @Override
    public void terminate() {
        active.terminate();
    }

    // TODO: Level switching mechanics
}
