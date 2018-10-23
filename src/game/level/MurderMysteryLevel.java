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
import game.puzzle.Puzzle;
import game.puzzle.Solution;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

public class MurderMysteryLevel extends Level {

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
    private ScrollingPopup text1, hintText1, hintText2, hintText3, gemText;
    /**
     * Puzzles in the level
     */
    private Puzzle puzzle1;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    public MurderMysteryLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();

        // Load map
        map = new MapFileLoader("/levels/murder_mystery_level.lvl").load();

        // Set crates to non-static so the shadow map is properly updated when they are removed
        map.getTiles("crate").forEach(c -> {
            c.getMesh().setIsStatic(false);
        });

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

        // Load gem
        Mesh greenGemMesh = AssetStore.getMesh("entities", "gem_green");
        greenGemMesh.setMaterial(new Material(0f));
        greenGemMesh.setIsStatic(false);

        Vector2i shrinePos = map.getTile("shrine").getPosition();
        IndicatorEntity greenGem = new IndicatorEntity(
                greenGemMesh,
                new Vector3f(shrinePos.x, 1.5f, shrinePos.y),
                new Vector3f(45f, 90f, 45f),
                null
        );

        // Load normal floor mesh
        Mesh floorMesh = AssetStore.getTileMesh("stone_floor");
        floorMesh.setMaterial(new Material(0f));

        // Load ghost mesh
        Mesh ghostMesh = AssetStore.getMesh("entities", "ghost");
        ghostMesh.setMaterial(new Material(0f));

        // Create ghosts
        Vector2i ghost1Pos = map.getTile("ghost_1").getPosition();
        Vector2i ghost2Pos = map.getTile("ghost_2").getPosition();
        Vector2i ghost3Pos = map.getTile("ghost_3").getPosition();
        Vector2i puzzleGhostPos = map.getTile("puzzle_ghost").getPosition();

        Entity ghost1 = new Entity(
                ghostMesh,
                new Vector3f(ghost1Pos.x, 1.5f, ghost1Pos.y),
                new Vector3f(0f, 0f, 0f)
        );
        Entity ghost2 = new Entity(
                ghostMesh,
                new Vector3f(ghost2Pos.x, 1.5f, ghost2Pos.y),
                new Vector3f(0f, 90f, 0f)
        );
        Entity ghost3 = new Entity(
                ghostMesh,
                new Vector3f(ghost3Pos.x, 1.5f, ghost3Pos.y),
                new Vector3f(0f, 90f, 0f)
        );
        Entity puzzleGhost = new Entity(
                ghostMesh,
                new Vector3f(puzzleGhostPos.x, 2f, puzzleGhostPos.y),
                new Vector3f(0f, 90f, 0f)
        );

        // Create interactive tiles
        Tile textTile1 = map.getTile("murder_mystery_text_1");
        IndicatorEntity textIndicator1 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile1.getPosition().x, 1f, textTile1.getPosition().y),
                textTile1
        );

        Tile hintTile1 = map.getTile("puzzle_hint_1");
        IndicatorEntity hintIndicator1 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(hintTile1.getPosition().x, 1f, hintTile1.getPosition().y),
                textTile1
        );
        Tile hintTile2 = map.getTile("puzzle_hint_2");
        IndicatorEntity hintIndicator2 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(hintTile2.getPosition().x, 1f, hintTile2.getPosition().y),
                textTile1
        );
        Tile hintTile3 = map.getTile("puzzle_hint_3");
        IndicatorEntity hintIndicator3 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(hintTile3.getPosition().x, 1f, hintTile3.getPosition().y),
                textTile1
        );

        Tile puzzleTile1 = map.getTile("murder_mystery_puzzle_1");
        IndicatorEntity puzzle1Indicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(puzzleTile1.getPosition().x, 1f, puzzleTile1.getPosition().y),
                puzzleTile1
        );

        // Create dialogue
        text1 = new ScrollingPopup("Boo! ... Oh, it didn't scare you...", () -> {
            gui.setComponent(new ScrollingPopup("Ah, you're not here for me I see.. it's the gem, isn't it? ", () -> {
                gui.setComponent(new ScrollingPopup("Hm, it only seems that there's a huge pile of boxes blocking your way. I could help you, but I want something in return.", () -> {
                    gui.setComponent(new ScrollingPopup("I have been murdered, you see? But I'm sooooooo curious to know with what weapon. I have nothing else to do anyway.", () -> {
                        gui.setComponent(new ScrollingPopup("I just can't figure it out. The other men *cough* errh, ghosts here might have some clues for you.", () -> {
                            gui.removeComponent();
                            paused = false;
                        }));
                    }));
                }));
            }));
        });

        hintText1 = new ScrollingPopup("Hm, I didn't see much, only that it was sort of pointy? Yea, pointy, Razor(tm) sharp.", () -> {
            gui.removeComponent();
            paused = false;
        });

        hintText2 = new ScrollingPopup("His death was certainly colorful.", () -> {
            gui.removeComponent();
            paused = false;
        });

        hintText3 = new ScrollingPopup("Some say it was mighty. Well, perhaps not for him in the end.", () -> {
            gui.removeComponent();
            paused = false;
        });

        gemText = new ScrollingPopup("You found the green gem! Go back to the main room to find more gems.", () -> {
            gui.removeComponent();
            greenGem.remove(() -> entitiesToRemove.add(greenGem));
            map.getTile("gem_pickup").removeTag("trigger");
            paused = false;
        });

        // Create puzzle
        puzzle1 = new Puzzle(
                "What's the murdering weapon?",
                // Options
                new String[]{"saw", "frying pan", "giraffe", "knife", "fork", "rifle", "pencil", "axe", "sword"},
                // Solutions
                new Solution[]{
                        new Solution("pencil", (s) -> {
                            gui.setComponent(new ScrollingPopup("Indeed! Now I remember! It was the pencil. Hah... what a coincidence. I will remove the boxes for you", () -> {
                                gui.removeComponent();
                                // Remove crates
                                map.getTiles("crate").forEach(c -> {
                                    c.setMesh(floorMesh);
                                    c.setSolid(false);
                                });
                                // Remove indicators
                                textIndicator1.remove(() -> entitiesToRemove.add(textIndicator1));
                                puzzle1Indicator.remove(() -> entitiesToRemove.add(puzzle1Indicator));
                                hintIndicator1.remove(() -> entitiesToRemove.add(hintIndicator1));
                                hintIndicator2.remove(() -> entitiesToRemove.add(hintIndicator2));
                                hintIndicator3.remove(() -> entitiesToRemove.add(hintIndicator3));
                                // Remove triggers
                                textTile1.removeTag("trigger");
                                puzzleTile1.removeTag("trigger");
                                hintTile1.removeTag("trigger");
                                hintTile2.removeTag("trigger");
                                hintTile3.removeTag("trigger");
                                // Resume the game
                                paused = false;
                            }));
                        }),
                        new Solution("frying pan", (s) -> {
                            gui.setComponent(new ScrollingPopup("Although my trusty frying pan has deflected its fair share of bullets, I'm quite sure it didn't kill me.", () -> {
                                gui.removeComponent();
                                paused = false;
                            }));
                        })
                },
                // Default solution
                new Solution("", (s) -> {
                    gui.setComponent(new ScrollingPopup("I don't think a " + s + " killed me...", () -> {
                        gui.removeComponent();
                        paused = false;
                    }));
                }),
                30
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

        map.getTiles("lantern").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.968f, 0.788f, 0.390f),
                            new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                            0.2f,
                            new PointLight.Attenuation(0f, 0f, 0f),
                            new Vector2f(0.1f, 100f)
                    )
            );
        });
        map.getTiles("lantern_crate").forEach(t -> {
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
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.105f, 0.701f, 0f),
                        new Vector3f(shrinePos.x, 3.5f, shrinePos.y),
                        0.6f,
                        new PointLight.Attenuation(0f, 0f, 0f),
                        new Vector2f(0.1f, 100f)
                )
        );

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities.addAll(Arrays.asList(
                player,
                greenGem,
                textIndicator1,
                hintIndicator1,
                hintIndicator2,
                hintIndicator3,
                puzzle1Indicator,
                ghost1,
                ghost2,
                ghost3,
                puzzleGhost
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

        if (currentPlayerTile.hasTag("trigger")) {
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }
            if (KeyBinding.isInteractPressed()) {
                if (currentPlayerTile.hasTag("murder_mystery_text_1")) {
                    gui.setComponent(text1);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("puzzle_hint_1")) {
                    gui.setComponent(hintText1);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("puzzle_hint_2")) {
                    gui.setComponent(hintText2);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("puzzle_hint_3")) {
                    gui.setComponent(hintText3);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("murder_mystery_puzzle_1")) {
                    gui.setComponent(new PuzzleGUI(puzzle1));
                    paused = true;
                }
                if (currentPlayerTile.hasTag("gem_pickup")) {
                    gui.setComponent(gemText);
                    levelController.setGemFound(LevelController.GEM.GREEN);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("ladder")) {
                    levelController.switchToMainRoom(MainRoomLevel.MAIN_ROOM_SPAWN.FROM_LEVEL_3);
                }
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
        sceneLight.cleanup();
    }
}
