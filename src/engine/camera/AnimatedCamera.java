package engine.camera;

import engine.util.Spline;
import org.joml.Vector3f;

/**
 * This camera follows a set of predefined points
 */
public class AnimatedCamera extends Camera {

    private Vector3f[] points;
    private float speed;

    private Spline spline;
    private int index = 0;

    /**
     * Constructs a camera object that follows the given spline
     * with given rotation
     *
     * @param points   a list of points that the camera should use to construct splines
     * @param rotation the camera rotation
     */
    public AnimatedCamera(Vector3f[] points, Vector3f rotation) {
        this(points, rotation, 1);
    }

    /**
     * Constructs a camera object that follows the given spline
     * with given rotation and given speed
     *
     * @param points   a list of points that the camera should use to construct splines
     * @param rotation the camera rotation
     * @param speed    the speed of the camera movement
     */
    public AnimatedCamera(Vector3f[] points, Vector3f rotation, float speed) {
        super(points[0], rotation);
        this.points = points;
        this.speed = speed;
    }

    /**
     * Update the position of the camera to follow the set of points
     */
    @Override
    public void update(float delta) {
        if (spline == null || spline.isDone()) {
            Vector3f p1 = getNextPoint();
            ++index;
            Vector3f p2 = getNextPoint();
            ++index;
            Vector3f p3 = getNextPoint();
            spline = new Spline(p1, p2, p3);
        }

        spline.update(delta * speed);
        setPosition(spline.getResult());
    }

    /**
     * Gets the next point of the sequence.
     * Wrap back to zero if index exceeds length.
     * @return The next point of the sequence
     */
    private Vector3f getNextPoint() {
        index %= points.length;
        return new Vector3f(points[index]);
    }
}
