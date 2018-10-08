package engine.entities;

import engine.animation.Animation;
import graphics.Mesh;
import org.joml.Vector3f;

public class RotatingEntity extends AnimatedEntity {

    public RotatingEntity(Mesh mesh, Vector3f position, Animation animation) {
        this(mesh, position, new Vector3f(0), animation);
    }

    public RotatingEntity(Mesh mesh, Vector3f position, Vector3f rotation, Animation animation) {
        super(mesh, position, rotation, animation);
    }

    public RotatingEntity(Mesh mesh, Vector3f position, Vector3f rotation, Animation animation, float scale) {
        super(mesh, position, rotation, animation, scale);
    }

    @Override
    public void update(float delta) {
        float value = animation.update(delta);
        rotation.y = value;
    }

}
