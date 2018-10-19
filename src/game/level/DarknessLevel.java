package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.Player;
import engine.gui.FloatingScrollText;
import engine.gui.PuzzleGUI;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.*;
import engine.loader.PLYLoader;
import engine.sound.SoundBuffer;
import engine.sound.SoundListener;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import engine.util.AssetStore;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import game.puzzle.Puzzle;
import game.puzzle.Solution;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

public class DarknessLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private SceneLight sceneLight;
    private GUI gui;
    private SoundManager soundManager;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> entitiesToRemove;

    /**
     * Dialog in the game
     */
    private ScrollingPopup text1;

    /**
     * Puzzles
     */
    private Puzzle puzzle1;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    /**
     *  Light sources used for the game
     */
    private SpotLight flashLight;
    private Vector3f previousPosition;

    public DarknessLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {

        // Load map
        map = new MapFileLoader("/levels/darkness_level.lvl").load();

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

        // Create interactive tiles
        Tile pencilTile1 = map.getTile("puzzle_trigger");
        IndicatorEntity pencilIndicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(pencilTile1.getPosition().x, 1f, pencilTile1.getPosition().y),
                pencilTile1
        );

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

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.1f));

        Vector2i initialLightPosition = map.getTile("light").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.8f, 0.3f, 0.2f),
                        new Vector3f(initialLightPosition.x - 0.5f, 2f, initialLightPosition.y + 0.5f),
                        1f,
                        new PointLight.Attenuation(0f, 0.3f, 0f),
                        new Vector2f(0.01f, 100f)
                )
        );

        // Flashlight
        flashLight = new SpotLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(player.getPosition().x + 1f, 2f, player.getPosition().z),
                2f,
                new Vector3f(1f, -1f, 0f),
                (float) Math.cos(Math.toRadians(20f)),
                (float) Math.cos(Math.toRadians(40f)),
                new Vector2f(0.05f, 300f)
        );

        // Setup sound
        soundManager = new SoundManager();
        soundManager.init();

        SoundBuffer buffDarkness = new SoundBuffer("/sound/darkness.ogg");
        soundManager.addSoundBuffer(buffDarkness);
        SoundSource sourceBack = new SoundSource(false, true);
        sourceBack.setBuffer(buffDarkness.getBufferId());
        soundManager.addSoundSource("helloDarkness", sourceBack);
        soundManager.setListener(SoundListener.getSoundListener());

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Create puzzle(s)
        puzzle1 = new Puzzle(
                "This description does nothing",
                // Options
                new String[]{
                        // flashlight, sun, lightning, lighthouse, harp
                        "flashlight", "lightning", "apple", "palm_tree", "panda" // Temporary list
                },
                // Solutions
                new Solution[]{
                        new Solution("flashlight", () -> {
                            gui.setComponent(new ScrollingPopup("Let there be light!", () -> {
                                sceneLight.spotLights.add(flashLight);
                                renderer.resetShadowMap();
                                pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                pencilTile1.removeTag("puzzle_trigger");
                                paused = false;
                            }));
                        }),
                        new Solution("lightning", () -> {
                            gui.setComponent(new ScrollingPopup("Let there be light", () -> {
                                pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                pencilTile1.removeTag("puzzle_trigger");
                                paused = false;
                            }));
                        }),
                        new Solution("apple", () -> {
                            gui.setComponent(new ScrollingPopup("Let the be light", () -> {
                                pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                pencilTile1.removeTag("puzzle_trigger");
                                paused = false;
                            }));
                        }),
                        new Solution("palm_tree", () -> {
                            gui.setComponent(new ScrollingPopup("Let there be light", () -> {
                                pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                pencilTile1.removeTag("puzzle_trigger");
                                paused = false;
                            }));
                        }),
                        new Solution("panda", () -> {
                            gui.setComponent(new ScrollingPopup("Let there be music", () -> {
                                sourceBack.play();
                                pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                pencilTile1.removeTag("puzzle_trigger");
                                paused = false;
                            }));
                        })
                },
                // Default solution
                new Solution("", () -> {
                    gui.setComponent(new ScrollingPopup("Hm, the only way we could use that, is if we light it on fire...", () -> {
                        gui.setComponent(new PuzzleGUI(puzzle1));
                    }));
                }),
                60
        );

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities = new ArrayList<>(Arrays.asList(
                player,
                pencilIndicator
        ));

        paused = false;

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

        previousPosition = new Vector3f(player.getPosition());

        entities.forEach(e -> e.update(interval));
        entitiesToRemove.forEach(e -> entities.remove(e));

        Tile currentPlayerTile = map.getTile(
                Math.round(player.getPosition().x),
                Math.round(player.getPosition().z)
        );

        if (sceneLight.spotLights.contains(flashLight)) {
            float signX = Math.signum(player.getPosition().x - previousPosition.x);
            float signZ = Math.signum(player.getPosition().z - previousPosition.z);

            if (signX != 0 || signZ != 0) {
                flashLight.setPosition(new Vector3f(player.getPosition().x + signX,
                        2f, player.getPosition().z + signZ));
                flashLight.setConeDirection(new Vector3f(signX, -1f, signZ));
            }
        }

        if (currentPlayerTile.hasTag("puzzle_trigger")) {
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }

            if (KeyBinding.isInteractPressed()) {
                gui.setComponent(new PuzzleGUI(puzzle1));
                paused = true;
            }
        } else if (gui.hasComponent()) {
            gui.removeComponent();
        }

        camera.update();
        player.update(interval);
        sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));

        soundManager.updateListenerPosition(camera);
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
        soundManager.terminate();
    }
}
