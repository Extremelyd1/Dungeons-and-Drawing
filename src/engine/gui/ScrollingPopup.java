package engine.gui;

import engine.MouseInput;
import engine.animation.Animator;
import engine.util.AssetStore;
import game.action.Action;

public class ScrollingPopup extends Popup {

    private String fullText;

    private Animator animator;

    /**
     * Constructs a popup with a custom width and no text
     * @param width the width of the popup in pixels
     */
    public ScrollingPopup(float width, Action action) {
        this(width, null, action);
    }

    /**
     * Constructs a popup with the default width and some text
     * @param text text of the popup
     */
    public ScrollingPopup(String text, Action action) {
        this(POPUP_DEFAULT_WIDTH, text, action);
    }

    /**
     * Constructs a popup with a custom width and some text
     * @param text text of the popup
     * @param width width of the popup
     * @param action the action to execute when user closes the popup
     */
    public ScrollingPopup(float width, String text, Action action) {
        super(width, text, action);
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

        if (mouse.isLeftButtonPressed() && action != null) {
            action.execute();
        }

        // Center the object
        super.update(mouse, delta);
    }

}
