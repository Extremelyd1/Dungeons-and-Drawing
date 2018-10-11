package engine.animation;

import engine.animation.keyframe.KeyFrame;

public class TrigonometricAnimator extends Animator {

    public TrigonometricAnimator(Animation animation) {
        this(animation, true);
    }

    public TrigonometricAnimator(Animation animation, boolean loop) {
        this(animation, loop, true);
    }

    public TrigonometricAnimator(Animation animation, boolean loop, boolean start) {
        super(animation, loop, start);
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
            return animation.getKeyFrames()[animation.getKeyFrames().length - 1].getValue();
        }
        increaseAnimationTime(delta);
        return calculateCurrentValue();
    }

    /**
     * Increases the current animation time which allows the animation to
     * progress. If the current animation has reached the end then the timer is
     * reset, causing the animation to loop.
     */
    protected void increaseAnimationTime(float delta) {
        animationTime += delta;
        if (animationTime > animation.getLength()) {
            if (loop) {
                this.animationTime %= animation.getLength();
            } else {
                this.animationTime = animation.getLength();
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
    protected float calculateCurrentValue() {
        KeyFrame[] frames = getPreviousAndNextFrames();
        float progression = calculateProgression(frames[0], frames[1]);
        return interpolate(frames[0], frames[1], progression);
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
    protected float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
        float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
        float currentTime = animationTime - previousFrame.getTimeStamp();
        return currentTime / totalTime;
    }

    /**
     * Calculates the interpolation value between these keyframe with given
     * progression according to a sine function.
     *
     * @param previousFrame - the previous keyframe in the animation.
     * @param nextFrame     - the next keyframe in the animation.
     * @param progression   - a number between 0 and 1 indicating how far between the
     *                      previous and next keyframes the current animation time is.
     * @return The float value representing the interpolated value between the keyframes
     */
    protected float interpolate(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
        float difference = nextFrame.getValue() - previousFrame.getValue();

        return (((float) Math.cos(progression * Math.PI) - 1) /  -2) * difference + previousFrame.getValue();
    }

}
