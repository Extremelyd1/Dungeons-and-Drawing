package engine.entities;

import engine.animation.Animator;
import engine.util.AssetStore;
import game.action.Action;
import graphics.Mesh;
import org.joml.Vector3f;

public class LockEntity extends MultiAnimatedEntity {

    private Action removeAction;
    private Action halfwayAction;
    private boolean startRemove;
    private float startHeight;
    private float startRotation;

    public LockEntity(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(
                mesh,
                position,
                rotation,
                scale,
                new Animator[]{
                        AssetStore.getAnimator("rotatingLock"),
                        AssetStore.getAnimator("flyingLock")
                }
        );

        this.startRemove = false;
        this.startHeight = position.y;
        this.startRotation = rotation.x;
    }

    public void remove(Action action) {
        if (!startRemove) {
            startRemove = true;
            removeAction = action;
        }
    }

    public void halfway(Action action) {
        halfwayAction = action;
    }

    public void update(float delta) {
        if (startRemove) {
            rotation.x = startRotation + animators[0].update(delta);
            position.y = startHeight + animators[1].update(delta);

            if (animators[1].isHalfway()) {
                halfwayAction.execute();
            }

            if (animators[1].hasEnded()) {
                removeAction.execute();
            }
        }
    }
}
