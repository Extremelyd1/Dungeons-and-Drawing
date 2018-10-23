package game.level;

import engine.MouseInput;
import engine.animation.ModelAnimation;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Player;
import engine.gui.FloatingScrollText;
import engine.gui.PuzzleGUI;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
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
import game.mobs.Snake;
import game.puzzle.Puzzle;
import game.puzzle.Solution;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobEscape extends Level {
    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    private SceneLight sceneLight;
    private GUI gui;
    private Puzzle arcCollapsePuzzle;
    private Snake snake = null;

    private List<Entity> entitiesToRemove;

    private ScrollingPopup text1, text2, text3;

    private boolean paused;

    private Mesh snakeMesh;

    private boolean puzzleSolved;

    public MobEscape(LevelController levelController) {
        super(levelController);
    }

    /**
     * Level Infos
     * <p>
     * Tile 27/17 arc to delete	                                                    Done
     * <p>
     * Tile 26/17, 27/17, 28/17 to replace with boulders to  block the mob path	    Done
     * <p>
     * Tile 27/22 end of the level                                                  Done
     * <p>
     * Tile 14/1 mob spawn                                                          Done
     * <p>
     * Tile 13/5, 13/4, 13/6 mob spawn trigger                                      Done
     * <p>
     * Tile 2/5 player spawn	                                                    Done
     */

    @Override
    public void init() throws Exception {
        // Load Map
        map = new MapFileLoader("/levels/mob_escape_level.lvl").load();

        // Make sure the shadows update
        map.getTile("stone_1").getMesh().setIsStatic(false);
        map.getTile("arc").getMesh().setIsStatic(false);
        map.getTile("stone_2").getMesh().setIsStatic(false);

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Set stone meshes
        Mesh stone1Mesh = AssetStore.getTileMesh("stone_1");
        Mesh stone2Mesh = AssetStore.getTileMesh("stone_2");

        // Load mesh for question mark
        Mesh questionMarkMesh = AssetStore.getMesh("entities", "question_mark");
        questionMarkMesh.setMaterial(new Material(0f));
        questionMarkMesh.setIsStatic(false);

        // Load mesh for pencil
        Mesh pencilMesh = AssetStore.getMesh("entities", "pencil");
        pencilMesh.setMaterial(new Material(0f));
        pencilMesh.setIsStatic(false);

        // Get indicator tiles
        Tile puzzle1Tile = map.getTile("puzzle_1");
        IndicatorEntity puzzle1Inicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzle1Tile.getPosition().x, 0.5f, puzzle1Tile.getPosition().y),
                puzzle1Tile
        );

        Tile text1Tile = map.getTile("text_1");
        IndicatorEntity text1Indicator = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(text1Tile.getPosition().x, 0.5f, text1Tile.getPosition().y),
                text1Tile
        );

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup puzzle
        puzzleSolved = false;
        arcCollapsePuzzle = new Puzzle(
                "To collapse the arc you draw:",
                // Possible guesses
                new String[]{"cannon", "frying pan", "rifle"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("cannon", (s) -> {
                    gui.setComponent(new ScrollingPopup("You hear a loud explosion!", () ->
                            paused = false
                    ));
                    // Remove the arc and replace the entire row with boulders to block the mob path
                    map.getTile("stone_1").setMesh(stone1Mesh);
                    map.getTile("stone_1").setSolid(true);
                    map.getTile("arc").setMesh(stone2Mesh);
                    map.getTile("arc").setSolid(true);
                    map.getTile("stone_2").setMesh(stone1Mesh);
                    map.getTile("stone_2").setSolid(true);
                    // Remove tag
                    puzzle1Tile.removeTag("trigger");
                    // Remove indicator
                    puzzle1Inicator.remove(() -> {
                        entitiesToRemove.add(puzzle1Inicator);
                    });
                    puzzleSolved = true;
                    // Remove the snake
                    entitiesToRemove.add(snake);
                    // Set snake to null so that collision check doesn't occur
                    snake = null;
                })},
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("A " + s + " will probably not work. Quick, try again!", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                10
        );

        // Setup Player spawn
        AnimatedModel playerModel = AnimatedModelLoader.loadEntity("/models/entities/player_model.dae");
        playerModel.getMesh().setMaterial(new Material(0.0f));
        playerModel.getMesh().setIsStatic(false);
        ModelAnimation playerAnimation = AnimationLoader.loadAnimation(playerModel);
        playerModel.doAnimation(playerAnimation);
        player = new Player(playerModel, map);
        player.setSpeed(3);
        player.setScale(new Vector3f(0.25f));

        // Get player spawn
        Vector2i spawn = map.getTile("spawn").getPosition();
        player.setPosition(spawn.x, 0.5f, spawn.y);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(2, 11, 3)
        );

        // Load snake
        snakeMesh = PLYLoader.loadMesh("/models/entities/snake.ply");
        snakeMesh.setMaterial(new Material(0.0f));
        snakeMesh.setIsStatic(false);

        snake = new Snake(snakeMesh, map);
        snake.setScale(0.08f);
        snake.setPosition(14, 0.49f, 1);
        snake.setSpeed(2.5f);
        snake.setTarget(player);
        snake.followOnSightOnly(false);

        text1 = new ScrollingPopup("Welcome inside the dungeon, traveller. Please, make yourself at home.", () -> {
            gui.setComponent(new ScrollingPopup("Please, be aware of our... pets. Some won't hurt you, but others try to chase you.", () -> {
                gui.setComponent(new ScrollingPopup("The latter you can't fight, so you must find other ways of keeping them away.", () -> {
                    gui.removeComponent();
                    paused = false;
                }));
            }));
        });

        text2 = new ScrollingPopup("Oh no! The house snake Snaky the Snek has awoken. He doesn't like visitors that much. Quick, try to find a way of escaping.", () -> {
            paused = false;
        });

        text3 = new ScrollingPopup("See that arc? We might be able to blow it up and stop Snaky that way...", () -> {
            gui.setComponent(new PuzzleGUI(arcCollapsePuzzle));
        });

        // Setup Map Lights
        sceneLight = new SceneLight();
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f, 0.2f, 0.2f));

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

        entitiesToRemove = new ArrayList<>();
        entities = new ArrayList<>(Arrays.asList(
                player,
                text1Indicator,
                puzzle1Inicator
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

        camera.update(interval);
        player.update(interval);
        //sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));

        entities.removeAll(entitiesToRemove);

        for (Entity entity : entities) {
            entity.update(interval);
        }

        // Remove entities
        Tile currentPlayerTile = map.getTile(
                Math.round(player.getPosition().x),
                Math.round(player.getPosition().z)
        );

        if (currentPlayerTile.hasTag("mob_trigger")) {

            gui.setComponent(text2);
            paused = true;

            map.getTiles("mob_trigger").forEach(t -> t.removeTag("mob_trigger"));
            snake = new Snake(snakeMesh, map);
            snake.setScale(0.08f);
            Vector2i spawn = map.getTile("mob_spawn").getPosition();
            snake.setPosition(spawn.x, 0.49f, spawn.y);
            snake.setSpeed(2.5f);
            snake.setTarget(player);
            snake.followOnSightOnly(false);
            entities.add(snake);

            // Skip the update loop this time so we actually pause the loop
            return;
        }

        // Snake
        if (snake != null) {
            if (snake.isCollidingWithTarget(0.95f)) {
                gui.setComponent(new ScrollingPopup("Snaky the Snek got you :c Try again.", () -> {
                    levelController.restart();
                }));
                paused = true;

                // Skip the rest of the update to show the pop up
                return;
            }
        }

        if (currentPlayerTile.hasTag("end")) {
            if (!puzzleSolved) {
                gui.setComponent(new ScrollingPopup("We must first get rid of the snake.", () -> {
                    Vector2i arcPos = map.getTile("arc").getPosition();
                    player.setPosition(arcPos.x, 0.5f, arcPos.y);
                    paused = false;
                }));
                paused = true;

                return;
            } else {
                levelController.next();
            }
        }

        // Check for tiles that have a trigger
        if (currentPlayerTile.hasTag("trigger")) {
            if (!gui.hasComponent()) {
                // Show interact hint
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
                // Check the exact trigger
            }
            if (KeyBinding.isInteractPressed()) {
                if (currentPlayerTile.hasTag("text_1")) {
                    gui.setComponent(text1);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("puzzle_1")) {
                    gui.setComponent(text3);
                    paused = true;
                }
            }
            // If not on any trigger anymore, remove floating text
        } else if (gui.hasComponent()) {
            gui.removeComponent();
        }

        // Check end of level
        if (Math.round(player.getPosition().x) == 27 && Math.round(player.getPosition().z) == 22) {
            levelController.next();
        }

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
        snake = null;
        camera = null;
        sceneLight.cleanup();
    }
}
