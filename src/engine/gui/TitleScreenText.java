package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import engine.animation.Animator;
import engine.animation.TrigonometricAnimator;
import engine.util.AssetStore;
import org.joml.Vector2f;

/**
 * Basic popup. Is centered by default and the height adjusts based on the text
 * that is to be displayed. Has a default width, but this can be changed
 */
public class TitleScreenText extends FloatingText {

    private RGBA textColor = POPUP_COLOR_TEXT;

    private float yPos = 0;

    private Animator alphaAnimator;
    private Animator movementAnimator;

    /**
     * Constructs a popup with the default width and some text
     * @param text text of the popup
     */
    public TitleScreenText(String text) {
        super(text);
        this.alphaAnimator = AssetStore.getAnimator("titlescreenText");
        this.movementAnimator = AssetStore.getAnimator("indicatorMovement");
    }

    @Override
    public void render() {
        NanoVG nano = NanoVG.getInstance();

        nano.transform(this.getPosition());

        if (text != null) {
            GameWindow window = GameWindow.getGameWindow();
            // Draw the text
            float posX = window.getWindowWidth() / 2f - getComponentWidth() / 2.0f;
            float posY = window.getWindowHeight() - getComponentHeight() * (yPos + 0.2f) - textHeight / 2.0f;
            nano.drawHintText(new Vector2f(posX, posY), getComponentWidth(), text, POPUP_COLOR_TEXT);
        }
   }

    @Override
    public void update(MouseInput mouse, float delta) {
        super.update(mouse, delta);

        float alpha = alphaAnimator.update(delta);
        textColor.a = (int) (alpha * 255);

        float newPos = movementAnimator.update(delta);
        yPos = newPos * 0.1f;
    }
}
