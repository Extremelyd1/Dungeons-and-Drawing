package engine;

import game.LevelController;
import game.state.SandboxTestLevel;
import sun.security.ssl.Debug;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * Main class of this Java Application to test the Light Weight Java Gaming
 * Library (LWJGT) and to to explore whether it can be applied for the
 * course 2IOE0 Interactive Intelligent Systems.
 * <p>
 * This application uses two libraries
 * <b> Java Light Weight Gaming Library (LWJGL) </b>
 * A low level API that gives access to, among others, OpenGL functionality.
 *
 * <b> Java OpenGL Math Library (JOML)</b>
 * Provides vector and matrix operations
 *
 * <b> PNG Decoder </b>
 * To load and process a PNG image and its attributes to be used as a texture
 *
 * @author Yannick Scheepers (TU/e, 1002370)
 * @author Erik Ussin (TU/e, 1034012)
 * @author Casper Smits(TU/e, 1034012)
 * @author Koen Degeling (TU/e, 1018025)
 * @author Valeriya Prokopova (TU/e, 1033287)
 * @author Cas Wognum (TU/e, 1012585)
 */
public class Launcher {

    /**
     * <h1> Program executions starts here </h1>
     * Tries to create an instance of a class encompassing all game logic
     * and starts the game engine with this class as a parameter. Upon failure
     * the program execution is immediately stopped
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Debug.println("PID", ManagementFactory.getRuntimeMXBean().getName());
            //TimeUnit.SECONDS.sleep(8);
            (new GameEngine(new LevelController())).start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }

    }
}
