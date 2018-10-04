package engine.util;

import org.joml.Vector3f;

public class ColorInterpolator {
    private float progress = 0;
    private Vector3f from, to;
    private Vector3f result;
    private float speed = 1.0f;
    private boolean isDone = true;

    public void setInterpolation(Vector3f from, Vector3f to, float speed) {
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.isDone = false;
        this.progress = 0.0f;
    }

    public boolean updateColor(float interval) {
        progress += speed / interval;
        if (progress >= 1.0f) {
            progress = 1.0f;
            isDone = true;
        } else {
            isDone = false;
        }
        result = new Vector3f(from).mul(1.0f - progress);
        result.add(new Vector3f(to).mul(progress));
        return isDone;
    }

    public Vector3f getInterpolationResult() {
        return result;
    }

    public boolean getStatus(){
        return isDone;
    }
}
