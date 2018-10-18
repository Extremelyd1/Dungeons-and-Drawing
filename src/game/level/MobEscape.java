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
import engine.lights.AmbientLight;
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
import game.mobs.SimpleMob;
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

public class MobEscape extends Level {
    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    private SceneLight sceneLight;
    private GUI gui;
    private Puzzle arcCollapsePuzzle;
    private Snake mob = null;

    private boolean paused = false;

    public MobEscape(LevelController levelController) {
        super(levelController);
    }

    /**
     * Level Infos
     *
     * Tile 27/17 arc to delete	                                                    Done
     *
     * Tile 26/17, 27/17, 28/17 to replace with boulders to  block the mob path	    Done
     *
     * Tile 27/22 end of the level
     *
     * Tile 14/1 mob spawn                                                          Done
     *
     * Tile 13/5, 13/4, 13/6 mob spawn trigger                                      Done
     *
     * Tile 2/5 player spawn	                                                    Done
     */

    @Override
    public void init() throws Exception {
        // Load Map tiles
        MapFileLoader mapFileLoader = new MapFileLoader("/levels/mobEscape/generatedEditorLevel_tiles.lvl");
        map = mapFileLoader.load();
        final Tile tiles[][] = map.getTiles();
        tiles[26][17].getMesh().setIsStatic(false);
        tiles[27][17].getMesh().setIsStatic(false);
        tiles[28][17].getMesh().setIsStatic(false);

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup Map Lights
        sceneLight = new SceneLight();
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f, 0.2f, 0.2f));
        for (int i = 0; i < 10; i++) {
            sceneLight.pointLights.add(new PointLight(
                    new Vector3f(1f, 1f, 1f),
                    new Vector3f(2, 2, 2),
                    0.0f,
                    new Vector2f(0.1f, 60f)
            ));
            sceneLight.pointLights.get(i).setAttenuation(new PointLight.Attenuation(0.0f, 0.00f,0.2f));
        }
        LevelEditor.loadLights("/resources/levels/mobEscape/", "generatedEditorLevel_lights.lvl", sceneLight);

        // Setup map Entities
        entities = new ArrayList<>();
        LevelEditor.loadEntities("/resources/levels/mobEscape", "generatedEditorLevel_entities.lvl", entities);
        entities.get(0).getMesh().setIsStatic(false);

        IndicatorEntity trigger1Entity = new IndicatorEntity(
                entities.get(0).getMesh(),
                new Vector3f(entities.get(0).getPosition().x, 1f, entities.get(0).getPosition().z),
                tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)]
        );
        entities.set(0, trigger1Entity);

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup puzzle
        arcCollapsePuzzle = new Puzzle(
                "To collapse the arc you draw:",
                // Possible guesses
                new String[]{"key", "cactus", "hat"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("key", () -> {
                    gui.setComponent(new ScrollingPopup("You hear a loud bang!", () ->
                            paused = false
                    ));
                    // Remove the arc and replace the entire row with boulders to block the mob path
                    tiles[26][17] = new Tile(new Vector2i(26, 17), new Vector3f(tiles[26][5].getRotation()), AssetStore.getTileMesh("stone_1"), true);
                    tiles[27][17] = new Tile(new Vector2i(27, 17), new Vector3f(tiles[27][5].getRotation()), AssetStore.getTileMesh("stone_2"), true);
                    tiles[28][17] = new Tile(new Vector2i(28, 17), new Vector3f(tiles[28][5].getRotation()), AssetStore.getTileMesh("stone_1"), true);
                    tiles[26][17].getMesh().setIsStatic(false);
                    tiles[27][17].getMesh().setIsStatic(false);
                    tiles[28][17].getMesh().setIsStatic(false);
                    tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)].removeTag("trigger");
                    entities.remove(0);     // Remove Question Mark
                    Vector2i mobTile = new Vector2i(Math.round(mob.getPosition().x), Math.round(mob.getPosition().z));
                    if (mobTile.y == 17 && mobTile.x >= 26 && mobTile.x <= 28) {
                        entities.remove(mob);
                        mob = null;
                    } else {
                        mob.setMap(map);              // Update mob map
                        mob.followOnSightOnly(true);
                    }
                })}, new Solution("", () -> {
            gui.removeComponent();
            paused = false;
        })
                , 10
        );
        tiles[Math.round(entities.get(0).getPosition().x)][Math.round(entities.get(0).getPosition().z)].addTag("trigger");

        // Setup Player spawn
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        playerMesh.setIsStatic(false);
        player = new Player(playerMesh, map);
        player.setSpeed(5);
        player.setScale(new Vector3f(1, 2, 1));
        player.setPosition(2, 0.5f, 5);
        entities.add(player);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(3, 11, 3)
        );

        // DEBUG
        Debug.println("MobEscape", "Mob Escape level loaded");
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
            if (mob == null) {
                if (currentPlayerTile.getPosition().x == 13 &&
                    currentPlayerTile.getPosition().y >= 4 &&
                    currentPlayerTile.getPosition().y <= 6) {
                    Mesh mobMesh;
                    try {
                        mobMesh = PLYLoader.loadMesh("/models/entities/snake.ply");
                        mobMesh.setMaterial(new Material(0.0f));
                        mobMesh.setIsStatic(false);
                        mob = new Snake(mobMesh, map);
                        mob.setScale(0.08f);
                        mob.setPosition(14, 0.49f, 1);
                        mob.setSpeed(2.5f);
                        mob.setTarget(player);
                        mob.followOnSightOnly(false);
                        entities.add(mob);
                        Debug.println("Mob Escape", "mob spawned");
                    } catch (Exception e) {
                    } // temporary
                }
            } else {
                if (mob.isCollidingWithTarget()) {
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
                if (KeyBinding.isInteractPressed() && currentPlayerTile.hasTag("trigger")) {
                    // Show puzzle GUI
                    gui.setComponent(new PuzzleGUI(arcCollapsePuzzle));
                    paused = true;
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
        mob = null;
        camera = null;
        //gui.terminate(); DO NOT TERMINATE
    }
}
