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

    public MultiAnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, Animator[] animators) {
        super(mesh, position, rotation);
        this.animators = animators;
    }

}
