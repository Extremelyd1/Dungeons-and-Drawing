package engine.entities;

import engine.animation.Animation;
import engine.animation.Animator;
import engine.animation.LinearAnimator;
import engine.animation.keyframe.KeyFrame;
import engine.util.AssetStore;
import game.action.Action;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector3f;

public class IndicatorEntity extends MultiAnimatedEntity {

    Tile tile;

    private final float startHeight;

    private boolean startRemove = false;
    private Action removeAction;

    public IndicatorEntity(Mesh mesh, Vector3f position, Tile tile) {
        this(mesh, position, new Vector3f(0), tile);
    }

    public IndicatorEntity(Mesh mesh, Vector3f position, Vector3f rotation, Tile tile) {
        this(mesh, position, rotation, 1, tile);
    }

    public IndicatorEntity(Mesh mesh, Vector3f position, Vector3f rotation, float scale, Tile tile) {
        super(
                mesh,
                position,
                rotation,
                scale,
                new Animator[] {
                        AssetStore.getAnimator("indicatorRotation"),
                        AssetStore.getAnimator("indicatorMovement"),
                        AssetStore.getAnimator("indicatorRemove")
                }
        );
        this.tile = tile;
        this.startHeight = position.y;
    }

    public void remove(Action action) {
        if (!startRemove) {
            startRemove = true;
            KeyFrame[] keyFrames = new KeyFrame[2];
            keyFrames[0] = new KeyFrame(0f, position.y);
            keyFrames[1] = new KeyFrame(3f, -2f);

            Animation animation = new Animation(3f, keyFrames);
            animators[2] = new LinearAnimator(animation, false, false);
            animators[2].start();

            removeAction = action;
        }
    }

    @Override
    public void update(float delta) {
        float rotationValue = animators[0].update(delta);
        rotation.y = rotationValue;

        if (startRemove) {
            float heightValue = animators[2].update(delta);
            position.y = heightValue;
            if (animators[2].hasEnded()) {
                removeAction.execute();
            }
        } else {
            float heightValue = animators[1].update(delta);
            position.y = startHeight + heightValue;
        }
    }

    public Tile getTile() {
        return tile;
    }

}
