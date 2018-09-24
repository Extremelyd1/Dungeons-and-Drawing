package game.level;

import engine.GameWindow;
import engine.IGameLogic;
import engine.MouseInput;

public abstract class Level implements IGameLogic {



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
