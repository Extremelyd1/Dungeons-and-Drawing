package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import org.joml.Vector2f;

public class TitleCard extends GUIComponent {


    public TitleCard() {
        super();
        float x = GameWindow.getGameWindow().getWindowWidth() / 2.0f;
        float y = GameWindow.getGameWindow().getWindowHeight() / 2.0f;
        this.setPosition(x, y);
    }

    @Override
    public void update(MouseInput mouse) {
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();
        nano.transform(this.getPosition(), this.getRotation(), this.getScale());
        nano.drawTitleText(new Vector2f(0, 0),
                "Dungeons & Drawings",
                new RGBA(255, 255, 255, getAlphaChannel())
        );
    }
}
