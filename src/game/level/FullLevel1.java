package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.DoorEntity;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.Player;
import engine.gui.FloatingText;
import engine.gui.Popup;
import engine.gui.PuzzleGUI;
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
import game.action.Action;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import game.mobs.SimpleMob;
import game.puzzle.Puzzle;
import game.puzzle.Solution;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class FullLevel1 extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    private SceneLight sceneLight;
    private GUI gui;

    private Puzzle testPuzzle;

    private boolean paused = false;

    public FullLevel1(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();

        // Load map
        map = new MapFileLoader("/level4.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        playerMesh.setIsStatic(false);
        player = new Player(playerMesh, map);
        player.setSpeed(5);
        player.setScale(new Vector3f(1, 2, 1));
        player.setPosition(2, 0.5f, 3);

        //Vector2i spawn = map.getTile("spawn").getPosition();
        //player.setPosition(spawn.x, 0.5f, spawn.y);

        entities.add(player);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(3, 11, 3)
        );
//        camera = new FreeCamera();

        // Setup lights
        sceneLight = new SceneLight();

        map.getTiles("light").forEach(
                t -> {
                    System.out.println("start " + sceneLight.pointLights.size());
                    if (sceneLight.pointLights.size() < 3) {
                        sceneLight.pointLights.add(new PointLight(
                                        new Vector3f(1f, 1f, 1f),
                                        new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                                        0.4f,
                                        new Vector2f(1f, 100f)
                                )
                        );
                    }
                    System.out.println("end " + sceneLight.pointLights.size());
                }
        );

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.8f, 0.8f, 0.8f),     // color
                new Vector3f(0.0f, 1.0f, 0.4f),     // direction
                0.2f,                                // intensity
                new Vector2f(1.0f, 10.0f),             // near-far plane
                false);

        // Setup gui
        gui = new GUI();
        gui.initialize();
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));
        map.getTiles("door").forEach((t) ->
                entities.add(new DoorEntity(
                        doorMesh,
                        new Vector3f(t.getPosition().x - 0.5f, 0f, t.getPosition().y),
                        new Vector3f(t.getRotation()),
                        0.5f,
                        t
                ))
        );

        Mesh question_mesh = AssetStore.getMesh("entities", "question_mark");
        question_mesh.setMaterial(new Material(0f));
        map.getTiles("trigger").forEach((t) ->
                entities.add(new IndicatorEntity(
                        question_mesh,
                        new Vector3f(t.getPosition().x, 1f, t.getPosition().y),
                        t
                ))
        );

        testPuzzle = new Puzzle(
                "To open a door you draw:",
                    new String[] {"key", "cactus", "hat"},
                    new Solution[]{new Solution("key", () -> {
                        gui.setComponent(new Popup("Indeed! A key opens the door", () ->
                                paused = false
                        ));
                        ((DoorEntity) entities.get(1)).open();
                        ((IndicatorEntity) entities.remove(2)).getTile().removeTag("trigger");
                    })}, new Solution("", () -> {
                        gui.removeComponent();
                        paused = false;
                    })
                    , 20
        );
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float delta, MouseInput mouseInput) {
        if (!paused) {
            camera.update();
            player.update(delta);
            sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));

            for (Entity entity : entities) {
                entity.update(delta);
            }

            Tile currentPlayerTile = map.getTile(
                    Math.round(player.getPosition().x),
                    Math.round(player.getPosition().z)
            );

            if (currentPlayerTile.hasTag("trigger")) {
                gui.setComponent(new FloatingText("Press 'e' to interact", () -> gui.removeComponent()));
                if (KeyBinding.isInteractPressed() && currentPlayerTile.hasTag("puzzle1")) {
                    gui.setComponent(new PuzzleGUI(testPuzzle));
                    paused = true;
                }
            } else if (gui.hasComponent()) {
                gui.removeComponent();
            }
        }

        gui.update();
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
        gui.terminate();
    }
}
