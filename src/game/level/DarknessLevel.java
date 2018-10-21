package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.DoorEntity;
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
import game.mobs.Snake;
import game.puzzle.Puzzle;
import game.puzzle.Solution;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
     *
     */
    private boolean lightningEnabled = false;
    private int deltaUpdates = 0;

    /**
     *
     */
    private ScrollingPopup text1, text2;

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
    private PointLight flashLight;
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

        // Load mesh for door
        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));
        doorMesh.setIsStatic(false);

        // Create interactive tiles
        Tile pencilTile1 = map.getTile("light_puzzle_trigger");
        IndicatorEntity pencilIndicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(pencilTile1.getPosition().x, 1f, pencilTile1.getPosition().y),
                pencilTile1
        );

        Tile textTile1 = map.getTile("welcome_text");
        IndicatorEntity textIndicator1 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile1.getPosition().x, 1f, textTile1.getPosition().y),
                textTile1
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

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0f));

        Vector2i initialLightPosition = map.getTile("init_light").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.8f, 0.3f, 0.2f),
                        new Vector3f(initialLightPosition.x - 0.5f, 2f, initialLightPosition.y + 0.5f),
                        1f,
                        new PointLight.Attenuation(0f, 0.3f, 0f),
                        new Vector2f(0.01f, 100f)
                )
        );

        Vector2i shrineLightPosition = map.getTile("shrine_light").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.8f, 0.3f, 0.2f),
                        new Vector3f(shrineLightPosition.x + 0.5f, 0.5f, shrineLightPosition.y + 0.5f),
                        1f,
                        new PointLight.Attenuation(0f, 0.3f, 0f),
                        new Vector2f(0.01f, 100f)
                )
        );

        // Flashlight
        flashLight = new PointLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(player.getPosition().x + 1f, 2f, player.getPosition().z),
                0.5f,
                new PointLight.Attenuation(0f, 0.3f, 0f),
                new Vector2f(0.1f, 100f)
        );
        flashLight.setToDynamicOnly();

        // Setup sound
        soundManager = new SoundManager();
        soundManager.init();

        SoundBuffer buffDarkness = new SoundBuffer("/sound/darkness.ogg");
        soundManager.addSoundBuffer(buffDarkness);
        SoundBuffer buffThunder = new SoundBuffer("/sound/thunder.ogg");
        soundManager.addSoundBuffer(buffThunder);
        SoundSource sourceBack = new SoundSource(false, true);
        sourceBack.setBuffer(buffDarkness.getBufferId());
        SoundSource sourceThunder = new SoundSource(false, true);
        sourceThunder.setBuffer(buffThunder.getBufferId());
        soundManager.addSoundSource("helloDarkness", sourceBack);
        soundManager.addSoundSource("thunder", sourceThunder);
        soundManager.setListener(SoundListener.getSoundListener());

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Create dialogue
        text1 = new ScrollingPopup("Wow. So dark. Hope it's just a phase.", () -> {
            gui.setComponent(new ScrollingPopup("This room makes me think of a joke I heard a long time ago. Want to hear it?", () -> {
                gui.setComponent(new ScrollingPopup("No? Too bad. I'm going to tell you anyway", () -> {
                    gui.setComponent(new ScrollingPopup("\"Dark humor is like food. Not everyone gets it.\" Hahahaha, classic!", () -> {
                       gui.setComponent(new ScrollingPopup("So where were we? Ah right. We got gems to collect", () -> {
                           textIndicator1.remove(() -> entitiesToRemove.add(textIndicator1));
                           textTile1.removeTag("trigger");
                           paused = false;
                           gui.removeComponent();
                       }));
                    }));
                }));
            }));
        });



        // Create puzzle(s)
        puzzle1 = new Puzzle(
                "This description does nothing",
                // Options
                new String[]{
                        // flashlight, sun, lightning, lighthouse, harp
                        "flashlight", "lightning", "apple", "panda" // Temporary list
                },
                // Solutions
                new Solution[]{
                        // Flashlight
                        new Solution("flashlight", (s) -> {
                            gui.setComponent(new ScrollingPopup("A bit boring, but sure. Let there be a flashlight!", () -> {
                                flashLight.setPosition(new Vector3f(player.getPosition().x + 1f, 2f, player.getPosition().z));
                                sceneLight.pointLights.add(flashLight);
                                renderer.resetShadowMap();
                                pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                pencilTile1.removeTag("trigger");
                                paused = false;
                                gui.removeComponent();
                            }));
                        }),

                        // Lightning
                        new Solution("lightning", (s) -> {
                            sourceThunder.play();
                            gui.setComponent(new ScrollingPopup("Mwhuahahaha! It's alive... IT'S ALIVE!", () -> {
                                gui.setComponent(new ScrollingPopup("Ahem... Sorry you had to see that... But you simply cannot have lightning without an evil laugh", () -> {
                                    lightningEnabled = true;
                                    pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                    pencilTile1.removeTag("trigger");
                                    paused = false;
                                }));
                            }));
                        }),
                        // Sun
                        new Solution("apple", (s) -> {
                            gui.setComponent(new ScrollingPopup("A sun seems like a highly impracticable light source to use inside of a dungeon. But who am I to judge? You do you!", () -> {
                                sceneLight.directionalLight = new DirectionalLight(
                                        new Vector3f(0.0f, 7.0f, 0.0f),       // position
                                        new Vector3f(0.9f, 0.85f, 0.4f),       // color
                                        new Vector3f(-0.4f, 1.0f, 0),       // direction
                                        10f,                                // intensity
                                        new Vector2f(1.0f, 10.0f),              // near-far plane
                                        false
                                );
                                pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                pencilTile1.removeTag("trigger");
                                paused = false;
                            }));
                        }),

                        new Solution("panda", (s) -> {
                            gui.setComponent(new ScrollingPopup("You prefer music over some proper light source? Oh, you think darkness is your ally? But you merely adopted the dark. I was born in it! Molded by it!", () -> {
                                gui.setComponent(new ScrollingPopup("Oh, right, music. I know something that fits this situation.", () -> {
                                    sourceBack.play();
                                    pencilIndicator.remove(() -> entitiesToRemove.add(pencilIndicator));
                                    pencilTile1.removeTag("trigger");
                                    paused = false;
                                }));
                            }));
                        })},

                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Hm, the only way we could use a " + s.replace('_', ' ') + ", is if we light it on fire... I'm not sure if that is such a bright idea.", () -> {
                        gui.setComponent(new PuzzleGUI(puzzle1));
                    }));
                }),
                20
        );

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities = new ArrayList<>(Arrays.asList(
                player,
                textIndicator1,
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

        if (sceneLight.pointLights.contains(flashLight)) {
            float signX = Math.signum(player.getPosition().x - previousPosition.x);
            float signZ = Math.signum(player.getPosition().z - previousPosition.z);

            if (signX != 0 || signZ != 0) {
                flashLight.setPosition(new Vector3f(player.getPosition().x + signX,
                        2f, player.getPosition().z + signZ));
            }
        }

        if (lightningEnabled) {
            Random rd = new Random();

            if (deltaUpdates >= 30) {
                float amount = sceneLight.ambientLight.getLight().x;
                sceneLight.ambientLight = new AmbientLight(new Vector3f(amount));
                deltaUpdates--;
            } else if (rd.nextInt(100) <= 1) {
                float amount = rd.nextFloat() / 4 + 0.75f;
                sceneLight.ambientLight = new AmbientLight(new Vector3f(amount));
                deltaUpdates = 30;
            } else {
                sceneLight.ambientLight = new AmbientLight(new Vector3f(0.1f));
                deltaUpdates = 0;
            }
        }

        if (currentPlayerTile.hasTag("trigger")) {
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }

            if (KeyBinding.isInteractPressed()) {
                if (currentPlayerTile.hasTag("welcome_text")) {
                    gui.setComponent(text1);
                } else if (currentPlayerTile.hasTag("hint_trigger")) {
                    gui.setComponent(text2);
                } else if (currentPlayerTile.hasTag("light_puzzle_trigger")) {
                    gui.setComponent(new PuzzleGUI(puzzle1));
                }

                paused = true;
            }
        } else if (gui.hasComponent()) {
            gui.removeComponent();
        }

        camera.update(interval);
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
