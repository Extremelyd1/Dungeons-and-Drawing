package engine.entities;

import engine.animation.Animation;
import engine.util.AssetStore;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector3f;

public class DoorEntity extends AnimatedEntity {

    private final Tile tile;
    private final boolean invertedRotation;

    private boolean started = false;
    private boolean ended = false;

    public DoorEntity(Mesh mesh, Vector3f position, Vector3f rotation, Tile tile) {
        this(mesh, position, rotation, 1f, tile);
    }

    public DoorEntity(Mesh mesh, Vector3f position, Vector3f rotation, float scale, Tile tile) {
        this(mesh, position, rotation, scale, tile, false);
    }

    public DoorEntity(Mesh mesh, Vector3f position, Vector3f rotation, float scale, Tile tile, boolean invertedRotation) {
        super(mesh, position, rotation, scale, AssetStore.getAnimator("door"));
        this.tile = tile;
        this.invertedRotation = invertedRotation;
    }

    public void open() {
        if (!started) {
            animator.start();
            started = true;
        }
    }

    @Override
    public void update(float delta) {
        if (ended || !started) {
            return;
        }
        float value = animator.update(delta);
        rotation.y = value * (invertedRotation ? 1 : -1);
        if (animator.hasEnded()) {
            tile.setSolid(false);
            ended = true;
        }
    }

}
