package engine.entities;

import game.map.Map;
import graphics.Mesh;
import org.joml.Vector3f;

/**
 * LivingEntity is a {@link Entity} that has movement properties
 */
public class LivingEntity extends Entity {

    private float speed;
    private Map map;

    public LivingEntity(Mesh mesh, Map map) {
        super(mesh);
        this.map = map;
        speed = 1;
    }

    public LivingEntity(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, position, rotation);
        this.map = map;
        speed = 1;
    }

    public LivingEntity(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, position, rotation);
        this.map = map;
        this.speed = speed;
    }

    public LivingEntity(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float scale, float speed) {
        super(mesh, position, rotation, scale);
        this.map = map;
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setMap(Map map) { this.map = map; }

    public Map getMap() {
        return map;
    }

}
