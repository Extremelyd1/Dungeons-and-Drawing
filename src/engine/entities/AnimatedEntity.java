package engine.entities;

import engine.animation.Animator;
import graphics.Mesh;
import org.joml.Vector3f;

/**
 * An entity that has a single animation
 */
public abstract class AnimatedEntity extends Entity {

    protected Animator animator;

    public AnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, Animator animator) {
        super(mesh, position, rotation);
        this.animator = animator;
    }

    public AnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, float scale, Animator animator) {
        super(mesh, position, rotation, scale);
        this.animator = animator;
    }

    public AnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale, Animator animator) {
        super(mesh, position, rotation, scale);
        this.animator = animator;
    }

}
