package game.mobs;

import engine.entities.LivingEntity;
import game.map.Map;
import graphics.Mesh;
import org.joml.Vector3f;

public class SimpleMob extends LivingEntity {
    public SimpleMob(Mesh mesh, Map map) {
        super(mesh, map);
    }

    public SimpleMob(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, map, position, rotation);
    }

    public SimpleMob(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, map, position, rotation, speed);
    }

    public SimpleMob(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float scale, float speed) {
        super(mesh, map, position, rotation, scale, speed);
    }


}
