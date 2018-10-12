package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import org.joml.Vector2f;

/**
 * Class to render a titlecard which is a visually pleasing representation of
 * the title of our game. Centered by default
 */
public class TitleCard extends GUIComponent {

    private float textWidth;
    private float textHeight;

    public TitleCard() {
        super();
        setCentered(true);
    }

    @Override
    public void update(MouseInput mouse, float delta) {
        NanoVG nano = NanoVG.getInstance();

        textWidth = nano.computeTextWidth("Dungeons and Drawings");
        textHeight = nano.computeTextHeight("Dungeons and Drawings", GameWindow.getGameWindow().getWindowWidth());

        setComponentHeight(textHeight);
        setComponentWidth(textWidth);

        super.update(mouse, delta);
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();
        nano.transform(this.getPosition());

        nano.drawTitleText(new Vector2f(textWidth / 2.0f, textHeight / 2.0f),
                "Dungeons & Drawings",
                new RGBA(255, 255, 255)
        );
    }
}
