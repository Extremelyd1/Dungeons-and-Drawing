package engine.gui;

import engine.GameWindow;
import engine.MouseInput;

import java.util.List;

public class DrawingList extends Popup {

    float timeLeft;

    public DrawingList(List<String> availableDrawings) {
        super(GameWindow.getGameWindow().getWindowHeight() * 0.25f);
        setComponentHeight(GameWindow.getGameWindow().getWindowHeight() * 0.75f);
        setComponentWidth(GameWindow.getGameWindow().getWindowHeight() * 0.25f);
    }

    public void start() {

    }
    
    @Override
    public void render() {

    }

    @Override
    public void update(MouseInput mouse) {
        super.update(mouse);
    }
}
