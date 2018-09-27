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
}
