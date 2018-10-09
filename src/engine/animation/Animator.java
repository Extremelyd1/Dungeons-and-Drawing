package engine.animation;

import engine.animation.keyframe.KeyFrame;

public abstract class Animator {

    protected final Animation animation;

    protected final boolean loop;

    protected boolean running;
    protected float animationTime;

    public Animator(Animation animation) {
        this(animation, true);
    }

    public Animator(Animation animation, boolean loop) {
        this(animation, loop, true);
    }

    public Animator(Animation animation, boolean loop, boolean start) {
        this.animation = animation;
        this.loop = loop;
        this.running = start;
        this.animationTime = 0;
    }

    /**
     * Starts the animation, if not running already
     */
    public void start() {
        if (!running) {
            running = true;
            animationTime = 0;
        }
    }

    /**
     * Checks whether the animation has ended
     * @return whether the animation has ended
     */
    public boolean hasEnded() {
        if (!loop && !running) {
            return true;
        }
        return false;
    }

    /**
     * Resets the animation timer to 0
     */
    public void resetAnimation() {
        running = false;
        animationTime = 0;
    }

    /**
     * This method should be called each frame to update the animation currently
     * being played. This increases the animation time (and loops it back to
     * zero if necessary), finds the pose that the entity should be in at that
     * time of the animation, and then applies that pose to all the model's
     * joints by setting the joint transforms.
     */
    public abstract float update(float delta);

    /**
     * Increases the current animation time which allows the animation to
     * progress. If the current animation has reached the end then the timer is
     * reset, causing the animation to loop.
     */
    protected abstract void increaseAnimationTime(float delta);

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
    protected abstract float calculateCurrentValue();

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
    protected KeyFrame[] getPreviousAndNextFrames() {
        KeyFrame previousFrame = animation.getKeyFrames()[0];
        KeyFrame nextFrame = animation.getKeyFrames()[0];
        for (int i = 1; i < animation.getKeyFrames().length; i++) {
            nextFrame = animation.getKeyFrames()[i];
            if (nextFrame.getTimeStamp() >= animationTime) {
                break;
            }
            previousFrame = animation.getKeyFrames()[i];
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
    protected abstract float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame);

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
    protected abstract float interpolate(KeyFrame previousFrame, KeyFrame nextFrame, float progression);

}
