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
import game.mobs.SimpleMob;
import game.puzzle.Puzzle;
import game.puzzle.Solution;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

public class PrisonEscapeLevel extends Level{
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
    private ScrollingPopup riddleText, text1, text2;
    /**
     * Puzzles in the level
     */
    private Puzzle doorPuzzle1, doorPuzzle2, doorPuzzle3, doorPuzzle4, doorPuzzle5, wallPuzzle;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    private SimpleMob[] mob;
    private int spawnedMobs = 0;

    public PrisonEscapeLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {

        paused = false;

        entities = new ArrayList<>();
        mob = new SimpleMob[4];
        // Load map
        map = new MapFileLoader("/levels/prisonEscapeLevel.lvl").load();

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
                new Vector3f(3, 11, 3)
        );
        // Mesh for the floor
        Mesh floorMesh = AssetStore.getTileMesh("stone_floor");
        floorMesh.setMaterial(new Material(0f));

        // Mesh for the door
        Mesh doorMesh = AssetStore.getMesh("entities", "bigwooden_door_only");
        doorMesh.setMaterial(new Material(0f));
        doorMesh.setIsStatic(false);

        // Mesh for the pencil
        Mesh pencilMesh = AssetStore.getMesh("entities", "pencil");
        pencilMesh.setMaterial(new Material(0f));
        pencilMesh.setIsStatic(false);

        // Mesh for question mark
        Mesh questionMarkMesh = AssetStore.getMesh("entities", "question_mark");
        questionMarkMesh.setMaterial(new Material(0f));
        questionMarkMesh.setIsStatic(false);

        // Indicator entities
        Tile puzzleTile1 = map.getTile("door_1_puzzle");
        IndicatorEntity puzzle1Indicator1 = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzleTile1.getPosition().x, 1f, puzzleTile1.getPosition().y),
                puzzleTile1
        );
        Tile puzzleTile2 = map.getTile("door_2_puzzle");
        IndicatorEntity puzzle1Indicator2 = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzleTile2.getPosition().x, 1f, puzzleTile2.getPosition().y),
                puzzleTile2
        );
        Tile puzzleTile3 = map.getTile("door_3_puzzle");
        IndicatorEntity puzzle1Indicator3 = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzleTile3.getPosition().x, 1f, puzzleTile3.getPosition().y),
                puzzleTile3
        );
        Tile puzzleTile4 = map.getTile("door_4_puzzle");
        IndicatorEntity puzzle1Indicator4 = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzleTile4.getPosition().x, 1f, puzzleTile4.getPosition().y),
                puzzleTile4
        );
        Tile puzzleTile5 = map.getTile("door_5_puzzle");
        IndicatorEntity puzzle1Indicator5 = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzleTile5.getPosition().x, 1f, puzzleTile5.getPosition().y),
                puzzleTile5
        );
        Tile puzzleTile6 = map.getTile("wall_puzzle");
        IndicatorEntity puzzle1Indicator6 = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzleTile6.getPosition().x, 1f, puzzleTile6.getPosition().y),
                puzzleTile5
        );
        Tile riddleTile = map.getTile("riddle_text");
        IndicatorEntity riddleIndicator = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(riddleTile.getPosition().x, 1f, riddleTile.getPosition().y),
                riddleTile
        );
        Tile textTile = map.getTile("text1");
        IndicatorEntity textIndicator = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile.getPosition().x, 1f, textTile.getPosition().y),
                textTile
        );
        Tile textTile2 = map.getTile("text2");
        IndicatorEntity textIndicator2 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile2.getPosition().x, 1f, textTile2.getPosition().y),
                textTile2
        );
        // Spawn wall
        Tile wall = map.getTile("cracked_wall");
        wall.setSolid(true);
        Mesh wallMesh = PLYLoader.loadMesh("/models/tiles/corner_wall.ply");
        wallMesh.setMaterial(new Material(0f));
        wall.setMesh(wallMesh);

        // Setup doors
        Tile door1Left = map.getTile("door1");
        DoorEntity door1 = new DoorEntity(
                doorMesh,
                new Vector3f(door1Left.getPosition().x - 0.5f, 0f, door1Left.getPosition().y + 0.4f),
                new Vector3f(0f),
                0.5f,
                door1Left
        );
        door1Left.setSolid(true);
        Tile door1Right = map.getTile("door1tile");
        door1Right.setSolid(true);

        Tile door2Left = map.getTile("door2");
        DoorEntity door2 = new DoorEntity(
                doorMesh,
                new Vector3f(door2Left.getPosition().x - 0.5f, 0f, door2Left.getPosition().y + 0.4f),
                new Vector3f(0f),
                0.5f,
                door2Left
        );
        door2Left.setSolid(true);
        Tile door2Right = map.getTile("door2tile");
        door2Right.setSolid(true);

        Tile door3Left = map.getTile("door3");
        DoorEntity door3 = new DoorEntity(
                doorMesh,
                new Vector3f(door3Left.getPosition().x - 0.5f, 0f, door3Left.getPosition().y + 0.4f),
                new Vector3f(0f),
                0.5f,
                door3Left
        );
        door3Left.setSolid(true);
        Tile door3Right = map.getTile("door3tile");
        door3Right.setSolid(true);

        Tile door4Left = map.getTile("door4");
        DoorEntity door4 = new DoorEntity(
                doorMesh,
                new Vector3f(door4Left.getPosition().x - 0.5f, 0f, door4Left.getPosition().y + 0.4f),
                new Vector3f(0f),
                0.5f,
                door4Left
        );
        door4Left.setSolid(true);
        Tile door4Right = map.getTile("door4tile");
        door4Right.setSolid(true);

        Tile door5Left = map.getTile("door5");
        DoorEntity door5 = new DoorEntity(
                doorMesh,
                new Vector3f(door5Left.getPosition().x - 0.5f, 0f, door5Left.getPosition().y + 0.4f),
                new Vector3f(0f),
                0.5f,
                door5Left
        );
        door5Left.setSolid(true);
        Tile door5Right = map.getTile("door5tile");
        door5Right.setSolid(true);

        //Puzzles
        doorPuzzle1 = new Puzzle(
                "This does nothing...",
                new String[]{"key"},
                new Solution[]{
                        new Solution( "key", (s) -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door1.open();
                                door1Left.setSolid(false);
                                door1Right.setSolid(false);
                                // Spawn mob
                                spawnMob("ghost1spawn");
                                spawnInterior("interior1");
                                // Remove indicators
                                puzzle1Indicator1.remove(() -> entitiesToRemove.add(puzzle1Indicator1));
                                // Remove triggers
                                puzzleTile1.removeTag("trigger");
                                // Resume the game
                                paused = false;
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Stop screwing around! How will I use a " + s + " to open a door?", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle2 = new Puzzle(
                "This does nothing...",
                new String[]{"key"},
                new Solution[]{
                        new Solution( "key", (s) -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door2.open();
                                door2Left.setSolid(false);
                                door2Right.setSolid(false);
                                // Spawn mob
                                spawnMob("ghost2spawn");
                                spawnInterior("interior2");
                                // Remove indicators
                                puzzle1Indicator2.remove(() -> entitiesToRemove.add(puzzle1Indicator2));
                                // Remove triggers
                                puzzleTile2.removeTag("trigger");
                                // Resume the game
                                paused = false;
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Stop screwing around! How will I use a " + s + " to open a door?", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle3 = new Puzzle(
                "This does nothing...",
                new String[]{"key"},
                new Solution[]{
                        new Solution( "key", (s) -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door3.open();
                                door3Left.setSolid(false);
                                door3Right.setSolid(false);
                                spawnInterior("interior3");
                                // Remove indicators
                                puzzle1Indicator3.remove(() -> entitiesToRemove.add(puzzle1Indicator3));
                                // Remove triggers
                                puzzleTile3.removeTag("trigger");
                                // Resume the game
                                paused = false;
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Stop screwing around! How will I use a " + s + " to open a door?", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle4 = new Puzzle(
                "This does nothing...",
                new String[]{"key"},
                new Solution[]{
                        new Solution( "key", (s) -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door4.open();
                                door4Left.setSolid(false);
                                door4Right.setSolid(false);
                                // Spawn mob
                                spawnMob("ghost4spawn");
                                spawnInterior("interior4");
                                // Remove indicators
                                puzzle1Indicator4.remove(() -> entitiesToRemove.add(puzzle1Indicator4));
                                // Remove triggers
                                puzzleTile4.removeTag("trigger");
                                // Resume the game
                                paused = false;
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Stop screwing around! How will I use a " + s + " to open a door?", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle5 = new Puzzle(
                "This does nothing...",
                new String[]{"key"},
                new Solution[]{
                        new Solution( "key", (s) -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door5.open();
                                door5Left.setSolid(false);
                                door5Right.setSolid(false);
                                // Spawn new puzzle and cracked wall
                                spawnInterior("interior5");
                                try {
                                    Mesh crackedWall = PLYLoader.loadMesh("/models/tiles/crate.ply");
                                    crackedWall.setMaterial(new Material(0f));
                                    crackedWall.setIsStatic(false);
                                    wall.setMesh(crackedWall);//TODO: replace with cracked model
                                } catch (Exception e){}
                                entities.add(puzzle1Indicator6);
                                entities.add(textIndicator2);
                                // Remove indicators
                                puzzle1Indicator5.remove(() -> entitiesToRemove.add(puzzle1Indicator5));
                                // Remove triggers
                                puzzleTile5.removeTag("trigger");
                                // Resume the game
                                paused = false;
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("Stop screwing around! How will I use a " + s + " to open a door?", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );

        wallPuzzle = new Puzzle(
                "This does nothing...",
                new String[]{"key", "cannon", "hammer", "guitar", "lightning"},
                new Solution[]{
                        new Solution( "hammer", (s) -> {
                            gui.setComponent(new ScrollingPopup("You break down the wall with the hammer!", () -> {
                                gui.setComponent(new ScrollingPopup("There seems to be a tunnel here!", () -> {
                                    gui.removeComponent();
                                    // Remove the wall
                                    try {
                                        wall.setMesh(floorMesh);
                                        wall.setSolid(false);
                                    } catch (Exception e){}
                                    // Remove indicators
                                    puzzle1Indicator6.remove(() -> entitiesToRemove.add(puzzle1Indicator6));
                                    // Remove triggers
                                    puzzleTile6.removeTag("trigger");
                                    // Resume the game
                                    paused = false;
                                }));
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("I don't think that using " + s + " will work.", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        // Text
        riddleText = new ScrollingPopup("There seems to be five cells. I need to find the escaped prisoner's" +
                " cell. All the cells have a number, but someone forgot to put the signs up! The escapee is in prison cell" +
                " 3, however, the cells numbers are all jumbled", () -> {
            gui.setComponent(new ScrollingPopup("Prisoner 3 was in a cell adjacent to the right of prisoner 5. "
                    + "Prisoner 2 was in a cell at the left end of the hall. Prisoner 1 is in a cell "
                    + "neighbouring prisoner 5 and 4.", () -> {
                gui.removeComponent();
                paused = false;
            }));

        });

        text1 = new ScrollingPopup("You have entered the dungeon!", () -> {
            gui.setComponent(new ScrollingPopup("This is where the prisoners were locked up, one of the prisoners tried to" +
                    " escape recently!", () -> {
                gui.removeComponent();
                textIndicator.remove(() -> {entitiesToRemove.add(textIndicator);});
                textTile.removeTag("trigger");

                paused = false;
            }));
        });

        text2 = new ScrollingPopup("This seems to be the prison cell of the escaped prisoner, I wonder how he escaped!", () -> {
            gui.setComponent(new ScrollingPopup("Maybe it has to do with that crack in the wall over there?", () -> {
                gui.removeComponent();
                textIndicator2.remove(() -> {entitiesToRemove.add(textIndicator2);});
                textTile2.removeTag("trigger");
                paused = false;
            }));
        });

        // Set up ambient light
        sceneLight = new SceneLight();
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));
        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.2f, 0.4f, 0.8f),       // color
                new Vector3f(0.0f, 1.0f, 0.4f),       // direction
                0.3f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                false
        );
        map.getTiles("lantern").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.968f, 0.788f, 0.390f),
                            new Vector3f(t.getPosition().x, 4.5f, t.getPosition().y),
                            1f,
                            new PointLight.Attenuation(0f, 0.7f, 0f),
                            new Vector2f(1f, 100f)
                    )
            );
        });
        map.getTiles("lantern_floor").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.968f, 0.588f, 0.290f),
                            new Vector3f(t.getPosition().x, 1f, t.getPosition().y),
                            1f,
                            new PointLight.Attenuation(0f, 1f, 0f),
                            new Vector2f(1f, 100f)
                    )
            );
            Tile floorLantern = map.getTile("lantern_floor");
            floorLantern.setSolid(true);
        });
        map.getTiles("lantern_crate").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.768f, 0.688f, 0.290f),
                            new Vector3f(t.getPosition().x, 2.5f, t.getPosition().y),
                            1f,
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
                player,
                door1, puzzle1Indicator1,
                door2, puzzle1Indicator2,
                door3, puzzle1Indicator3,
                door4, puzzle1Indicator4,
                door5, puzzle1Indicator5,
                textIndicator, riddleIndicator
        ));
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        gui.update(interval, mouseInput);

        if (paused) {
            return;
        }

        entities.forEach(e -> e.update(interval));
        entitiesToRemove.forEach(e -> entities.remove(e));

        Tile currentPlayerTile = map.getTile(
                Math.round(player.getPosition().x),
                Math.round(player.getPosition().z)
        );

        // Mob handling
        for (int i = 0; i < mob.length; i++) {
            if (mob[i]!=null) {
                if (mob[i].isCollidingWithTarget(1.7f)) {
                    gui.setComponent(new ScrollingPopup("The ghost caught you, try again!", () -> {
                        levelController.restart();
                    }));
                    paused = true;

                    return;
                }
            }
        }

        if (currentPlayerTile.hasTag("trigger")) {
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }
            if (KeyBinding.isInteractPressed()) {
                if (currentPlayerTile.hasTag("door_1_puzzle")) {
                    gui.setComponent(new PuzzleGUI(doorPuzzle1));
                    paused = true;
                }
                if (currentPlayerTile.hasTag("door_2_puzzle")) {
                    gui.setComponent(new PuzzleGUI(doorPuzzle2));
                    paused = true;
                }
                if (currentPlayerTile.hasTag("door_3_puzzle")) {
                    gui.setComponent(new PuzzleGUI(doorPuzzle3));
                    paused = true;
                }
                if (currentPlayerTile.hasTag("door_4_puzzle")) {
                    gui.setComponent(new PuzzleGUI(doorPuzzle4));
                    paused = true;
                }
                if (currentPlayerTile.hasTag("door_5_puzzle")) {
                    gui.setComponent(new PuzzleGUI(doorPuzzle5));
                    paused = true;
                }
                if (currentPlayerTile.hasTag("wall_puzzle")) {
                    gui.setComponent(new PuzzleGUI(wallPuzzle));
                    paused = true;
                }
                if (currentPlayerTile.hasTag("ladder")) {
                    levelController.switchToMainRoom(MainRoomLevel.MAIN_ROOM_SPAWN.FROM_LEVEL_2);
                }
                if (currentPlayerTile.hasTag("riddle_text")) {
                    gui.setComponent(riddleText);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("text1")) {
                    gui.setComponent(text1);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("text2")) {
                    gui.setComponent(text2);
                    paused = true;
                }
            }
            if (currentPlayerTile.hasTag("enter_tunnel")) {
                levelController.next();
            }
        } else if (gui.hasComponent()) {
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

    }

    private void spawnMob(String tag) {
        try {
            Mesh mobMesh = PLYLoader.loadMesh("/models/entities/ghost.ply");
            mobMesh.setMaterial(new Material(0.0f));
            mobMesh.setIsStatic(false);
            mob[spawnedMobs] = new SimpleMob(mobMesh, map);
            mob[spawnedMobs].setScale(1f);
            mob[spawnedMobs].setPosition( map.getTile(tag).getPosition().x,
                    map.getTile(tag).getPosition().y, 0f);
            mob[spawnedMobs].setSpeed(1f);
            mob[spawnedMobs].setTarget(player);
            mob[spawnedMobs].followOnSightOnly(false);
            entities.add(mob[spawnedMobs]);
            spawnedMobs++;
        } catch(Exception e) {}
    }

    private void spawnInterior(String tag) {
        map.getTiles(tag).forEach(t -> {
            try {
                if (t.hasTag("bed")) {
                    Mesh bedMesh = PLYLoader.loadMesh("/models/tiles/bed.ply");
                    bedMesh.setMaterial(new Material(0f));
                    t.setSolid(true);
                    t.setMesh(bedMesh);
                }
                if (t.hasTag("skeleton")) {
                    Mesh skeletonMesh = PLYLoader.loadMesh("/models/tiles/crate.ply");
                    skeletonMesh.setMaterial(new Material(0f));
                    t.setSolid(true);
                    t.setMesh(skeletonMesh);
                }
            } catch (Exception e) {}
        });
    }
}
