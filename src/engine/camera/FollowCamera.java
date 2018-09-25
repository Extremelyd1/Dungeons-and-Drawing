package engine.camera;

import engine.entities.GameEntity;
import org.joml.Vector3f;

/**
 * This camera follows a certain entity.
 */
public class FollowCamera extends Camera {

    private Vector3f offset;
    private GameEntity entity;

    public FollowCamera(GameEntity entity) {
        this.entity = entity;
    }

    public FollowCamera(GameEntity entity, Vector3f rotation) {
        super(entity.getPosition(), rotation);
        this.entity = entity;
        this.offset = new Vector3f(0, 0, 0);
    }

    public FollowCamera(GameEntity entity, Vector3f rotation, Vector3f offset) {
        super(entity.getPosition(), rotation);
        this.entity = entity;
        this.offset = offset;
    }

    /**
     * Update the position of the camera to match the position of the entity
     */
    @Override
    public void update() {
        // Make sure to COPY the vector and not copy the reference
        position = new Vector3f(entity.getPosition()).add(offset);
    }
}
