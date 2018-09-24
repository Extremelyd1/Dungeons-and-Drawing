package game.level;

import engine.Camera;
import engine.GameWindow;
import engine.IGameLogic;
import engine.MouseInput;
import game.LevelController;
import game.Renderer;

public abstract class Level implements IGameLogic {

    protected LevelController levelController;
    protected Camera camera;
    protected Renderer renderer;

    public Level(LevelController levelController) {
        this.levelController = levelController;
    }

    public abstract void init(GameWindow window) throws Exception;

    @Override
    public abstract void input(GameWindow window, MouseInput mouseInput);

    @Override
    public abstract void update(float interval, MouseInput mouseInput);

    @Override
    public abstract void render(GameWindow window);

    @Override
    public abstract void terminate();
}
