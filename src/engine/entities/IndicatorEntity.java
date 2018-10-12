package engine.entities;

import engine.animation.Animation;
import engine.animation.Animator;
import engine.util.AssetStore;
import game.action.Action;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector3f;

public class IndicatorEntity extends MultiAnimatedEntity {

    Tile tile;

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
    }

    public void remove(Action action) {
        if (!startRemove) {
            startRemove = true;
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
            position.y = heightValue;
        }
    }

    public Tile getTile() {
        return tile;
    }

}
