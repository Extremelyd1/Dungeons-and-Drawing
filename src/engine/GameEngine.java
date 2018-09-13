package engine;

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
    
    // Threads
    private Thread thread;   
    private boolean isRunning = false; 
    
    // Game logic
    private final Timer timer; 
    private final IGameLogic gameLogic;
    
    // Input / Output
    private final MouseInput mouseInput;
    private GameWindow window;
   
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
     * Calls {@link GameEngine.run()}
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
//            terminate();
        }        
    }
    
    /** Initialize all game components */
    private void initialize() throws Exception {
        if (DEBUG_MODE) {
            System.out.println("Debugging enabled: LWJGL " + 
                    Version.getVersion() + "!");
        }

        window = GameWindow.getGameWindow();

        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    /** Update all game components with a given frequency */
    private void loop() {      
        
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        isRunning = true; 
        while (isRunning && !window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();
        }
    }
    
    /** Gather and start processing the user input */
    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }
    
    /** 
     * Update the game state
     * @param interval the frequence at which to render
     */
    protected void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }
    
    /** Update the game graphics */
    protected void render() {
        gameLogic.render(window);
        window.render();
    }
    
    /** Terminate all game components */
    protected void terminate() {
        gameLogic.terminate(); 
        window.terminate();
    }
}

