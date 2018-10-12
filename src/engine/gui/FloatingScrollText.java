package engine.gui;

import engine.MouseInput;
import engine.animation.Animator;
import engine.util.AssetStore;

public class FloatingScrollText extends FloatingText {

    private String fullText;

    private Animator animator;

    /**
     * Constructs a popup with default width and some text
     * that has an animation
     * @param text text of the popup
     */
    public FloatingScrollText(String text) {
        this(POPUP_DEFAULT_WIDTH, text);
    }

    /**
     * Constructs a popup with a custom width and some text
     * @param text text of the popup
     * @param width width of the popup
     */
    public FloatingScrollText(float width, String text) {
        super(width, text);
        this.fullText = text;
        this.animator = AssetStore.getAnimator("linear1sec");
    }

    @Override
    public void update(MouseInput mouse, float delta) {
        NanoVG nano = NanoVG.getInstance();

        if (animator != null) {
            float progress = animator.update(delta);

            int numberOfLetters = (int) (fullText.length() * progress);

            text = fullText.substring(0, numberOfLetters);
        }

        // Compute the height of the text
        if (text != null) {
            textHeight = nano.computeTextHeight(text, POPUP_TEXT_WIDTH);
            setComponentHeight(Math.max(textHeight + 70, POPUP_MINIMUM_HEIGHT));
        }

        // Center the object
        super.update(mouse, delta);
    }

}
