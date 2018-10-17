package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.Player;
import engine.gui.FloatingScrollText;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import engine.util.AssetStore;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import game.puzzle.Puzzle;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

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
    private ScrollingPopup text1, text2, text3;
    /**
     * Puzzles in the level
     */
    private Puzzle puzzle1;
    /**
     * Flags that indicate whether the levels has been completed. These do NOT reset when the level reloads
     */
    private boolean level1, level2, level3, level4;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    public MainRoomLevel(LevelController levelController) {
        super(levelController);

        this.level1 = true;
        this.level2 = true;
        this.level3 = true;
        this.level4 = true;
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
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        playerMesh.setIsStatic(false);
        player = new Player(playerMesh, map);
        player.setSpeed(3f);
        player.setScale(new Vector3f(1, 2, 1));

        Vector2i spawn = map.getTile("spawn").getPosition();
        player.setPosition(spawn.x, 0.5f, spawn.y);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(3, 11, 3)
        );

        // Load mesh for question mark
        Mesh questionMarkMesh = AssetStore.getMesh("entities", "question_mark");
        questionMarkMesh.setMaterial(new Material(0f));
        questionMarkMesh.setIsStatic(false);

        // Load mesh for pencil
        Mesh pencilMesh = AssetStore.getMesh("entities", "pencil");
        pencilMesh.setMaterial(new Material(0f));
        pencilMesh.setIsStatic(false);

        // Load mesh for door
        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));
        doorMesh.setIsStatic(false);

        loadGems();

        // Create interactive tiles

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
                            new Vector3f(t.getPosition().x, 4.5f, t.getPosition().y),
                            0.2f,
                            new PointLight.Attenuation(0f, 1f, 0f),
                            new Vector2f(1f, 100f)
                    )
            );
        });

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities.addAll(Arrays.asList(
                player
        ));

        paused = false;
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

        if (level1) {
            entities.add(redGem);
        }
        if (level2) {
            entities.add(yellowGem);
        }
        if (level3) {
            entities.add(greenGem);
        }
        if (level4) {
            entities.add(blueGem);
        }
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
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

            }
        } else if (currentPlayerTile.hasTag("end")) {
            levelController.next();
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
    }
}
