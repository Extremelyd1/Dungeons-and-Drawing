package engine.entities;

import game.map.Map;
import graphics.Mesh;
import org.joml.Vector3f;

/**
 * Enemy class, the NPC of the game that the player tries to avoid
 */
public class Enemy extends LivingEntity {

    public Enemy(Mesh mesh, Map map){
        super(mesh, map);
    }

    public Enemy(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, map, position, rotation);
    }

    public Enemy(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, map, position, rotation, speed);
    }

    public Enemy(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed, float scale) {
        super(mesh, map, position, rotation, speed, scale);
    }

}
