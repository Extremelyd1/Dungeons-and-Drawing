package engine;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Class to create, maintain and render the game window. As there will only
 * be one game window being used at any time, the class is designed according to
 * the singleton design pattern. The title, width and height of the window 
 * therefore have to be statically and manually set within this class. 
 *
 * @author Cas Wognum (TU/e, 1012585)
 */
public class GameWindow {

    private static GameWindow gameWindow; // Stores the Singleton GameWindow obj
    private long windowHandle; // Stores the GLFW Window Object
    
    // Defaults
    private final String DEFAULT_WINDOW_TITLE = "A first LWJGL game by Cas";
    private final int DEFAULT_WINDOW_WIDTH = 1280;
    private final int DEFAULT_WINDOW_HEIGHT = 720;
    
    // << If we want the window to be resizable, these variables should be used >>
    private int windowWidth; 
    private int windowHeight; 
    
    /** Private constructor */
    private GameWindow() {
        windowWidth = DEFAULT_WINDOW_WIDTH; 
        windowHeight = DEFAULT_WINDOW_HEIGHT; 
        initialize();
    }

    /**
     * Creates (if still null) and returns the Singleton GameWindow 
     * @return {@code gameWindow}
     */
    public static GameWindow getGameWindow() {
        if (gameWindow == null) {

            gameWindow = new GameWindow();
        }
        return gameWindow;
    }

    /**
     * Initialize GLFW and create a GLFW Window. 
     * 
     * @throws IllegalStateException if initialization of LWJGL fails 
     * @throws RuntimeException if creation of window fails
     */
    private void initialize() {
        
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("engine.io.GameWindow.initialize():  "
                    + "failed to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowHandle = glfwCreateWindow(
                DEFAULT_WINDOW_WIDTH, 
                DEFAULT_WINDOW_HEIGHT, 
                DEFAULT_WINDOW_TITLE, 
                NULL, NULL);

       // Check if window creation is succesful
        if (windowHandle == NULL) {
            throw new RuntimeException("engine.io.GameWindow.initialize(): "
                    + "failed to create the GLFW window");
        }
        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, (long windowParam, int key, int scancode, int action, int mods) -> {
            // If the escape key is released, close the window
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowParam, true);
            }
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    windowHandle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);
        
        // Enable v-sync (matches render frequency to the frequency of your
        // graphics card. For example: 60Hz = 60fps)
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);
        
        GL.createCapabilities();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        
        // Wireframe model
        //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
    }
    
    
    
    /** Terminates the window */
    public void terminate() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    
    
    /** Renders and updates the game components */
    public void render() {   
        glfwSwapBuffers(windowHandle); // swap the buffers (render new frame)
        glfwPollEvents(); // process all pending events
    }
    
    /**
     * Return whether a certain key is pressed down
     * 
     * @param keyCode the code of the key to check
     * @return whether the key is pressed or not
     */
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    
    /** 
     * Returns whether the window should be closed
     * @return glfwWindowShouldClose()
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }
    
    /** Set clear color in rgba. Clear color at initialization is
     *  rgba(0.0f, 0.0f, 0.0f, 0.0f), which is black and fully transparent
     * 
     * @param r red channel
     * @param g green channel
     * @param b blue channel
     * @param a alpha channel */
    public void setClearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public long getWindowHandle() {
        return windowHandle;
    }
    
        
}
