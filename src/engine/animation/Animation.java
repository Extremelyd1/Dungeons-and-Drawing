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
    private final boolean loop;

    private boolean running = false;
    private float animationTime = 0;

    /**
     * Copy constructor
     * @param animation the animation to copy
     */
    public Animation(Animation animation) {
        this(animation.length, animation.keyFrames, animation.loop);
    }

    /**
     * @param length the total length of the animation in seconds.
     * @param keyFrames all the keyframes for the animation, ordered by time of
     *            appearance in the animation.
     */
    public Animation(float length, KeyFrame[] keyFrames) {
        this(length, keyFrames, true);
    }

    /**
     * @param length the total length of the animation in seconds.
     * @param keyFrames all the keyframes for the animation, ordered by time of
     *            appearance in the animation.
     * @param loop whether the animation should loop
     */
    public Animation(float length, KeyFrame[] keyFrames, boolean loop) {
        this.keyFrames = keyFrames;
        this.length = length;
        this.loop = loop;
    }

    public void start() {
        if (!running) {
            running = true;
            animationTime = 0;
        }
    }

    /**
     * This method should be called each frame to update the animation currently
     * being played. This increases the animation time (and loops it back to
     * zero if necessary), finds the pose that the entity should be in at that
     * time of the animation, and then applies that pose to all the model's
     * joints by setting the joint transforms.
     */
    public float update(float delta) {
        if (!running) {
            return keyFrames[keyFrames.length - 1].getValue();
        }
        increaseAnimationTime(delta);
        return calculateCurrentValue();
    }

    /**
     * Increases the current animation time which allows the animation to
     * progress. If the current animation has reached the end then the timer is
     * reset, causing the animation to loop.
     */
    private void increaseAnimationTime(float delta) {
        animationTime += delta;
        if (animationTime > length) {
            if (loop) {
                this.animationTime %= length;
            } else {
                running = false;
            }
        }
    }

    /**
     * This method returns the current value of the animation.
     * <p>
     * The value is calculated based on the previous and next keyframes in the
     * current animation. Each keyframe provides the desired pose at a certain
     * time in the animation, so the animated pose for the current time can be
     * calculated by interpolating between the previous and next keyframe.
     * <p>
     * This method first finds the previous and next keyframe, calculates how far
     * between the two the current animation is, and then calculated the pose
     * for the current animation time by interpolating between the transforms at
     * those keyframes.
     *
     * @return The current value of the animation.
     */
    private float calculateCurrentValue() {
        KeyFrame[] frames = getPreviousAndNextFrames();
        float progression = calculateProgression(frames[0], frames[1]);
        return interpolate(frames[0], frames[1], progression);
    }

    /**
     * Finds the previous keyframe in the animation and the next keyframe in the
     * animation, and returns them in an array of length 2. If there is no
     * previous frame (perhaps current animation time is 0.5 and the first
     * keyframe is at time 1.5) then the first keyframe is used as both the
     * previous and next keyframe. The last keyframe is used for both next and
     * previous if there is no next keyframe.
     *
     * @return The previous and next keyframes, in an array which therefore will
     * always have a length of 2.
     */
    private KeyFrame[] getPreviousAndNextFrames() {
        KeyFrame previousFrame = keyFrames[0];
        KeyFrame nextFrame = keyFrames[0];
        for (int i = 1; i < keyFrames.length; i++) {
            nextFrame = keyFrames[i];
            if (nextFrame.getTimeStamp() > animationTime) {
                break;
            }
            previousFrame = keyFrames[i];
        }
        return new KeyFrame[]{previousFrame, nextFrame};
    }

    /**
     * Calculates how far between the previous and next keyframe the current
     * animation time is, and returns it as a value between 0 and 1.
     *
     * @param previousFrame - the previous keyframe in the animation.
     * @param nextFrame     - the next keyframe in the animation.
     * @return A number between 0 and 1 indicating how far between the two
     * keyframes the current animation time is.
     */
    private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
        float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
        float currentTime = animationTime - previousFrame.getTimeStamp();
        return currentTime / totalTime;
    }

    /**
     * Calculates the interpolation value between these keyframe with given
     * progression.
     *
     * @param previousFrame - the previous keyframe in the animation.
     * @param nextFrame     - the next keyframe in the animation.
     * @param progression   - a number between 0 and 1 indicating how far between the
     *                      previous and next keyframes the current animation time is.
     * @return The float value representing the interpolated value between the keyframes
     */
    private float interpolate(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
        float difference = nextFrame.getValue() - previousFrame.getValue();

        return progression * difference + previousFrame.getValue();
    }

    /**
     * Resets the animation timer to 0
     */
    public void resetAnimation() {
        animationTime = 0;
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