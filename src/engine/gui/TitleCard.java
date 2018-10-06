package engine.gui;

import engine.GameWindow;

public class TitleCard extends GUIComponent {


    public TitleCard() {
        super();
        float x = GameWindow.getGameWindow().getWindowWidth() / 2.0f;
        float y = GameWindow.getGameWindow().getWindowHeight() / 2.0f;
        this.setPosition(x, y);
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();
        nano.drawTitleText(
                this.getPosition().x ,
                this.getPosition().y - 48,
                "Dungeons & Drawings",
                new RGBA(255, 255, 255, getAlphaChannel())
        );
    }
}
