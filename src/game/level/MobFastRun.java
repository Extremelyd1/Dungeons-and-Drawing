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
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MobFastRun extends Level {
    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    private List<Entity> entitiesToRemove;
    private SceneLight sceneLight;
    private GUI gui;

    private Puzzle shovelRockPuzzle, doorPuzzle, leftCratePuzzle, rightCratePuzzle;
    private Mesh mobMesh;
    private ScrollingPopup text1, text2, text3;
    private IndicatorEntity gem;
    private DoorEntity mainDoor;
    private Snake mob = null;
    private int toolUsed = 0;

    private boolean paused = false;

    public MobFastRun(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        // Load Map tiles
        map = new MapFileLoader("/levels/mob_run_fast.lvl").load();

        entities = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup Map Lights
        sceneLight = new SceneLight();
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f, 0.2f, 0.2f));

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.8f, 0.8f, 0.8f),     // color
                new Vector3f(0.0f, 1.0f, 0.4f),     // direction
                0.15f,                                // intensity
                new Vector2f(1.0f, 10.0f),             // near-far plane
                false);

        map.getTiles("hang_light").forEach((t) -> {
            sceneLight.pointLights.add(new PointLight(
                    new Vector3f(0.968f, 0.788f, 0.390f),
                    new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                    0.2f,
                    new PointLight.Attenuation(0f, 0f, 0f),
                    new Vector2f(0.1f, 100f)
            ));
        });
        map.getTiles("crate_light").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.701f, 0.439f, 0f),
                            new Vector3f(t.getPosition().x, 2.5f, t.getPosition().y),
                            0.6f,
                            new PointLight.Attenuation(0f, 0f, 0f),
                            new Vector2f(0.1f, 100f)
                    )
            );
        });

        // Load normal floor mesh
        Mesh floorMesh = AssetStore.getTileMesh("stone_floor");
        floorMesh.setMaterial(new Material(0f));

        // Load mesh for question mark
        Mesh questionMarkMesh = AssetStore.getMesh("entities", "question_mark");
        questionMarkMesh.setMaterial(new Material(0f));
        questionMarkMesh.setIsStatic(false);

        // Load mesh for pencil
        Mesh pencilMesh = AssetStore.getMesh("entities", "pencil");
        pencilMesh.setMaterial(new Material(0f));
        pencilMesh.setIsStatic(false);

        // Load gem
        Mesh gemMesh = AssetStore.getMesh("entities", "gem_blue");
        gemMesh.setMaterial(new Material(0f));
        gemMesh.setIsStatic(false);

        // Load door mesh
        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));
        doorMesh.setIsStatic(false);

        // Load mob mesh
        mobMesh = PLYLoader.loadMesh("/models/entities/snake.ply");
        mobMesh.setMaterial(new Material(0f));
        mobMesh.setIsStatic(false);

        Tile text1Tile = map.getTile("text1");

        IndicatorEntity text1Indicator = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(text1Tile.getPosition().x, 1f, text1Tile.getPosition().y),
                new Vector3f(text1Tile.getRotation()),
                text1Tile
        );
        entities.add(text1Indicator);

        // Setup text
        text1 = new ScrollingPopup("What is this room supposed to be? It looks like some kind of storage room.", () -> {
            gui.setComponent(new ScrollingPopup("Perhaps I can find some treasure here, or maybe a gem!", () -> {
                gui.removeComponent();
                paused = false;
            }));
        });

        Tile text2Tile = map.getTile("text2");

        IndicatorEntity text2Indicator = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(text2Tile.getPosition().x, 1f, text2Tile.getPosition().y),
                new Vector3f(text2Tile.getRotation()),
                text2Tile
        );
        entities.add(text2Indicator);

        text2 = new ScrollingPopup("What in tarnation are you doing here, there is a bloody snake after you!", () -> {
            gui.setComponent(new ScrollingPopup("Well, don't just stand there, find a way out of here!", () -> {
                gui.removeComponent();
                paused = false;
            }));
        });

        Tile text3Tile = map.getTile("text3");

        IndicatorEntity text3Indicator = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(text3Tile.getPosition().x, 1f, text3Tile.getPosition().y),
                new Vector3f(text3Tile.getRotation()),
                text3Tile
        );
        entities.add(text3Indicator);

        text3 = new ScrollingPopup("While rummaging around the crates you hear something. Better get moving.", () -> {
            gui.removeComponent();
            paused = false;
        });

        Tile puzzle1Tile = map.getTile("puzzle1");

        IndicatorEntity puzzle1Indicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzle1Tile.getPosition().x, 1f, puzzle1Tile.getPosition().y),
                puzzle1Tile
        );
        entities.add(puzzle1Indicator);

        Tile puzzle2Tile = map.getTile("puzzle2");

        IndicatorEntity puzzle2Indicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzle2Tile.getPosition().x, 1f, puzzle2Tile.getPosition().y),
                puzzle2Tile
        );
        entities.add(puzzle2Indicator);

        Tile puzzle3Tile = map.getTile("puzzle3");

        IndicatorEntity puzzle3Indicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzle3Tile.getPosition().x, 1f, puzzle3Tile.getPosition().y),
                puzzle3Tile
        );
        entities.add(puzzle3Indicator);

        Tile puzzle4Tile = map.getTile("puzzle4");

        IndicatorEntity puzzle4Indicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzle4Tile.getPosition().x, 1f, puzzle4Tile.getPosition().y),
                puzzle4Tile
        );
        entities.add(puzzle4Indicator);

        Tile doorTile = map.getTile("door");
        mainDoor = new DoorEntity(
                doorMesh,
                new Vector3f(doorTile.getPosition().x - 0.5f, 0f, doorTile.getPosition().y),
                new Vector3f(0),
                0.5f,
                doorTile
        );
        doorTile.setSolid(true);
        entities.add(mainDoor);

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup player
        AnimatedModel playerModel = AnimatedModelLoader.loadEntity("/models/entities/player_model.dae");
        playerModel.getMesh().setMaterial(new Material(0.0f));
        playerModel.getMesh().setIsStatic(false);
        ModelAnimation playerAnimation = AnimationLoader.loadAnimation(playerModel);
        playerModel.doAnimation(playerAnimation);
        player = new Player(playerModel, map);
        player.setSpeed(3f);
        player.setScale(new Vector3f(0.25f));
        entities.add(player);

        Tile spawn = map.getTile("spawn");

        player.setPosition(spawn.getPosition().x, 0.5f, spawn.getPosition().y);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(2, 11, 3)
        );

        Tile stoneTile = map.getTile("stone");
        stoneTile.getMesh().setIsStatic(false);

        // Setup Puzzles
        //// Shovel Rock Puzzle
        shovelRockPuzzle = new Puzzle(
                "To move the rock out of the way you use a:",
                // Possible guesses
                new String[]{"shovel"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("shovel", (s) -> {
                    gui.setComponent(new ScrollingPopup("Your shovel moves the rock out of the way however it snaps making a loud noise", () ->
                            gui.setComponent(new ScrollingPopup("While shoveling you heard some noises coming from somewhere around the crates...", () ->
                                    paused = false
                            ))
                    ));

                    stoneTile.setMesh(floorMesh);
                    stoneTile.setSolid(false);

                    puzzle1Indicator.remove(() -> entitiesToRemove.add(puzzle1Indicator));
                    puzzle1Tile.removeTag("trigger");

                })}, new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("I'm not sure what to do with " + s, () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }), 10
        );

        Tile crate1 = map.getTile("crate1");
        crate1.getMesh().setIsStatic(false);

        // Left Crate Puzzle
        leftCratePuzzle = new Puzzle(
                "To open the crate you use a:",
                // Possible guesses
                new String[]{"axe", "saw"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("axe", (s) -> {
                    if (toolUsed == 1) {
                        gui.setComponent(new ScrollingPopup("Your axe is broken. You miserably fail to open the crate with it", () ->
                                paused = false
                        ));
                        return;         // Axe already used
                    } else if (toolUsed == 0) {
                        gui.setComponent(new ScrollingPopup("As you slice open the crate the axe handle suddenly flies off.", () ->
                                gui.setComponent(new ScrollingPopup("Guess I won't be able to use this anymore. Hope I won't need it again.", () ->
                                        gui.setComponent(new ScrollingPopup("At least it opened the box though and you find a mysterious artifact", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 1;
                    } else {
                        gui.setComponent(new ScrollingPopup("You manage to slice open the crate and find another mysterious artifact. ", () ->
                                gui.setComponent(new ScrollingPopup("Yes! This completes the other half I found earlier! ", () ->
                                        gui.setComponent(new ScrollingPopup("Wonder what I could do with this butterfly looking object...", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 3;
                    }

                    // Remove the crate and trigger
                    crate1.setMesh(floorMesh);
                    crate1.setSolid(false);

                    puzzle2Indicator.remove(() -> entitiesToRemove.add(puzzle2Indicator));
                    puzzle2Tile.removeTag("trigger");
                }),
                new Solution("saw", (s) -> {
                    if (toolUsed == 2) {
                        gui.setComponent(new ScrollingPopup("Your saw is broken. You miserably fail to open the crate with it.", () ->
                                paused = false
                        ));
                        return;         // Axe already used
                    } else if (toolUsed == 0) {
                        gui.setComponent(new ScrollingPopup("As you saw open the crate the saw handle suddenly breaks off.", () ->
                                gui.setComponent(new ScrollingPopup("Guess I won't be able to use this anymore. Hope I won't need it again.", () ->
                                        gui.setComponent(new ScrollingPopup("At least it opened the box though and you find a mysterious artifact.", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 2;
                    } else {
                        gui.setComponent(new ScrollingPopup("You manage to saw open the crate and find another mysterious artifact.", () ->
                                gui.setComponent(new ScrollingPopup("Yes! This completes the other half I found earlier!", () ->
                                        gui.setComponent(new ScrollingPopup("Wonder what I could do with this butterfly looking object...", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 3;
                    }

                    // Remove the crate and trigger
                    crate1.setMesh(floorMesh);
                    crate1.setSolid(false);

                    puzzle2Indicator.remove(() -> entitiesToRemove.add(puzzle2Indicator));
                    puzzle2Tile.removeTag("trigger");
                })
                },
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("I'm not sure how I can use " + s, () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                })
                , 10
        );

        Tile crate2 = map.getTile("crate2");
        crate2.getMesh().setIsStatic(false);

        // Right Crate Puzzle
        rightCratePuzzle = new Puzzle(
                "To open the crate you use a:",
                // Possible guesses
                new String[]{"axe", "saw"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("axe", (s) -> {
                    if (toolUsed == 1) {
                        gui.setComponent(new ScrollingPopup("Your axe is broken. You miserably fail to open the crate with it.", () ->
                                paused = false
                        ));
                        return;         // Axe already used
                    } else if (toolUsed == 0) {
                        gui.setComponent(new ScrollingPopup("As you slice open the crate the axe head suddenly flies off.", () ->
                                gui.setComponent(new ScrollingPopup("Guess I won't be able to use this anymore. Hope I won't need it again.", () ->
                                        gui.setComponent(new ScrollingPopup("At least it opened the box though and you find a mysterious artifact.", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 1;
                    } else {
                        gui.setComponent(new ScrollingPopup("You manage to slice open the crate and find another mysterious artifact.", () ->
                                gui.setComponent(new ScrollingPopup("Yes! This completes the other half I found earlier!", () ->
                                        gui.setComponent(new ScrollingPopup("Wonder what I could do with this butterfly looking object...", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 3;
                    }

                    // Remove the crate and trigger
                    crate2.setMesh(floorMesh);
                    crate2.setSolid(false);

                    puzzle3Indicator.remove(() -> entitiesToRemove.add(puzzle3Indicator));
                    puzzle3Tile.removeTag("trigger");
                }),
                new Solution("saw", (s) -> {
                    if (toolUsed == 2) {
                        gui.setComponent(new ScrollingPopup("Your saw is broken. You miserably fail to open the crate with it", () ->
                                paused = false
                        ));
                        return;         // Axe already used
                    } else if (toolUsed == 0) {
                        gui.setComponent(new ScrollingPopup("As you saw through the crate the handle suddenly breaks apart.", () ->
                                gui.setComponent(new ScrollingPopup("Guess I won't be able to use this anymore. Hope I won't need it again.", () ->
                                        gui.setComponent(new ScrollingPopup("At least it opened the crate though and you find a mysterious artifact.", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 2;
                    } else {
                        gui.setComponent(new ScrollingPopup("You manage to saw open the crate and find another mysterious artifact. ", () ->
                                gui.setComponent(new ScrollingPopup("Yes! This completes the other half I found earlier! ", () ->
                                        gui.setComponent(new ScrollingPopup("Wonder what I could do with this butterfly looking object...", () ->
                                                gui.setComponent(new ScrollingPopup("You hear another hissing noise...", () ->
                                                        paused = false
                                                ))
                                        ))
                                ))
                        ));
                        toolUsed = 3;
                    }

                    // Remove the crate and trigger
                    crate2.setMesh(floorMesh);
                    crate2.setSolid(false);

                    puzzle3Indicator.remove(() -> entitiesToRemove.add(puzzle3Indicator));
                    puzzle3Tile.removeTag("trigger");
                })
                },
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("What am I supposed to do with " + s, () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                })
                , 10
        );

        // Door Puzzle
        doorPuzzle = new Puzzle(
                "To open the crate you use a:",
                // Possible guesses
                new String[]{"key", "butterfly"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("key", (s) -> {
                    gui.setComponent(new ScrollingPopup("I need some kind of weird object to open this door.", () ->
                            gui.setComponent(new ScrollingPopup("Perhaps I can find it somewhere around here...", () -> {
                                    paused = false;
                                    gui.removeComponent();
                            }))
                    ));
                }),
                new Solution("butterfly", (s) -> {
                    if (toolUsed == 3) {
                        gui.setComponent(new ScrollingPopup("The door opens very slowly.... Hurry up already before that snake catches up!", () -> {
                            paused = false;
                            gui.removeComponent();
                        }));

                        mainDoor.open();

                        puzzle4Indicator.remove(() -> entitiesToRemove.add(puzzle4Indicator));

                    } else {
                        gui.setComponent(new ScrollingPopup("Not sure what I can do with a butterfly...", () -> {
                            paused = false;
                            gui.removeComponent();
                        }));
                    }
                })},
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Not sure what I can do with a " + s, () -> {
                        paused = false;
                        gui.removeComponent();
                    }));
                }), 10
        );

        Tile gemTile = map.getTile("gem");
        gem = new IndicatorEntity(
                gemMesh,
                new Vector3f(gemTile.getPosition().x, 1.5f, gemTile.getPosition().y),
                new Vector3f(45f, 90f, 45f),
                gemTile
        );
        entities.add(gem);

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
        if (!paused) {
            camera.update(interval);
            player.update(interval);
            sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));

            entities.removeAll(entitiesToRemove);

            for (Entity entity : entities) {
                entity.update(interval);
            }

            if (mob != null) {
                mob.update(interval);
                if (mob.isCollidingWithTarget(0.5f)) {
                    gui.setComponent(new ScrollingPopup("You were too slow...", () -> {
                        paused = false;
                        levelController.restart();
                    }));
                    paused = true;
                    return;
                }
            }

            // Check for tiles that have a trigger
            Tile currentPlayerTile = map.getTile(
                    Math.round(player.getPosition().x),
                    Math.round(player.getPosition().z)
            );
            // Handle puzzles
            if (currentPlayerTile.hasTag("trigger")) {
                if (!gui.hasComponent()) {
                    // Show interact hint
                    gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
                }
                if (KeyBinding.isInteractPressed()) {
                    if (currentPlayerTile.hasTag("text1")) {
                        gui.setComponent(text1);
                        paused = true;
                    } else if (currentPlayerTile.hasTag("text2")) {
                        gui.setComponent(text2);
                        paused = true;
                    } else if (currentPlayerTile.hasTag("text3")) {
                            gui.setComponent(text3);
                            paused = true;
                    } else if (currentPlayerTile.hasTag("puzzle1")) {
                        paused = true;
                        gui.setComponent(new ScrollingPopup("These rocks look like they could move if I had some tools for them.", () -> {
                            gui.setComponent(new PuzzleGUI(shovelRockPuzzle));
                        }));
                    } else if (currentPlayerTile.hasTag("puzzle2")) {
                        paused = true;
                        gui.setComponent(new ScrollingPopup("This crate looks suspicious, perhaps with the right tool I could take a peek inside.", () -> {
                            gui.setComponent(new PuzzleGUI(leftCratePuzzle));
                        }));
                    } else if (currentPlayerTile.hasTag("puzzle3")) {
                        paused = true;
                        gui.setComponent(new ScrollingPopup("This crate looks suspicious, I wonder if I could open it with a tool somehow.", () -> {
                            gui.setComponent(new PuzzleGUI(rightCratePuzzle));
                        }));
                    } else if (currentPlayerTile.hasTag("puzzle4")) {
                        paused = true;
                        gui.setComponent(new ScrollingPopup("There is a weird shaped hole in this door...", () -> {
                            gui.setComponent(new PuzzleGUI(doorPuzzle));
                        }));
                    } else if (currentPlayerTile.hasTag("end")) {
                        levelController.switchToMainRoom(MainRoomLevel.MAIN_ROOM_SPAWN.FROM_LEVEL_4);
                    }
                }
            } else if (currentPlayerTile.hasTag("gem")) {
                paused = true;
                gui.setComponent(new ScrollingPopup("You found the blue gem! Quickly go back to the main room before the snake catches you!", () -> {
                    gem.remove(() -> entitiesToRemove.add(gem));
                    currentPlayerTile.removeTag("gem");
                    levelController.setGemFound(LevelController.GEM.BLUE);
                    paused = false;
                }));
            } else if (currentPlayerTile.hasTag("snake_trigger")) {

                paused = true;

                gui.setComponent(new ScrollingPopup("Suddenly you notice the distinct hissing sound coming from the hallway behind you.", () -> {

                    gui.setComponent(new ScrollingPopup("That can't be any good news, quickly try to get out of the room as soon as you can!", () -> {

                        map.getTiles("snake_trigger").forEach((t) -> t.removeTag("snake_trigger"));

                        Tile snakeSpawnTile = map.getTile("snake_spawn");

                        //Mob Spawn
                        mob = new Snake(mobMesh, map);
                        mob.setScale(0.08f);
                        mob.setPosition(snakeSpawnTile.getPosition().x, 0.49f, snakeSpawnTile.getPosition().y);
                        mob.setSpeed(2.6f);
                        mob.setTarget(player);
                        mob.followOnSightOnly(false);
                        entities.add(mob);

                        paused = false;

                    }));

                }));
            } else if (gui.hasComponent()) {
                gui.removeComponent();
            }
        }

        gui.update(interval);
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
        paused = false;
        toolUsed = 0;
        mob = null;
        camera = null;
        sceneLight.cleanup();
    }
}
