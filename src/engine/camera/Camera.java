package engine.camera;

import org.joml.Vector3f;

/**
 * Basic camera class which provides all required methods and variables to support camera
 * calculations.
 */
public class Camera {

    /**
     * Position of the camera in world coordinates
     */
    protected Vector3f position;
    /**
     * Rotation of the camera
     */
    protected Vector3f rotation;

    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    /**
     * Moves the camera relatively to its rotation and position, i.e. if the camera moves forward,
     * it moves along the view vector.
     *
     * @param offsetX Offset in the x direction
     * @param offsetY Offset in the y direction (up/down)
     * @param offsetZ Offset in the z direction
     */
    public void moveRelative(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    /**
     * Update the camera during the update cycle, if necessary
     */
    public void update(float delta) {
    }
}
