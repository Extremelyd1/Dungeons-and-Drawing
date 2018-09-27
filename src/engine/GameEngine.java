package engine;

import engine.util.Timer;
import org.lwjgl.*;

/** 
 * This class implements the main game loop in a Runnable Interface: 
 * Initialize (-> Update -> Render)* -> Terminate. 
 * It is executed on a new thread. 
 *
 *
 * @author Cas Wognum (TU/e, 1012585)
 */
public class GameEngine implements Runnable {

    // Display extra information if in debug mode to find errors more easily
    public static final boolean DEBUG_MODE = true; 
    public static final int TARGET_UPS = 60; // updates per second
    public static final int TARGET_FPS = 120; // frames per second
    
    // Threads
    private Thread thread;   
    private boolean isRunning = false; 
    
    // Game logic
    private final Timer timer;
    private final IGameLogic gameLogic;
    
    // Input / Output
    private final MouseInput mouseInput;
   
    /**
     * Constructor that starts the game engine
     * @param gameLogic the (logic of the) game we want to run
     */
    public GameEngine(IGameLogic gameLogic) {
        this.gameLogic = gameLogic; 
        timer = new Timer(); 
        mouseInput = new MouseInput();
    }
    
    /**
     * Starts the game application on a new thread. 
     * Calls {@link GameEngine#run()}
     */
    public synchronized void start() {

        if (isRunning) {
            return; 
        }
        isRunning = true; 
        thread = new Thread(this, "GAME_LOOP_THREAD");

        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            thread.run();
        } else {
            thread.start();
        }
    }

    /**
     * Contains the main structure for the game loop. It initializes all
     * components, runs the game loop and eventually terminates all components
     * and thus terminates the program. 
     */
    @Override
    public void run() {   
        
        try {
            initialize();
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            terminate();
        }        
    }
    
    /** Initialize all game components */
    private void initialize() throws Exception {
        if (DEBUG_MODE) {
            System.out.println("Debugging enabled: LWJGL " + 
                    Version.getVersion() + "!");
        }

        timer.init();
        mouseInput.init();
        gameLogic.init();
    }

    /** Update all game components with a given frequency */
    private void loop() {      
        
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        isRunning = true; 
        while (isRunning && !GameWindow.getGameWindow().shouldClose()) {
            // Store the start time of the iteration
            double iterationStartTime = timer.getTime();
            // Calculate the time that elapsed since the previous game iteration
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(elapsedTime);
                accumulator -= interval;
            }
            render();
            // If the iteration did not take the expected time, let the thread sleep
            // for the remaining time
            sync(iterationStartTime);
        }
    }

    /** Let the thread sleep the rest of how long the game iteration should have lasted */
    private void sync(double loopStartTime) {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = loopStartTime + loopSlot;
        while(timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                // TODO: Do something with exception?
            }
        }
    }
    
    /** Gather and start processing the user input */
    protected void input() {
        mouseInput.input();
        gameLogic.input(mouseInput);
    }
    
    /** 
     * Update the game state
     * @param delta the frequence at which to render
     */
    protected void update(float delta) {
        gameLogic.update(delta, mouseInput);
    }
    
    /** Update the game graphics */
    protected void render() {
        GameWindow window = GameWindow.getGameWindow();
        gameLogic.render();
        window.render();
    }
    
    /** Terminate all game components */
    protected void terminate() {
        gameLogic.terminate();
        GameWindow.getGameWindow().terminate();
    }
}

