package engine.entities;

import graphics.Mesh;
import org.joml.Vector3f;

/**
 * Enemy class, the NPC of the game that the player tries to avoid
 */
public class Enemy extends LivingEntity {

    public Enemy(Mesh mesh){
        super(mesh);
    }

    public Enemy(Mesh mesh, Vector3f position, Vector3f rotation) {
        super(mesh, position, rotation);
    }

    public Enemy(Mesh mesh, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, position, rotation, speed);
    }

    public Enemy(Mesh mesh, Vector3f position, Vector3f rotation, float speed, float scale) {
        super(mesh, position, rotation, speed, scale);
    }

}
