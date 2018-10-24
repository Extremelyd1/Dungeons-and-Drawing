package engine;

import game.LevelController;
import game.LevelControllerValidation;
import pathfinding.Pathfinding_Validator;

/**
 * Main class of this Java Application for the game produced for
 * the course 2IOE0 Interactive Intelligent Systems Q1 year 2018/2019
 * at the University of Eindhoven.
 * <p>
 * This application uses three libraries
 * <b> Java Light Weight Gaming Library (LWJGL) </b>
 * A low level API that gives access to, among others, OpenGL functionality.
 *
 * <b> Java OpenGL Math Library (JOML)</b>
 * Provides vector and matrix operations
 *
 * <b> PNG Decoder </b>
 * To load and process a PNG image and its attributes to be used as a texture
 *
 * <b> Deep Learning 4 Java (dl4j) </b>
 * dl4j is used to perform the image recognition.
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

        if (args.length > 0 && args[0].equals("-validator=a_star")) {
            // Startup A* validator
            (new Pathfinding_Validator()).run();
        } else if (args.length > 0 && args[0].equals("-validator=light-animation")) {
            // Startup light and animation validators
            (new GameEngine(new LevelControllerValidation())).start();
        } else {
            // Load the game
            try {
                (new GameEngine(new LevelController())).start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(-1);
            }
        }

    }
}
