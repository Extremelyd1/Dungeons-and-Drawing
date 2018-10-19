package engine.animation;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * Represents the local bone-space transform of a joint at a certain keyframe
 * during an animation. This includes the position and rotation of the joint,
 * relative to the parent joint (for the root joint it's relative to the model's
 * origin, seeing as the root joint has no parent). The transform is stored as a
 * position vector and a quaternion (rotation) so that these values can be
 * easily interpolated, a functionality that this class also provides.
 *
 * @author Karl
 *
 */

public class JointTransform {

    // remember, this position and rotation are relative to the parent bone!
//    private final Vector3f position;
    private final Quaternion rotation;

    /**
     *
     * @param rotation
     *            - the rotation of the joint relative to the parent joint
     *            (bone-space) at a certain keyframe.
     */
    public JointTransform(Quaternion rotation) {
//        this.position = position;
        this.rotation = rotation;
    }

    /**
     * In this method the bone-space transform matrix is constructed by
     * translating an identity matrix using the position variable and then
     * applying the rotation. The rotation is applied by first converting the
     * quaternion into a rotation matrix, which is then multiplied with the
     * transform matrix.
     *
     * @return This bone-space joint transform as a matrix. The exact same
     *         transform as represented by the position and rotation in this
     *         instance, just in matrix form.
     */
    public Matrix4f getLocalTransform() {
        Matrix4f matrix = new Matrix4f();
        matrix.mul(rotation.toRotationMatrix());
        return matrix;
    }

    /**
     * Interpolates between two transforms based on the progression value. The
     * result is a new transform which is part way between the two original
     * transforms. The translation can simply be linearly interpolated, but the
     * rotation interpolation is slightly more complex, using a method called
     * "SLERP" to spherically-linearly interpolate between 2 quaternions
     * (rotations). This gives a much much better result than trying to linearly
     * interpolate between Euler rotations.
     *
     * @param frameA
     *            - the previous transform
     * @param frameB
     *            - the next transform
     * @param progression
     *            - a number between 0 and 1 indicating how far between the two
     *            transforms to interpolate. A progression value of 0 would
     *            return a transform equal to "frameA", a value of 1 would
     *            return a transform equal to "frameB". Everything else gives a
     *            transform somewhere in-between the two.
     * @return
     */
    protected static JointTransform interpolate(JointTransform frameA, JointTransform frameB, float progression) {
        Quaternion rot = Quaternion.interpolate(frameA.rotation, frameB.rotation, progression);
        return new JointTransform(rot);
    }

}