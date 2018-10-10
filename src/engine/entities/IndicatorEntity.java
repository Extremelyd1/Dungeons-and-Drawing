package engine.entities;

import engine.animation.Animation;
import engine.animation.Animator;
import engine.util.AssetStore;
import graphics.Mesh;
import org.joml.Vector3f;

public class IndicatorEntity extends MultiAnimatedEntity {

    public IndicatorEntity(Mesh mesh, Vector3f position) {
        this(mesh, position, new Vector3f(0));
    }

    public IndicatorEntity(Mesh mesh, Vector3f position, Vector3f rotation) {
        this(mesh, position, rotation, 1);
    }

    public IndicatorEntity(Mesh mesh, Vector3f position, Vector3f rotation, float scale) {
        super(
                mesh,
                position,
                rotation,
                scale,
                new Animator[] {
                        AssetStore.getAnimator("indicatorRotation"),
                        AssetStore.getAnimator("indicatorMovement")
                }
        );
    }

    @Override
    public void update(float delta) {
        float rotationValue = animators[0].update(delta);
        rotation.y = rotationValue;

        float heightValue = animators[1].update(delta);
        position.y = heightValue;
    }

}
