package engine.entities;

import engine.animation.Animation;
import graphics.Mesh;
import org.joml.Vector3f;

public class AnimatedEntity extends Entity {

    Animation animation;

    public AnimatedEntity(Mesh mesh, Animation animation) {
        super(mesh);
        this.animation = animation;
    }

    public AnimatedEntity(Mesh mesh, Vector3f position, Animation animation) {
        super(mesh, position);
        this.animation = animation;
    }

    public AnimatedEntity(Mesh mesh, Vector3f position, float scale, Animation animation) {
        super(mesh, position, scale);
        this.animation = animation;
    }

    public AnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, Animation animation) {
        super(mesh, position, rotation);
        this.animation = animation;
    }

    public AnimatedEntity(Mesh mesh, Vector3f position, Vector3f rotation, Animation animation, float scale) {
        super(mesh, position, rotation, scale);
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }

}
