package engine.camera;

import engine.GameWindow;
import engine.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Special type of camera that can be moved using the keyboard and mouse.
 */
public class FreeCamera extends Camera {

    private static final float CAMERA_POS_STEP = 0.10f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    /**
     * Read the input and move the camera
     *
     * @param window     Window object for getting input
     * @param mouseInput Mouse input
     */
    public void handleInput(GameWindow window, MouseInput mouseInput) {
        Vector3i cameraInc = new Vector3i(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W) || window.isKeyPressed(GLFW_KEY_UP)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S) || window.isKeyPressed(GLFW_KEY_DOWN)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A) || window.isKeyPressed(GLFW_KEY_LEFT)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D) || window.isKeyPressed(GLFW_KEY_RIGHT)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }

        // Update camera position
        this.moveRelative(
                cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP
        );

        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            this.getRotation().add(
                    new Vector3f(
                            rotVec.x * MOUSE_SENSITIVITY,
                            rotVec.y * MOUSE_SENSITIVITY,
                            0
                    )
            );
        }
    }
}
