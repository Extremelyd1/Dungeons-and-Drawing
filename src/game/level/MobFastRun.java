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
import org.joml.Vector2i;
import org.joml.Vector3f;
import sun.security.ssl.Debug;

import java.util.ArrayList;
import java.util.List;

public class MobFastRun extends Level {
    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    private SceneLight sceneLight;
    private GUI gui;
    private Puzzle shovelRockPuzzle, doorPuzzle, leftCratePuzzle, rightCratePuzzle;
    private Entity leftCrateQuestionMark, rightCrateQuestionMark, doorQuestionMark;
    IndicatorEntity gem;
    private DoorEntity mainDoor;
    private Snake mob = null;
    private int toolUsed = 0;

    private boolean paused = false;

    public MobFastRun(LevelController levelController) {
        super(levelController);
    }

    /**
     * Level Infos
     *
     * Tile 14/1 snake spawn tile
     *
     * Tile 10/27 open left box (Entity 1)
     *
     * Tile 34/27 open right box (Entity 2)
     *
     * Tile 27/12 shovel rock out of the way (Entity 0) (Rock at 28/12)     DONE
     *
     * Tile 23/30 door open after both boxes are open (Entity 4)
     *
     * Tile 23/29 key(Entity 3)
     *
     * Tile 23/36 End of level
     */

    @Override
    public void init() throws Exception {
        // Load Map tiles
        MapFileLoader mapFileLoader = new MapFileLoader("/levels/mobRun/generatedEditorLevel_tiles.lvl");
        map = mapFileLoader.load();
        final Tile tiles[][] = map.getTiles();
        // Setup tiles
        tiles[28][12].getMesh().setIsStatic(false);

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

        for (int i = 0; i < 10; i++) {
            sceneLight.pointLights.add(new PointLight(
                    new Vector3f(1f, 1f, 1f),
                    new Vector3f(2, 2, 2),
                    0.0f,
                    new Vector2f(0.1f, 60f)
            ));
            sceneLight.pointLights.get(i).setAttenuation(new PointLight.Attenuation(0.0f, 0.00f,0.2f));
        }
        LevelEditor.loadLights("/resources/levels/mobRun/", "generatedEditorLevel_lights.lvl", sceneLight);

        // Setup map Entities
        entities = new ArrayList<>();
        LevelEditor.loadEntities("/resources/levels/mobRun", "generatedEditorLevel_entities.lvl", entities);
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).getMesh().setIsStatic(false);
        }

        // Setup Question Marks
        IndicatorEntity triggerEntity[] = new IndicatorEntity[5];
        for (int i =0; i < 4; i++) {
            triggerEntity[i] = new IndicatorEntity(
                entities.get(i).getMesh(),
                new Vector3f(entities.get(i).getPosition().x, 1f, entities.get(i).getPosition().z),
                tiles[Math.round(entities.get(i).getPosition().x)][Math.round(entities.get(i).getPosition().z)]);
            entities.set(i, triggerEntity[i]);
        }
        leftCrateQuestionMark = entities.get(1);
        rightCrateQuestionMark = entities.get(2);
        doorQuestionMark = entities.get(3);

        mainDoor = new DoorEntity(
                entities.get(4).getMesh(),
                entities.get(4).getPosition(),
                entities.get(4).getRotation(),
                0.5f,
                tiles[Math.round(entities.get(4).getPosition().x)][Math.round(entities.get(4).getPosition().z)]
        );
        entities.set(4, mainDoor);

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
        player.setSpeed(4f);
        player.setScale(new Vector3f(0.25f));
        player.setPosition(1, 0.5f, 9);
        entities.add(player);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(2, 11, 3)
        );

        // Setup Puzzles
        //// Shovel Rock Puzzle
        shovelRockPuzzle = new Puzzle(
                "To move the rock out of the way you use a:",
                // Possible guesses
                new String[]{"shovel"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("shovel", (s) -> {
                    gui.setComponent(new ScrollingPopup("Your shovel moves the rock out of the way however it snaps making a loud noise", () ->
                            gui.setComponent(new ScrollingPopup("I hear a hissing noise in the distance... I better hurry up...", () ->
                                    paused = false
                            ))
                    ));
                    // Remove the arc and replace the entire row with boulders to block the mob path
                    tiles[28][12] = new Tile(new Vector2i(28, 12), new Vector3f(tiles[28][12].getRotation()), AssetStore.getTileMesh("stone_floor"), true);
                    tiles[28][12].getMesh().setIsStatic(false);
                    tiles[28][12].setSolid(false);
                    tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)].removeTag("trigger1");
                    entities.remove(0);     // Remove Question Mark

                    //Mob Spawn
                    Mesh mobMesh = null;
                    try { mobMesh = PLYLoader.loadMesh("/models/entities/snake.ply");} catch (Exception e) {}
                    mobMesh.setMaterial(new Material(0.0f));
                    mobMesh.setIsStatic(false);
                    mob = new Snake(mobMesh, map);
                    mob.setScale(0.08f);
                    mob.setPosition(14, 0.49f, 1);
                    mob.setSpeed(2.5f);
                    mob.setTarget(player);
                    mob.followOnSightOnly(false);
                    entities.add(mob);

                })}, new Solution("", (s) -> {
            gui.removeComponent();
            paused = false;
        })
                , 10
        );
        tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)].addTag("trigger1");

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

                    // Remove the crate and speed up the mob
                    tiles[10][27] = new Tile(new Vector2i(10, 27), new Vector3f(tiles[10][27].getRotation()), AssetStore.getTileMesh("stone_floor"), true);
                    tiles[10][27].getMesh().setIsStatic(false);
                    tiles[10][27].setSolid(false);
                    tiles[Math.round(entities.get(1).getPosition().x)][Math.round(entities.get(1).getPosition().z)].removeTag("trigger2");
                    entities.remove(leftCrateQuestionMark);
                }),
                new Solution("saw", (s) -> {
                    if (toolUsed == 2) {
                        gui.setComponent(new ScrollingPopup("Your saw is broken. You miserably fail to open the crate with it", () ->
                                paused = false
                        ));
                        return;         // Axe already used
                    } else if (toolUsed == 0) {
                        gui.setComponent(new ScrollingPopup("As you saw open the crate the saw handle suddenly breaks off.", () ->
                                gui.setComponent(new ScrollingPopup("Guess I won't be able to use this anymore. Hope I won't need it again.", () ->
                                        gui.setComponent(new ScrollingPopup("At least it opened the box though and you find a mysterious artifact", () ->
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

                    // Remove the crate and speed up the mob
                    tiles[10][27] = new Tile(new Vector2i(10, 27), new Vector3f(tiles[10][27].getRotation()), AssetStore.getTileMesh("stone_floor"), true);
                    tiles[10][27].getMesh().setIsStatic(false);
                    tiles[10][27].setSolid(false);
                    tiles[Math.round(entities.get(1).getPosition().x)][Math.round(entities.get(1).getPosition().z)].removeTag("trigger2");
                    entities.remove(leftCrateQuestionMark);
                })
                },
                new Solution("", (s) -> {
                    gui.removeComponent();
                    paused = false;
                })
                , 10
        );
        tiles[Math.round(entities.get(1).getPosition().x)][Math.round(entities.get(1).getPosition().z)].addTag("trigger2");

        // Right Crate Puzzle
        rightCratePuzzle = new Puzzle(
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
                        gui.setComponent(new ScrollingPopup("As you slice open the crate the axe head suddenly flies off.", () ->
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

                    // Remove the crate and speed up the mob
                    tiles[34][27] = new Tile(new Vector2i(34, 27), new Vector3f(tiles[34][27].getRotation()), AssetStore.getTileMesh("stone_floor"), true);
                    tiles[34][27].getMesh().setIsStatic(false);
                    tiles[34][27].setSolid(false);
                    tiles[Math.round(entities.get(2).getPosition().x)][Math.round(entities.get(2).getPosition().z)].removeTag("trigger3");
                    entities.remove(rightCrateQuestionMark);
                }),
                new Solution("saw", (s) -> {
                    if (toolUsed == 2) {
                        gui.setComponent(new ScrollingPopup("Your saw is broken. You miserably fail to open the crate with it", () ->
                                paused = false
                        ));
                        return;         // Axe already used
                    } else if (toolUsed == 0) {
                        gui.setComponent(new ScrollingPopup("As you saw through the crate the handle suddenly breaks apart", () ->
                                gui.setComponent(new ScrollingPopup("Guess I won't be able to use this anymore. Hope I won't need it again.", () ->
                                        gui.setComponent(new ScrollingPopup("At least it opened the crate though and you find a mysterious artifact", () ->
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

                    // Remove the crate and speed up the mob
                    tiles[34][27] = new Tile(new Vector2i(34, 27), new Vector3f(tiles[34][27].getRotation()), AssetStore.getTileMesh("stone_floor"), true);
                    tiles[34][27].getMesh().setIsStatic(false);
                    tiles[34][27].setSolid(false);
                    tiles[Math.round(entities.get(2).getPosition().x)][Math.round(entities.get(2).getPosition().z)].removeTag("trigger3");
                    entities.remove(rightCrateQuestionMark);
                })
                },
                new Solution("", (s) -> {
                    gui.removeComponent();
                    paused = false;
                })
                , 10
        );
        tiles[Math.round(entities.get(2).getPosition().x)][Math.round(entities.get(2).getPosition().z)].addTag("trigger3");

        // Door Puzzle
        doorPuzzle = new Puzzle(
                "To open the crate you use a:",
                // Possible guesses
                new String[]{"key", "butterfly"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("key", (s) -> {
                    gui.setComponent(new ScrollingPopup("I need some kind of weird object to open this door", () ->
                            gui.setComponent(new ScrollingPopup("Perhaps I can find it somewhere around here...", () ->
                                    paused = false
                            ))
                    ));
                }),
                new Solution("butterfly", (s) -> {
                    if (toolUsed == 3) {
                        mainDoor.open();
                        gui.setComponent(new ScrollingPopup("The door opens.... Hurry up already before that snake catches up!", () ->
                                paused = false
                        ));
                    } else {
                        gui.setComponent(new ScrollingPopup("Not sure what I can do with a butterfly...", () ->
                                paused = false
                        ));
                    }
                })},
                new Solution("", (s) -> {
                    gui.removeComponent();
                    paused = false;
                })
                , 10
        );
        tiles[Math.round(entities.get(3).getPosition().x)][Math.round(entities.get(3).getPosition().z)].addTag("trigger4");

        // Add gem
        // Load gem
        Mesh gemMesh = AssetStore.getMesh("entities", "gem_blue");
        gemMesh.setMaterial(new Material(0f));
        gemMesh.setIsStatic(false);

        gem = new IndicatorEntity(
                gemMesh,
                new Vector3f(23, 1.5f, 35),
                new Vector3f(45f, 90f, 45f),
                null
        );
        tiles[23][35].addTag("gem");
        entities.add(gem);

        paused = false;

        // DEBUG
        Debug.println("MobFastRun", "Mob Fast Run level loaded");
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

            for (Entity entity : entities) {
                entity.update(interval);
            }

            if (mob != null) {
                mob.update(interval);
                if (mob.isCollidingWithTarget(0.95f)) {
                    //levelController.restart();
                    gui.setComponent(new ScrollingPopup("You were too slow...", () -> {
                        paused = false;
                        Debug.println("MobFastRun", "Level restart");
                        levelController.restart();
                    }));
                    Debug.println("MobFastRun", "Paused");
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
            if (currentPlayerTile.hasTag("trigger1")) {
                if (!gui.hasComponent()) {
                    // Show interact hint
                    gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
                    // Check the exact trigger
                }
                if (KeyBinding.isInteractPressed() && currentPlayerTile.hasTag("trigger1")) {
                    // Show puzzle GUI
                    gui.setComponent(new PuzzleGUI(shovelRockPuzzle));
                    paused = true;
                }
                // If not on any trigger anymore, remove floating text
            } else if (currentPlayerTile.hasTag("trigger2")) {
                if (!gui.hasComponent()) {
                    // Show interact hint
                    gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
                    // Check the exact trigger
                }
                if (KeyBinding.isInteractPressed() && currentPlayerTile.hasTag("trigger2")) {
                    // Show puzzle GUI
                    gui.setComponent(new PuzzleGUI(leftCratePuzzle));
                    paused = true;
                }
                // If not on any trigger anymore, remove floating text
            } else if (currentPlayerTile.hasTag("trigger3")) {
                if (!gui.hasComponent()) {
                    // Show interact hint
                    gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
                    // Check the exact trigger
                }
                if (KeyBinding.isInteractPressed() && currentPlayerTile.hasTag("trigger3")) {
                    // Show puzzle GUI
                    gui.setComponent(new PuzzleGUI(rightCratePuzzle));
                    paused = true;
                }
                // If not on any trigger anymore, remove floating text
            } else if (currentPlayerTile.hasTag("trigger4")) {
                if (!gui.hasComponent()) {
                    // Show interact hint
                    gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
                    // Check the exact trigger
                }
                if (KeyBinding.isInteractPressed() && currentPlayerTile.hasTag("trigger4")) {
                    // Show puzzle GUI
                    gui.setComponent(new PuzzleGUI(doorPuzzle));
                    paused = true;
                }
                // If not on any trigger anymore, remove floating text
            } else if (currentPlayerTile.hasTag("gem")) {
                entities.remove(gem);
                currentPlayerTile.removeTag("gem");
                levelController.setGemFound(LevelController.GEM.BLUE);
            }else if (gui.hasComponent()) {
                gui.removeComponent();
            }

            // Check end of level
            if (Math.round(player.getPosition().x) == 23 && Math.round(player.getPosition().z) == 36) {
                levelController.switchToMainRoom(MainRoomLevel.MAIN_ROOM_SPAWN.FROM_LEVEL_4);
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
