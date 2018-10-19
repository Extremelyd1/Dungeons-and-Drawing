package engine.entities.animatedModel;

import engine.animation.ModelAnimation;
import engine.animation.ModelAnimator;
import engine.entities.animatedModel.textures.AMTexture;
import engine.loader.data.Vao;
import org.joml.Matrix4f;

/**
 *
 * This class represents an player in the world that can be animated. It
 * contains the model's VAO which contains the mesh data, the texture, and the
 * root joint of the joint hierarchy, or "skeleton". It also holds an int which
 * represents the number of joints that the model's skeleton contains, and has
 * its own {@link ModelAnimator} instance which can be used to apply animations to
 * this entity.
 *
 * @author Karl
 *
 */
public class AnimatedModel {

    // skin
    private final Vao vao;
    private final AMTexture texture;

    // skeleton
    private final Joint rootJoint;
    private final int jointCount;

    private final ModelAnimator animator;

    /**
     * Creates a new entity capable of animation. The inverse bind transform for
     * all joints is calculated in this constructor. The bind transform is
     * simply the original (no pose applied) transform of a joint in relation to
     * the model's origin (model-space). The inverse bind transform is simply
     * that but inverted.
     *
     * @param vao
     *            - the mesh object containing the mesh data for this entity.
     * @param rootJoint
     *            - the root joint of the joint hierarchy which makes up the
     *            "skeleton" of the entity.
     * @param jointCount
     *            - the number of joints in the joint hierarchy (skeleton) for
     *            this entity.
     *
     */
    public AnimatedModel(Vao vao, AMTexture texture, Joint rootJoint, int jointCount) {
        this.vao = vao;
        this.texture = texture;
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new ModelAnimator(this);
        rootJoint.calcInverseBindTransform(new Matrix4f());
    }

    /**
     * @return The mesh object containing all the mesh data for this entity.
     */
    public Vao getVao() {
        return vao;
    }

    /**
     * @return The diffuse texture for this entity.
     */
    public AMTexture getTexture() {
        return texture;
    }

    /**
     * @return The root joint of the joint hierarchy. This joint has no parent,
     *         and every other joint in the skeleton is a descendant of this
     *         joint.
     */
    public Joint getRootJoint() {
        return rootJoint;
    }

    /**
     * Deletes the OpenGL objects associated with this entity, namely the model
     * (VAO) and texture.
     */
    public void delete() {
        vao.delete();
    }

    /**
     * Instructs this entity to carry out a given animation. To do this it
     * basically sets the chosen animation as the current animation in the
     * {@link ModelAnimator} object.
     *
     * @param animation
     *            - the animation to be carried out.
     */
    public void doAnimation(ModelAnimation animation) {
        animator.doAnimation(animation);
    }

    /**
     * Updates the animator for this entity, basically updating the animated
     * pose of the entity. Must be called every frame.
     */
    public void update(float delta) {
        animator.update(delta);
    }

    /**
     * Gets an array of the all important model-space transforms of all the
     * joints (with the current animation pose applied) in the entity. The
     * joints are ordered in the array based on their joint index. The position
     * of each joint's transform in the array is equal to the joint's index.
     *
     * @return The array of model-space transforms of the joints in the current
     *         animation pose.
     */
    public Matrix4f[] getJointTransforms() {
        Matrix4f[] jointMatrices = new Matrix4f[jointCount];
        addJointsToArray(rootJoint, jointMatrices);
        return jointMatrices;
    }

    /**
     * This adds the current model-space transform of a joint (and all of its
     * descendants) into an array of transforms. The joint's transform is added
     * into the array at the position equal to the joint's index.
     *
     * @param headJoint
     *            - the current joint being added to the array. This method also
     *            adds the transforms of all the descendents of this joint too.
     * @param jointMatrices
     *            - the array of joint transforms that is being filled.
     */
    private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
        jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
        for (Joint childJoint : headJoint.children) {
            addJointsToArray(childJoint, jointMatrices);
        }
    }

}