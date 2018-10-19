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
    private ScrollingPopup text1, hintText1, hintText2, hintText3;
    /**
     * Puzzles in the level
     */
    private Puzzle doorPuzzle1, doorPuzzle2, doorPuzzle3, doorPuzzle4, doorPuzzle5, wallPuzzle;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    private Snake[] mob;
    private int spawnedMobs = 0;
    /**
     * @TODO: replace snake with ghost and edit speed
     * @TODO: spawn wall and cracked wall
     * @TODO: spawn wall beds and skeletons in cell when opened
     * @TODO: have story and questions riddle
     */
    public PrisonEscapeLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();
        mob = new Snake[4];
        // Load map
        map = new MapFileLoader("/levels/prisonEscapeLevel.lvl").load();

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
                new String[]{"key", "lightning", "mug"},
                new Solution[]{
                        new Solution( "key", () -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door1.open();
                                door1Left.setSolid(false);
                                door1Right.setSolid(false);
                                // Spawn mob
                                try {
                                    Mesh mobMesh = PLYLoader.loadMesh("/models/entities/snake.ply");
                                    mobMesh.setMaterial(new Material(0.0f));
                                    mobMesh.setIsStatic(false);
                                    mob[spawnedMobs] = new Snake(mobMesh, map);
                                    mob[spawnedMobs].setScale(0.08f);
                                    mob[spawnedMobs].setPosition( map.getTile("ghost1spawn").getPosition().x,
                                            map.getTile("ghost1spawn").getPosition().y, 0f);
                                    mob[spawnedMobs].setSpeed(2.5f);
                                    mob[spawnedMobs].setTarget(player);
                                    mob[spawnedMobs].followOnSightOnly(false);
                                    entities.add(mob[spawnedMobs]);
                                    spawnedMobs++;
                                } catch(Exception e) {}
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
                new Solution("", () -> {
                    // TODO: This should fire when anything else than the solution above is provided
                    gui.setComponent(new ScrollingPopup("Hm, no, that's not quite right.", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle2 = new Puzzle(
                "This does nothing...",
                new String[]{"key", "lightning", "mug"},
                new Solution[]{
                        new Solution( "key", () -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door2.open();
                                door2Left.setSolid(false);
                                door2Right.setSolid(false);
                                // Spawn mob
                                try {
                                    Mesh mobMesh = PLYLoader.loadMesh("/models/entities/snake.ply");
                                    mobMesh.setMaterial(new Material(0.0f));
                                    mobMesh.setIsStatic(false);
                                    mob[spawnedMobs] = new Snake(mobMesh, map);
                                    mob[spawnedMobs].setScale(0.08f);
                                    mob[spawnedMobs].setPosition( map.getTile("ghost1spawn").getPosition().x,
                                            map.getTile("ghost1spawn").getPosition().y, 0f);
                                    mob[spawnedMobs].setSpeed(2.5f);
                                    mob[spawnedMobs].setTarget(player);
                                    mob[spawnedMobs].followOnSightOnly(false);
                                    entities.add(mob[spawnedMobs]);
                                    spawnedMobs++;
                                } catch(Exception e) {}
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
                new Solution("", () -> {
                    // TODO: This should fire when anything else than the solution above is provided
                    gui.setComponent(new ScrollingPopup("Hm, no, that's not quite right.", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle3 = new Puzzle(
                "This does nothing...",
                new String[]{"key", "lightning", "mug"},
                new Solution[]{
                        new Solution( "key", () -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door3.open();
                                door3Left.setSolid(false);
                                door3Right.setSolid(false);
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
                new Solution("", () -> {
                    // TODO: This should fire when anything else than the solution above is provided
                    gui.setComponent(new ScrollingPopup("Hm, no, that's not quite right.", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle4 = new Puzzle(
                "This does nothing...",
                new String[]{"key", "lightning", "mug"},
                new Solution[]{
                        new Solution( "key", () -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door4.open();
                                door4Left.setSolid(false);
                                door4Right.setSolid(false);
                                // Spawn mob
                                try {
                                    Mesh mobMesh = PLYLoader.loadMesh("/models/entities/snake.ply");
                                    mobMesh.setMaterial(new Material(0.0f));
                                    mobMesh.setIsStatic(false);
                                    mob[spawnedMobs] = new Snake(mobMesh, map);
                                    mob[spawnedMobs].setScale(0.08f);
                                    mob[spawnedMobs].setPosition( map.getTile("ghost1spawn").getPosition().x,
                                            map.getTile("ghost1spawn").getPosition().y, 0f);
                                    mob[spawnedMobs].setSpeed(2.5f);
                                    mob[spawnedMobs].setTarget(player);
                                    mob[spawnedMobs].followOnSightOnly(false);
                                    entities.add(mob[spawnedMobs]);
                                    spawnedMobs++;
                                } catch(Exception e) {}//@TODO: spawn mob
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
                new Solution("", () -> {
                    // TODO: This should fire when anything else than the solution above is provided
                    gui.setComponent(new ScrollingPopup("Hm, no, that's not quite right.", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );
        doorPuzzle5 = new Puzzle(
                "This does nothing...",
                new String[]{"key", "lightning", "mug"},
                new Solution[]{
                        new Solution( "key", () -> {
                            gui.setComponent(new ScrollingPopup("The door swings open!", () -> {
                                gui.removeComponent();
                                // Open door
                                door5.open();
                                door5Left.setSolid(false);
                                door5Right.setSolid(false);
                                // Spawn new puzzle and cracked wall
                                try {
                                    Mesh crackedWall = PLYLoader.loadMesh("/models/tiles/crate.ply");
                                    crackedWall.setMaterial(new Material(0f));
                                    crackedWall.setIsStatic(false);
                                    wall.setMesh(crackedWall);//@TODO: replace with cracked wall
                                } catch (Exception e){}
                                //@TODO: spawn new puzzle
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
                new Solution("", () -> {
                    // TODO: This should fire when anything else than the solution above is provided
                    gui.setComponent(new ScrollingPopup("Hm, no, that's not quite right.", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
        );


        // Set up ambient light
        sceneLight = new SceneLight();
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.5f));
        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.2f, 0.4f, 0.8f),       // color
                new Vector3f(0.0f, 1.0f, 0.4f),       // direction
                0.05f,                                // intensity
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
                            0.4f,
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
                            0.45f,
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
                door5, puzzle1Indicator5
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

    }
}
