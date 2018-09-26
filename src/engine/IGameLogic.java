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
     * @throws Exception if initialization was not possible 
     */
    void init() throws Exception;
    
    /**
     * Gather and start processing the user input
     * @param mouseInput 
     */
    void input(MouseInput mouseInput);
    
    /**
     * Update the game state and variables
     * @param interval interval at which it is being updated
     * @param mouseInput
     */
    void update(float interval, MouseInput mouseInput);
    
    /**
     * Update the graphics
     */
    void render();
    
    /** Free up resources that are no longer needed */
    void terminate();
}
