package engine.util;

import org.joml.Vector3f;

public class Spline {
    Vector3f[] p = new Vector3f[3];
    float t;

    public Spline(Vector3f p1, Vector3f p2, Vector3f p3) {
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        t= 0;
    }

    public Vector3f getPoint(float t) {
        Vector3f result = new Vector3f(p[0]).mul((1-t)*(1-t));
        result.add(new Vector3f(p[1]).mul(2*t*(1-t)));
        result.add(new Vector3f(p[2]).mul(t * t));
        return result;
    }
}
