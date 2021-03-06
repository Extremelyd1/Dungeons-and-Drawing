package game;

import engine.IGameLogic;
import engine.MouseInput;
import engine.input.KeyBinding;
import engine.util.AssetStore;
import engine.util.Timer;
import game.level.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelController implements IGameLogic {

    protected List<Level> levels;
    private int active;
    private Timer timer;

    private int mainRoomIndex;
    private MainRoomLevel mainRoomLevel;

    public LevelController() {
        this.restartGame();
    }

    @Override
    public void init() throws Exception {
        timer.init();
        levels.get(active).init();
    }

    @Override
    public void input(MouseInput mouseInput) {
        levels.get(active).input(mouseInput);
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Timed check so that we don't switch levels too quickly
        if (KeyBinding.isPreviousLevelPressed() && timer.peekElapsedTime() > 1) {
            previous();
            timer.init();
        }
        if (KeyBinding.isRestartLevelPressed() && timer.peekElapsedTime() > 1) {
            restart();
            timer.init();
        }
        if (KeyBinding.isNextLevelPressed() && timer.peekElapsedTime() > 1) {
            next();
            timer.init();
        }

        levels.get(active).update(interval, mouseInput);
    }

    @Override
    public void render() {
        levels.get(active).render();
    }

    @Override
    public void terminate() {
        levels.get(active).terminate();
    }

    /**
     * Switch to a new level
     *
     * @param levelIndex The index of the new level
     */
    public void switchToLevel(int levelIndex) {
        // Unload level to release resources
        levels.get(active).terminate();

        // Reset mesh cache
        AssetStore.clearMeshCache();

        // Switch context
        try {
            levels.get(levelIndex).init();
            active = levelIndex;
        } catch (Exception e) {
            System.err.println("Could not load level " + levelIndex);
            e.printStackTrace();
        }
    }

    /**
     * Switches the level to the main room
     */
    public void switchToMainRoom(MainRoomLevel.MAIN_ROOM_SPAWN spawnPoint) {
        mainRoomLevel.setSpawn(spawnPoint);
        switchToLevel(mainRoomIndex);
    }

    /**
     * Goes to the next level
     */
    public void next() {
        if (active < levels.size() - 1) {
            switchToLevel(active + 1);
        }
    }

    /**
     * Goes one level back
     */
    public void previous() {
        if (active > 0) {
            switchToLevel(active - 1);
        }
    }

    /**
     * Restarts the current level
     */
    public void restart() {
        try {
            levels.get(active).terminate();

            // Reset mesh cache
            AssetStore.clearMeshCache();

            levels.get(active).init();
        } catch (Exception e) {
            System.err.println("Could not load level " + active);
            e.printStackTrace();
        }
    }

    /**
     * Set that a gem was found
     *
     * @param gem The found gem
     */
    public void setGemFound(GEM gem) {
        mainRoomLevel.setGemFound(gem);
    }

    /**
     * Initialises all levels
     */
    public void restartGame() {
        this.levels = new ArrayList<>(Arrays.asList(
                new TitleScreenLevel(this),      // 0
                new TutorialDrawingLevel(this),  // 1
                new MobEscape(this),             // 2
                new MainRoomLevel(this),         // 3
                new DarknessLevel(this),         // 4
                new MurderMysteryLevel(this),    // 5
                new PrisonEscapeLevel(this),     // 6
                new TunnelLevel(this),           // 7
                new MobFastRun(this),            // 8
                new TreasureRoomLevel(this)      // 9
        ));
        this.active = 0;
        this.timer = new Timer();

        this.mainRoomIndex = findMainRoom();
        this.mainRoomLevel = (MainRoomLevel) levels.get(mainRoomIndex);
    }

    /**
     * @return The index of the main room
     */
    private int findMainRoom() {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i) instanceof MainRoomLevel) {
                return i;
            }
        }

        throw new IllegalStateException("No main room level found in levels");
    }


    public enum GEM {
        GREEN,
        YELLOW,
        RED,
        BLUE
    }
}
