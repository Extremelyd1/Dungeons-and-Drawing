package game;

import engine.IGameLogic;
import engine.MouseInput;
import game.level.Level;
import game.level.MapTestLevel;
import game.level.TestLevel;
import game.level.TutorialLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelController implements IGameLogic {

    private List<Level> levels;
    private Level active;

    public LevelController() {
        this.levels = new ArrayList<>(Arrays.asList(
                new TutorialLevel(this)
        ));
        this.active = levels.get(0);
    }

    @Override
    public void init() throws Exception {
        active.init();
    }

    @Override
    public void input(MouseInput mouseInput) {
        active.input(mouseInput);
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        active.update(interval, mouseInput);
    }

    @Override
    public void render() {
        active.render();
    }

    @Override
    public void terminate() {
        active.terminate();
    }

    // TODO: Level switching mechanics
}
