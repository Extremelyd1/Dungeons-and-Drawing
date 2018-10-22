package engine.input;

import engine.GameWindow;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class that implements the key binding
 *
 * @Author Koen Degeling (1018025)
 */
public class KeyBinding {
    private static long windowHandle = GameWindow.getGameWindow().getWindowHandle();

    private static boolean leftMousePressed = false;

    public static boolean isForwardPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_W) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_UP) == GLFW_PRESS;
    }

    public static boolean isLeftPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_A) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_LEFT) == GLFW_PRESS;
    }

    public static boolean isRightPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_D) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_RIGHT) == GLFW_PRESS;
    }

    public static boolean isBackwardPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_S) == GLFW_PRESS || glfwGetKey(windowHandle, GLFW_KEY_DOWN) == GLFW_PRESS;
    }

    public static boolean isUpPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_Z) == GLFW_PRESS;
    }

    public static boolean isDownPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_X) == GLFW_PRESS;
    }

    public static boolean isNextLevelPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_F3) == GLFW_PRESS;
    }

    public static boolean isRestartLevelPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_F2) == GLFW_PRESS;
    }

    public static boolean isPreviousLevelPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_F1) == GLFW_PRESS;
    }

    public static boolean isInteractPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_E) == GLFW_PRESS;
    }

    public static boolean isFinishedDrawingPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_ENTER) == GLFW_PRESS;
    }

    public static boolean isStartPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_SPACE) == GLFW_PRESS;
    }

    public static boolean isKeyPressed(int key) {
        return glfwGetKey(windowHandle, key) == GLFW_PRESS;
    }

    public static boolean isKeyReleased(int key) {
        return glfwGetKey(windowHandle, key) == GLFW_RELEASE;
    }

    /**
     * Checks whether the left mouse button was pressed only once. It resets when the mouse is released again.
     *
     * @return
     */
    public static boolean isLeftMousePressed() {
        // Check if the flag should reset
        if (glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_LEFT) == GLFW_RELEASE) {
            leftMousePressed = false;
        }

        // If already pressed, return false
        if (leftMousePressed) {
            return false;
        }

        // Check current mouse state
        if (glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            leftMousePressed = true;
            return true;
        }

        // Default
        return false;
    }
}
