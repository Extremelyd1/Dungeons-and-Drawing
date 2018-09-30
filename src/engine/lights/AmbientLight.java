package engine.lights;

import org.joml.Vector3f;

public class AmbientLight {

    private Vector3f light;

    public AmbientLight(Vector3f light) {
        this.light = light;
    }

    public AmbientLight() {
        this(new Vector3f(0.3f, 0.3f, 0.3f));
    }

    public Vector3f getLight() {
        return light;
    }
}
