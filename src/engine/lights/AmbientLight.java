package engine.lights;

import org.joml.Vector3f;

/**
 * Ambient light implementation.
 *
 * An ambient light is a light that lights up the scene in an even way. It comes
 * from all directions and always has the same colour. Therefore, we only
 * need to specify which color the ambient light is.
 */
public class AmbientLight {

    /**
     * The light (color) itself
     */
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
