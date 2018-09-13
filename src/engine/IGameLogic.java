package engine;

/**
 * Interface for the classes that define and implement the logic of a certain
 * game that uses this game engine. 
 * 
 * @author Cas Wognum (TU/e, 1012585)
 */
public interface IGameLogic {

    /** 
     * Initialize game variables and state at launch time 
     * @param window The window in which all graphics are displayed
     * @throws Exception if initialization was not possible 
     */
    void init(GameWindow window) throws Exception;
    
    /**
     * Gather and start processing the user input
     * @param window the game window 
     * @param mouseInput 
     */
    void input(GameWindow window, MouseInput mouseInput);
    
    /**
     * Update the game state and variables
     * @param interval interval at which it is being updated
     * @param mouseInput
     */
    void update(float interval, MouseInput mouseInput);
    
    /**
     * Update the graphics 
     * @param window the game window
     */
    void render(GameWindow window);
    
    /** Free up resources that are no longer needed */
    void terminate();
}
