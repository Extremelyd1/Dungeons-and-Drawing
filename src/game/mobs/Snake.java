package game.mobs;

import game.map.Map;
import graphics.Mesh;
import org.joml.Vector3f;

public class Snake extends SimpleMob {
    private float morph = 0.0f;

    public Snake(Mesh mesh, Map map) {
        super(mesh, map);
    }

    public Snake(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, map, position, rotation);
    }

    public Snake(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, map, position, rotation, speed);
    }

    public Snake(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float scale, float speed) {
        super(mesh, map, position, rotation, scale, speed);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (super.isMoving) {
            morph += 3.1f * (delta * getSpeed());
            if (morph >= 100) morph = 0.0f;
        }
    }

    public float getMorph() { return morph; }
}
