package engine.util;

import org.joml.Vector3f;

public class Spline {

    private Vector3f[] p;
    private Vector3f v1, v2;
    private float t;
    private Vector3f result;

    /**
     * Creates a empty spline.
     * Call {@link Spline#setup} to define points
     */
    public Spline() {
    }

    /**
     * Creates a spline with the given points.
     * @param p1 The start point of the spline
     * @param p2 The control point of the spline
     * @param p3 The end point of the spline
     */
    public Spline(Vector3f p1, Vector3f p2, Vector3f p3) {
        setup(p1, p2, p3);
    }

    /**
     * Sets the points for the spline
     * @param p1 The start point of the spline
     * @param p2 The control point of the spline
     * @param p3 The end point of the spline
     */
    public void setup(Vector3f p1, Vector3f p2, Vector3f p3) {
        p = new Vector3f[3];
        // Base Initialization
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        t = 0;
        // V1
        v1 = new Vector3f(p1).mul(2);
        v1.sub(new Vector3f(p2).mul(4));
        v1.add(new Vector3f(p3).mul(2));
        // V2
        v2 = new Vector3f(p1).mul(-2);
        v2.add(new Vector3f(p2).mul(2));
        // Set default result
        result = new Vector3f(p[0]);
    }

    /**
     * Updates the current position on the spline with given speed.
     * @param speed The speed at which to advance on the spline
     * @return The remaining distance to travel on the spline between
     * 0 and 1
     */
    public float update(float speed) {
        float remaining = -1;
        if (p[0] != null && p[1] != null && p[2] != null) {
            Vector3f divisor = (new Vector3f(v1).mul(t)).add(v2);

            t += speed / divisor.length();
            if (t >= 1) {
                remaining = t - 1.0f;
                remaining = remaining * divisor.length();
                t = 1;
            } else {
                remaining = 0;
            }

            result = new Vector3f(p[0]).mul((1 - t) * (1 - t));
            result.add(new Vector3f(p[1]).mul(2 * t * (1 - t)));
            result.add(new Vector3f(p[2]).mul(t * t));
        }

        return remaining;
    }

    /**
     * Gets the current position on the spline.
     * @return The current position
     */
    public Vector3f getResult() {
        return result;
    }

    /**
     * Returns whether moving over the spline is finished.
     * @return true if finished, false otherwise
     */
    public boolean isDone() {
        if (t == 1) return true;
        else return false;
    }
}