package game.level;

import engine.MouseInput;
import engine.animation.ModelAnimation;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.DoorEntity;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Player;
import engine.gui.FloatingScrollText;
import engine.gui.PuzzleGUI;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.animatedModelLoader.AnimatedModelLoader;
import engine.loader.animatedModelLoader.AnimationLoader;
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

public class TutorialDrawingLevel extends Level {

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
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;
    /**
     * Flag whether a hint is shown (so not to remove stuff from the gui)
     */
    private boolean hintIsShown;

    public TutorialDrawingLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        // Load map
        map = new MapFileLoader("/levels/tutorial_drawing_level.lvl").load();

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

        Vector2i spawn = map.getTile("spawn").getPosition();
        player.setPosition(spawn.x, 0.5f, spawn.y);

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

        // Load mesh for door
        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));
        doorMesh.setIsStatic(false);

        // Create interactive tiles
        Tile textTile1 = map.getTile("tutorial_text_1");
        IndicatorEntity textIndicator1 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile1.getPosition().x, 1f, textTile1.getPosition().y),
                textTile1
        );

        Tile textTile2 = map.getTile("tutorial_text_2");
        IndicatorEntity textIndicator2 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile2.getPosition().x, 1f, textTile2.getPosition().y),
                textTile2
        );

        Tile pencilTile1 = map.getTile("tutorial_puzzle_1");
        IndicatorEntity pencilIndicator1 = new IndicatorEntity(
                pencilMesh,
                new Vector3f(pencilTile1.getPosition().x, 1f, pencilTile1.getPosition().y),
                pencilTile1
        );

        Tile doorTileLeft = map.getTile("tutorial_door_1");
        DoorEntity doorLeft = new DoorEntity(
                doorMesh,
                new Vector3f(doorTileLeft.getPosition().x - 0.5f, 0f, doorTileLeft.getPosition().y + 0.4f),
                new Vector3f(0f),
                0.5f,
                doorTileLeft
        );
        doorTileLeft.setSolid(true);

        Tile doorTileRight = map.getTile("tutorial_door_2");
        DoorEntity doorRight = new DoorEntity(
                doorMesh,
                new Vector3f(doorTileRight.getPosition().x + 0.5f, 0f, doorTileRight.getPosition().y + 0.4f),
                new Vector3f(0f, 180f, 0f),
                0.5f,
                doorTileRight,
                true
        );
        doorTileRight.setSolid(true);

        // Create dialogue
        text1 = new ScrollingPopup("Welcome, traveller, I see you came from quite far.", () -> {
            gui.setComponent(new ScrollingPopup("Who I am you ask? Oh, don't you worry. I'm the ominous voice, obviously.", () -> {
                gui.setComponent(new ScrollingPopup(
                        "You seek the treasure of the ancient dwarfs? Hahahaha! hundreds, no, thousands, no, countless men tried before you!", () -> {
                    gui.setComponent(new ScrollingPopup("We'll see how your creativity holds up... Good luck traveller. Try to find the entrance.", () -> {
                        textIndicator1.remove(() -> entitiesToRemove.add(textIndicator1));
                        textTile1.removeTag("trigger");
                        paused = false;
                    }));
                }));
            }));
        });

        text2 = new ScrollingPopup("Great job, traveller. Now, you thought you would win this game eeeeh, dungeon by swinging a sword and shooting a bow, right?", () -> {
            gui.setComponent(new ScrollingPopup("Too bad.", () -> {
                gui.setComponent(new ScrollingPopup("In this world, you will need to use your drawing skills to solve the puzzles you'll encounter.", () -> {
                    gui.setComponent(new ScrollingPopup("So, we have a door, with a lock. What would we need to open it?", () -> {
                        gui.setComponent(new FloatingScrollText("Interact with the pencil"));
                        textIndicator2.remove(() -> entitiesToRemove.add(textIndicator2));
                        textTile2.removeTag("trigger");
                        // TODO: Add check if the player did not already interact with the pencil
                        gui.setComponent(new FloatingScrollText("Interact with the pencil"));
                        paused = false;
                        hintIsShown = true;
                    }));
                }));
            }));
        });

        text3 = new ScrollingPopup("In this game you need to draw your solution to puzzles. Use the drawing interface to draw your solution.", () -> {
            gui.setComponent(new ScrollingPopup("You can see a list of possible options to your right and the timer in the right upper corner.", () -> {
                gui.setComponent(new ScrollingPopup("When the timer hits zero, your drawing will be evaluated. Or you can fast forward this by pressing the enter key.", () -> {
                    gui.setComponent(new ScrollingPopup("In order to improve your results, make sure that your drawings are as big as possible. The bigger, the better the game is at recognising them.", () -> {
                        gui.setComponent(new ScrollingPopup("Sometimes there is more than one solution, so get creative! If the game keeps mistaking your drawing for something else, try to draw the defining features of your object as well as possible.", () -> {
                            gui.setComponent(new PuzzleGUI(puzzle1));
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
                        "cannon", "giraffe", "guitar", "key", "axe"
                },
                // Solutions
                new Solution[]{
                        new Solution("key", (s) -> {
                            gui.setComponent(new ScrollingPopup("Good job! A key was indeed the key to the puzzle!", () -> {
                                // Open the doors
                                doorLeft.open();
                                doorRight.open();
                                doorTileLeft.setSolid(false);
                                doorTileRight.setSolid(false);

                                // Remove the indicators
                                pencilIndicator1.remove(() -> entitiesToRemove.add(pencilIndicator1));
                                textIndicator2.remove(() -> entitiesToRemove.add(textIndicator2));

                                // Remove triggers
                                pencilTile1.removeTag("trigger");
                                textTile2.removeTag("trigger");

                                paused = false;
                            }));
                        }),
                        new Solution("cannon", (s) -> {
                            gui.setComponent(new ScrollingPopup("A cannon? You serious? That's a tad too violent... Try again.", () -> {
                                gui.setComponent(new PuzzleGUI(puzzle1));
                            }));
                        }),
                        new Solution("axe", (s) -> {
                            gui.setComponent(new ScrollingPopup("You little viking. But no, this is pg13. Try again.", () -> {
                                gui.setComponent(new PuzzleGUI(puzzle1));
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Hm, not sure if we can open a door with " + s + "...", () -> {
                        gui.setComponent(new PuzzleGUI(puzzle1));
                    }));
                }),
                60
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
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        Vector2i moonLightPos = map.getTile("moon_light").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.2f, 0.4f, 0.8f),
                        new Vector3f(moonLightPos.x, 14f, moonLightPos.y),
                        0.4f,
                        new Vector2f(1f, 100f)
                )
        );

        Vector2i lanternCratePos = map.getTile("crate_lantern").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.8f, 0.2f, 0.2f),
                        new Vector3f(lanternCratePos.x, 3.5f, lanternCratePos.y),
                        0.6f,
                        new PointLight.Attenuation(0f, 0.3f, 0f),
                        new Vector2f(1f, 100f)
                )
        );

        Vector2i lanternCratePos2 = map.getTile("crate_lantern_2").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(1f, 0.2f, 0.2f),
                        new Vector3f(lanternCratePos2.x, 3.2f, lanternCratePos2.y + 0.5f),
                        0.8f,
                        new PointLight.Attenuation(0f, 0.4f, 0f),
                        new Vector2f(0.1f, 100f)
                )
        );

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities = new ArrayList<>(Arrays.asList(
                player,
                textIndicator1,
                textIndicator2,
                pencilIndicator1,
                doorLeft,
                doorRight
        ));

        paused = false;
        hintIsShown = false;
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
            if (hintIsShown) {
                hintIsShown = false;
                gui.removeComponent();
            }
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }
            if (KeyBinding.isInteractPressed()) {
                if (currentPlayerTile.hasTag("tutorial_text_1")) {
                    gui.setComponent(text1);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("tutorial_text_2")) {
                    gui.setComponent(text2);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("tutorial_puzzle_1")) {
                    gui.setComponent(text3);
                    paused = true;
                }
            }
        } else if (currentPlayerTile.hasTag("end")) {
            levelController.next();
        } else if (gui.hasComponent() && !hintIsShown) {
            gui.removeComponent();
        }

        camera.update(interval);
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
}
