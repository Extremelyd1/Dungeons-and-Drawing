package engine;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput extends GLFWKeyCallback {

    public static boolean[] keys = new boolean[65536];

    // The GLFWKeyCallback class is an abstract method that
    // can't be instantiated by itself and must instead be extended
    //
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
    }
}