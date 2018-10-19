package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.Player;
import engine.gui.FloatingScrollText;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import game.mobs.Snake;
import game.puzzle.Puzzle;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

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

    private ScrollingPopup text1, text2;

    private boolean paused = false;

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

//        final Tile tiles[][] = map.getTiles();
//        tiles[26][17].getMesh().setIsStatic(false);
//        tiles[27][17].getMesh().setIsStatic(false);
//        tiles[28][17].getMesh().setIsStatic(false);

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup map Entities
//        entities = new ArrayList<>();
//        LevelEditor.loadEntities("/resources/levels/mobEscape", "generatedEditorLevel_entities.lvl", entities);
//        entities.get(0).getMesh().setIsStatic(false);
//
//        IndicatorEntity trigger1Entity = new IndicatorEntity(
//                entities.get(0).getMesh(),
//                new Vector3f(entities.get(0).getPosition().x, 1f, entities.get(0).getPosition().z),
//                tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)]
//        );
//        entities.set(0, trigger1Entity);

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup puzzle
//        arcCollapsePuzzle = new Puzzle(
//                "To collapse the arc you draw:",
//                // Possible guesses
//                new String[]{"key", "cactus", "hat"},
//                // Solutions and their corresponding actions
//                new Solution[]{new Solution("key", (s) -> {
//                    gui.setComponent(new ScrollingPopup("You hear a loud bang!", () ->
//                            paused = false
//                    ));
//                    // Remove the arc and replace the entire row with boulders to block the mob path
//                    tiles[26][17] = new Tile(new Vector2i(26, 17), new Vector3f(tiles[26][5].getRotation()), AssetStore.getTileMesh("stone_1"), true);
//                    tiles[27][17] = new Tile(new Vector2i(27, 17), new Vector3f(tiles[27][5].getRotation()), AssetStore.getTileMesh("stone_2"), true);
//                    tiles[28][17] = new Tile(new Vector2i(28, 17), new Vector3f(tiles[28][5].getRotation()), AssetStore.getTileMesh("stone_1"), true);
//                    tiles[26][17].getMesh().setIsStatic(false);
//                    tiles[27][17].getMesh().setIsStatic(false);
//                    tiles[28][17].getMesh().setIsStatic(false);
//                    tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)].removeTag("trigger");
//                    entities.remove(0);     // Remove Question Mark
//                    Vector2i mobTile = new Vector2i(Math.round(mob.getPosition().x), Math.round(mob.getPosition().z));
//                    if (mobTile.y == 17 && mobTile.x >= 26 && mobTile.x <= 28) {
//                        entities.remove(mob);
//                        mob = null;
//                    } else {
//                        mob.setMap(map);              // Update mob map
//                        mob.followOnSightOnly(true);
//                    }
//                })}, new Solution("", (s) -> {
//            gui.removeComponent();
//            paused = false;
//        })
//                , 10
//        );
//        tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)].addTag("trigger");

        // Setup Player spawn
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        playerMesh.setIsStatic(false);
        player = new Player(playerMesh, map);
        player.setSpeed(3);
        player.setScale(new Vector3f(1, 2, 1));

        // Get player spawn
        Vector2i spawn = map.getTile("spawn").getPosition();
        player.setPosition(spawn.x, 0.5f, spawn.y);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(3, 11, 3)
        );

        // Load snake
        Mesh snakeMesh = PLYLoader.loadMesh("/models/entities/snake.ply");
        snakeMesh.setMaterial(new Material(0.0f));
        snakeMesh.setIsStatic(false);

        snake = new Snake(snakeMesh, map);
        snake.setScale(0.08f);
        snake.setPosition(14, 0.49f, 1);
        snake.setSpeed(2.5f);
        snake.setTarget(player);
        snake.followOnSightOnly(false);

        text1 = new ScrollingPopup("Text 1", () -> {
            gui.removeComponent();
            paused = false;
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

        entities = new ArrayList<>(Arrays.asList(
                player
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

        camera.update();
        player.update(interval);
        //sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));

        for (Entity entity : entities) {
            entity.update(interval);
        }

        // Remove entities
        Tile currentPlayerTile = map.getTile(
                Math.round(player.getPosition().x),
                Math.round(player.getPosition().z)
        );

        // Mob
        if (snake == null) {
//                if (currentPlayerTile.getPosition().x == 13 &&
//                        currentPlayerTile.getPosition().y >= 4 &&
//                        currentPlayerTile.getPosition().y <= 6) {
//                    Mesh mobMesh;
//                    try {
//                        mob = new Snake(mobMesh, map);
//                        mob.setScale(0.08f);
//                        mob.setPosition(14, 0.49f, 1);
//                        mob.setSpeed(2.5f);
//                        mob.setTarget(player);
//                        mob.followOnSightOnly(false);
//                        entities.add(mob);
//                    } catch (Exception e) {
//                    } // temporary
//                }
        } else {
            if (snake.isCollidingWithTarget()) {
                levelController.restart();
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

                }
                if (currentPlayerTile.hasTag("end")) {

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
