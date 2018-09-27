package engine.camera;

import engine.entities.Entity;
import org.joml.Vector3f;

/**
 * This camera follows a certain entity.
 */
public class FollowCamera extends Camera {

    private Vector3f offset;
    private Entity entity;

    public FollowCamera(Entity entity) {
        super(entity.getPosition(), new Vector3f(0, 0, 0));
        this.entity = entity;
        this.offset = new Vector3f(0, 0, 0);
    }

    public FollowCamera(Entity entity, Vector3f rotation) {
        super(entity.getPosition(), rotation);
        this.entity = entity;
        this.offset = new Vector3f(0, 0, 0);
    }

    public FollowCamera(Entity entity, Vector3f rotation, Vector3f offset) {
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
