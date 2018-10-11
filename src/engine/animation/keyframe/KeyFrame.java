package engine.animation.keyframe;

/**
 * Represents one keyframe of an animation. This contains the timestamp of the
 * keyframe, which is the time (in seconds) from the start of the animation when
 * this keyframe occurs.
 *
 * It also contains the desired bone-space transforms of all of the joints in
 * the animated entity at this keyframe in the animation (i.e. it contains all
 * the joint transforms for the "pose" at this time of the animation.). The
 * joint transforms are stored in a map, indexed by the name of the joint that
 * they should be applied to.
 */
public class KeyFrame {

    private final float timeStamp;
    private float value;

    /**
     * @param timeStamp
     *            - the time (in seconds) that this keyframe occurs during the
     *            animation.
     */
    public KeyFrame(float timeStamp, float value) {
        this.timeStamp = timeStamp;
        this.value = value;
    }

    /**
     * @return The time in seconds of the keyframe in the animation.
     */
    public float getTimeStamp() {
        return timeStamp;
    }

    /**
     * @return The value associated with this keyframe
     */
    public float getValue() {
        return value;
    }

}