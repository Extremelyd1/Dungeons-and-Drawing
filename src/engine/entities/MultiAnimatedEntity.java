package engine.entities;

import engine.animation.Animation;
import engine.animation.Animator;
import graphics.Mesh;
import org.joml.Vector3f;

/**
 * A entity that has multiple animations associated to it.
 * For instance, an entity that has a rotation and moving animation.
 */
public abstract class MultiAnimatedEntity extends Entity {

    protected Animator[] animators;

    public MultiAnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, float scale, Animator[] animators) {
        super(mesh, position, rotation, scale);
        this.animators = animators;
    }

    public MultiAnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale, Animator[] animators) {
        super(mesh, position, rotation, scale);
        this.animators = animators;
    }
}
