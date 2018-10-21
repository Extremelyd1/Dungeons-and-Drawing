package engine.animation;

import engine.animation.keyframe.ModelKeyFrame;

/**
 *
 * Represents an animation that can applied to an AnimatedModel. It
 * contains the length of the animation in seconds, and a list of
 * {@link ModelKeyFrame}s.
 *
 */
public class ModelAnimation {

    private final float length;//in seconds
    private final ModelKeyFrame[] keyFrames;
    private final ModelKeyFrame idleKeyFrame;

    /**
     * @param lengthInSeconds
     *            - the total length of the animation in seconds.
     * @param frames
     *            - all the keyframes for the animation, ordered by time of
     *            appearance in the animation.
     */
    public ModelAnimation(float lengthInSeconds, ModelKeyFrame[] frames, ModelKeyFrame idleFrame) {
        this.keyFrames = frames;
        this.length = lengthInSeconds;
        this.idleKeyFrame = idleFrame;
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
    public ModelKeyFrame[] getKeyFrames() {
        return keyFrames;
    }

    /**
     *
     * @return The keyframe used when the player is idle
     */
    public ModelKeyFrame getIdleKeyFrame() {
        return idleKeyFrame;
    }

}
