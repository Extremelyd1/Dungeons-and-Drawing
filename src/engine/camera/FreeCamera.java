package engine.camera;

import engine.GameWindow;
import engine.MouseInput;
import engine.input.KeyBinding;
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
     * @param mouseInput Mouse input
     */
    public void handleInput(MouseInput mouseInput) {
        GameWindow window = GameWindow.getGameWindow();
        Vector3i cameraInc = new Vector3i(0, 0, 0);
        if (KeyBinding.isForwardPressed()) {
            cameraInc.z = -1;
        } else if (KeyBinding.isBackwardPressed()) {
            cameraInc.z = 1;
        }
        if (KeyBinding.isLeftPressed()) {
            cameraInc.x = -1;
        } else if (KeyBinding.isRightPressed()) {
            cameraInc.x = 1;
        }
        if (KeyBinding.isUpPressed()) {
            cameraInc.y = -1;
        } else if (KeyBinding.isDownPressed()) {
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
