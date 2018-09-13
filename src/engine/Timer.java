
package engine;

/**
 * This class handles all time related tasks that our game needs
 * 
 * @author Cas Wognum (TU/e, 1012585)
 */
public class Timer {
    
    private double lastLoopTime;
    
    /** Initializes {@code lastLoopTime} to the current system time */
    public void init() {
        lastLoopTime = getTime();
    }

    /**
     * Returns the time in seconds since the system was turned on
     * 
     * @return {@code System.nanoTime() / 1000_000_000.0}
     */
    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    /**
     * Return the amount of time, in seconds, that was elapsed between two
     * subsequent calls to this method. 
     * 
     * @return {@code getTime() - lastLoopTime}
     */
    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    /** 
     * Return the last time stamp, in seconds, since the game loop was updated
     * 
     * @return {@code lastLoopTime} 
     */
    public double getLastLoopTime() {
        return lastLoopTime;
    }
}