package engine.util;

import org.joml.Vector3f;
import sun.security.ssl.Debug;

public class Spline {
    Vector3f[] p = new Vector3f[3];
    Vector3f v1, v2;
    float t = 0;
    Vector3f result;
    Vector3f divisor;

    public Spline() {
    }

    public Spline(Vector3f p1, Vector3f p2, Vector3f p3) {
        setup(p1, p2, p3);
    }

    public void setup(Vector3f p1, Vector3f p2, Vector3f p3) {
        // Base Initialization
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        t= 0;
        // V1
        v1 = new Vector3f(p1).mul(2);
        v1.sub(new Vector3f(p2).mul(4));
        v1.add(new Vector3f(p3).mul(2));
        // V2
        v2 = new Vector3f(p1).mul(-2);
        v2.add(new Vector3f(p2).mul(2));
        divisor = (new Vector3f(v1).mul(t)).add(v2);
        // Set default result
        result = new Vector3f(p[0]);
    }

    public float update(float speed) {
        float remaining = -1;
        if (p[0] != null && p[1] != null && p[2] != null) {
            Vector3f divisor = (new Vector3f(v1).mul(t)).add(v2);

            if (divisor.length() != 0) {
                t += speed / divisor.length();
            } else {
                t =1;
                result = new Vector3f(p[2]);
                return 0;
            }
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
        //Debug.println("Spline", "result: " + result.toString());

        return remaining;
    }

    public Vector3f getResult() {
        return result;
    }

    public boolean isDone(){
        if (t == 1) return true;
        else return false;
    }
}