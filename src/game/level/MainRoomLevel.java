package game.level;

import engine.MouseInput;
import engine.animation.ModelAnimation;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.*;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Player;
import engine.gui.FloatingScrollText;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import engine.loader.animatedModelLoader.AnimatedModelLoader;
import engine.loader.animatedModelLoader.AnimationLoader;
import engine.util.AssetStore;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;

public class MainRoomLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private SceneLight sceneLight;
    private GUI gui;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> entitiesToRemove;

    /**
     * Texts in the level
     */
    private ScrollingPopup text1;
    /**
     * Flags that indicate whether the levels has been completed. These do NOT reset when the level reloads
     */
    private boolean level1Completed, level2Completed, level3Completed, level4Completed;
    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;
    /**
     * The spawn point of the player
     */
    private MAIN_ROOM_SPAWN spawnPoint;

    public MainRoomLevel(LevelController levelController) {
        super(levelController);

        this.level1Completed = false;
        this.level2Completed = false;
        this.level3Completed = false;
        this.level4Completed = false;

        this.spawnPoint = MAIN_ROOM_SPAWN.FROM_TUTORIAL;
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();

        // Load map
        map = new MapFileLoader("/levels/main_room_level.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        AnimatedModel playerModel = AnimatedModelLoader.loadEntity("/models/entities/player_model.dae");
        playerModel.getMesh().setMaterial(new Material(0.0f));
        playerModel.getMesh().setIsStatic(false);
        ModelAnimation playerAnimation = AnimationLoader.loadAnimation(playerModel);
        playerModel.doAnimation(playerAnimation);
        player = new Player(playerModel, map);
        player.setSpeed(3f);
        player.setScale(new Vector3f(0.25f));

        setPlayerSpawnPoint();

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(2, 11, 3)
        );

        // Load mesh for question mark
        Mesh questionMarkMesh = AssetStore.getMesh("entities", "question_mark");
        questionMarkMesh.setMaterial(new Material(0f));
        questionMarkMesh.setIsStatic(false);

        // Load mesh for pencil
        Mesh pencilMesh = AssetStore.getMesh("entities", "pencil");
        pencilMesh.setMaterial(new Material(0f));
        pencilMesh.setIsStatic(false);

        // Load mesh for silver door and lock
        Mesh silverDoorMesh = AssetStore.getMesh("entities", "silver_door");
        silverDoorMesh.setMaterial(new Material(0f));
        silverDoorMesh.setIsStatic(false);
        Mesh silverDoorMeshMirror = AssetStore.getMesh("entities", "silver_door_mirror");
        silverDoorMesh.setMaterial(new Material(0f));
        silverDoorMesh.setIsStatic(false);
        Mesh lockMesh = AssetStore.getMesh("entities", "lock");
        lockMesh.setMaterial(new Material(0f));
        lockMesh.setIsStatic(false);

        // Setup silver door
        map.getTile("silver_door_left").setSolid(true);
        map.getTile("silver_door_right").setSolid(true);
        map.getTile("silver_door_center").setSolid(true);

        Tile silverDoorLeftTile = map.getTile("silver_door_left");
        Tile silverDoorRightTile = map.getTile("silver_door_right");
        Tile silverDoorCenterTile = map.getTile("silver_door_center");

        DoorEntity silverDoorLeft = new DoorEntity(
                silverDoorMesh,
                new Vector3f(silverDoorLeftTile.getPosition().x, 0.5f, silverDoorLeftTile.getPosition().y - 0.42f),
                new Vector3f(0f),
                new Vector3f(0.6f, 0.7f, 0.55f),
                null
        );
        DoorEntity silverDoorRight = new DoorEntity(
                silverDoorMeshMirror,
                new Vector3f(silverDoorRightTile.getPosition().x, 0.5f, silverDoorRightTile.getPosition().y + 0.42f),
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0.6f, 0.7f, 0.55f),
                null,
                true
        );
        LockEntity lock = new LockEntity(
                lockMesh,
                new Vector3f(silverDoorCenterTile.getPosition().x - 0.3f, 2.5f, silverDoorCenterTile.getPosition().y),
                new Vector3f(0f),
                new Vector3f(1f)
        );

        loadGems();

        // Create interactive tiles
        Tile textTile1 = map.getTile("main_room_text_1");
        IndicatorEntity textIndicator1 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile1.getPosition().x, 1f, textTile1.getPosition().y),
                textTile1
        );

        // Create dialogue
        text1 = new ScrollingPopup("Ah, here we are, the 'main room'. This is where your journey will start.", () -> {
            gui.setComponent(new ScrollingPopup("On the far right, you see a door, behind that door lies the treasure you seek so dearly.", () -> {
                gui.setComponent(new ScrollingPopup("In order to open the door, however, you must find all four gems hidden in this dungeon", () -> {
                    gui.setComponent(new ScrollingPopup("How you ask? Well, simply solve all the puzzles in the four adjacent rooms the ancient dwarfs let you! Good luck.", () -> {
                        textIndicator1.remove(() -> entitiesToRemove.add(textIndicator1));
                        textTile1.removeTag("trigger");
                        paused = false;
                    }));
                }));
            }));
        });

        // Setup lights
        sceneLight = new SceneLight();
        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.2f, 0.4f, 0.8f),       // color
                new Vector3f(0.0f, 1.0f, 0.4f),       // direction
                0.2f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                false
        );
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        map.getTiles("lantern").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.968f, 0.788f, 0.390f),
                            new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                            0.3f,
                            new PointLight.Attenuation(0f, 0f, 0f),
                            new Vector2f(0.1f, 100f)
                    )
            );
        });
        map.getTiles("lantern_crate").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.9f, 0.3f, 0.2f),
                            new Vector3f(t.getPosition().x, 2.5f, t.getPosition().y),
                            0.4f,
                            new PointLight.Attenuation(0f, 0.1f, 0f),
                            new Vector2f(0.1f, 100f)
                    )
            );
        });

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities.addAll(Arrays.asList(
                player,
                textIndicator1,
                silverDoorLeft,
                silverDoorRight,
                lock
        ));

        paused = false;

        // Only execute the following if the game has finished
        if (isGameFinished()) {
            Vector2i spawn = map.getTile("spawn_end").getPosition();
            player.setPosition(spawn.x, 0.5f, spawn.y);

            gui.setComponent(new ScrollingPopup("What's that... All gems are in place! What are all these moving sounds I hear?", () -> {
                lock.halfway(() -> {
                    silverDoorLeft.open();
                    silverDoorRight.open();
                    silverDoorLeftTile.setSolid(false);
                    silverDoorCenterTile.setSolid(false);
                    silverDoorRightTile.setSolid(false);
                });
                lock.remove(() -> entitiesToRemove.add(lock));
                paused = false;
            }));
            paused = true;
        }
    }

    private void loadGems() {
        // Load meshes for gems
        Mesh redGemMesh = AssetStore.getMesh("entities", "gem_red");
        redGemMesh.setMaterial(new Material(0f));
        redGemMesh.setIsStatic(false);

        Mesh yellowGemMesh = AssetStore.getMesh("entities", "gem_yellow");
        yellowGemMesh.setMaterial(new Material(0f));
        yellowGemMesh.setIsStatic(false);

        Mesh greenGemMesh = AssetStore.getMesh("entities", "gem_green");
        greenGemMesh.setMaterial(new Material(0f));
        greenGemMesh.setIsStatic(false);

        Mesh blueGemMesh = AssetStore.getMesh("entities", "gem_blue");
        blueGemMesh.setMaterial(new Material(0f));
        blueGemMesh.setIsStatic(false);

        Vector2i shrine1Pos = map.getTile("shrine_1").getPosition();
        Vector2i shrine2Pos = map.getTile("shrine_2").getPosition();
        Vector2i shrine3Pos = map.getTile("shrine_3").getPosition();
        Vector2i shrine4Pos = map.getTile("shrine_4").getPosition();

        Entity redGem = new IndicatorEntity(
                redGemMesh,
                new Vector3f(shrine1Pos.x, 1.5f, shrine1Pos.y),
                new Vector3f(45f, 90f, 45f),
                null
        );
        Entity yellowGem = new IndicatorEntity(
                yellowGemMesh,
                new Vector3f(shrine2Pos.x, 1.5f, shrine2Pos.y),
                new Vector3f(45f, 90f, 45f),
                null
        );
        Entity greenGem = new IndicatorEntity(
                greenGemMesh,
                new Vector3f(shrine3Pos.x, 1.5f, shrine3Pos.y),
                new Vector3f(45f, 90f, 45f),
                null
        );
        Entity blueGem = new IndicatorEntity(
                blueGemMesh,
                new Vector3f(shrine4Pos.x, 1.5f, shrine4Pos.y),
                new Vector3f(45f, 90f, 45f),
                null
        );

        if (level1Completed) {
            entities.add(redGem);
        }
        if (level2Completed) {
            entities.add(yellowGem);
        }
        if (level3Completed) {
            entities.add(greenGem);
        }
        if (level4Completed) {
            entities.add(blueGem);
        }
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }

        // Hack to complete the level
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_F5)) {
            finishLevel();
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        gui.update(interval);

        if (paused) {
            return;
        }

        entities.forEach(e -> e.update(interval));
        entitiesToRemove.forEach(e -> entities.remove(e));

        Tile currentPlayerTile = map.getTile(
                Math.round(player.getPosition().x),
                Math.round(player.getPosition().z)
        );

        if (currentPlayerTile.hasTag("trigger")) {
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }
            if (KeyBinding.isInteractPressed()) {
                if (currentPlayerTile.hasTag("main_room_text_1")) {
                    gui.setComponent(text1);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("entrance_level_1")) {
                    levelController.switchToLevel(3);
                }
                if (currentPlayerTile.hasTag("entrance_level_2")) {
                    // TODO: Switch to level
                }
                if (currentPlayerTile.hasTag("entrance_level_3")) {
                    levelController.switchToLevel(4);
                }
                if (currentPlayerTile.hasTag("entrance_level_4")) {
                    // TODO: Switch to level
                }

            }
        } else if (gui.hasComponent()) {
            gui.removeComponent();
        }

        camera.update();
        player.update(interval);
        sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));
    }

    @Override
    public void render() {
        renderer.render(
                camera,
                entities,
                sceneLight,
                map
        );
        gui.render();
    }

    @Override
    public void terminate() {
        sceneLight.cleanup();
    }

    public void setPlayerSpawnPoint() {
        Vector2i spawn;

        switch (spawnPoint) {
            case FROM_TUTORIAL:
                spawn = map.getTile("tutorial_spawn").getPosition();
                break;
            case FROM_LEVEL_1:
                spawn = map.getTile("spawn_level_1").getPosition();
                break;
            case FROM_LEVEL_2:
                spawn = map.getTile("spawn_level_2").getPosition();
                break;
            case FROM_LEVEL_3:
                spawn = map.getTile("spawn_level_3").getPosition();
                break;
            case FROM_LEVEL_4:
                spawn = map.getTile("spawn_level_4").getPosition();
                break;

            default:
                spawn = map.getTile("tutorial_spawn").getPosition();
        }

        player.setPosition(spawn.x, 0.5f, spawn.y);
    }

    /**
     * Sets the spawn point where the player should start when this level is loaded
     *
     * @param spawnPoint Spawn point
     */
    public void setSpawn(MAIN_ROOM_SPAWN spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public void setGemFound(LevelController.GEM gem) {
        switch (gem) {
            case RED:
                this.level1Completed = true;
                break;
            case BLUE:
                this.level2Completed = true;
                break;
            case GREEN:
                this.level3Completed = true;
                break;
            case YELLOW:
                this.level4Completed = true;
                break;
        }
    }

    /**
     * @return True if the game is completed, false otherwise
     */
    private boolean isGameFinished() {
        return level1Completed && level2Completed && level3Completed && level4Completed;
    }

    /**
     * Hack to instantly finish the game
     */
    private void finishLevel() {
        level1Completed = true;
        level2Completed = true;
        level3Completed = true;
        level4Completed = true;

        levelController.restart();
    }

    public enum MAIN_ROOM_SPAWN {
        FROM_TUTORIAL,
        FROM_LEVEL_1,
        FROM_LEVEL_2,
        FROM_LEVEL_3,
        FROM_LEVEL_4
    }
}
