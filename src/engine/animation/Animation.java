package engine.animation;

import engine.animation.keyframe.KeyFrame;

/**
 * Represents an animation that can applied. It
 * contains the length of the animation in seconds, and a list of
 * {@link KeyFrame}s.
 */
public class Animation {

    private final float length;//in seconds
    private final KeyFrame[] keyFrames;

    /**
     * @param length the total length of the animation in seconds.
     * @param keyFrames all the keyframes for the animation, ordered by time of
     *            appearance in the animation.
     */
    public Animation(float length, KeyFrame[] keyFrames) {
        this.keyFrames = keyFrames;
        this.length = length;
    }

    /**
     * @return The length of the animation in seconds.
     */
    public float getLength() {
        return length;
    }

    /**
     * @return An array of the animation's keyframes. The array is ordered based
     *         on the order of the keyframes in the animation (first keyframe of
     *         the animation in array position 0).
     */
    public KeyFrame[] getKeyFrames() {
        return keyFrames;
    }

}