package engine.entities;

import engine.animation.Animator;
import engine.util.AssetStore;
import game.action.Action;
import graphics.Mesh;
import org.joml.Vector3f;

public class LockEntity extends MultiAnimatedEntity {

    private Action removeAction;
    private boolean startRemove;
    private float startHeight;

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
    }

    public void remove(Action action) {
        if (!startRemove) {
            startRemove = true;
            removeAction = action;
        }
    }

    public void update(float delta) {
        if (startRemove) {
            position.y = startHeight + animators[1].update(delta);

            if (animators[1].hasEnded()) {
                removeAction.execute();
            }
        }
    }
}
