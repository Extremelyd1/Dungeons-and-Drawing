package engine.entities;

import graphics.Mesh;
import org.joml.Vector3f;

/**
 * LivingEntity is a {@link Entity} that has movement properties
 */
public class LivingEntity extends Entity {

    private float speed;

    public LivingEntity(Mesh mesh) {
        super(mesh);
        speed = 1;
    }

    public LivingEntity(Mesh mesh, Vector3f position, Vector3f rotation) {
        super(mesh, position, rotation);
        speed = 1;
    }

    public LivingEntity(Mesh mesh, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, position, rotation);
        this.speed = speed;
    }

    public LivingEntity(Mesh mesh, Vector3f position, Vector3f rotation, float scale, float speed) {
        super(mesh, position, rotation, scale);
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

}
