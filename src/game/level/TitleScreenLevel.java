package game.level;

import engine.GameWindow;
import engine.MouseInput;
import engine.camera.AnimatedCamera;
import engine.camera.Camera;
import engine.entities.DoorEntity;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.gui.GUIImage;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.util.AssetStore;
import engine.util.Utilities;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TitleScreenLevel extends Level {

    private Map map;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    /**
     * Keeps track of a list of entities that should be removed
     */
    private SceneLight sceneLight;
    private GUI gui;

    public TitleScreenLevel(LevelController levelController) {
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

        Vector3f[] cameraPoints = new Vector3f[]{
                new Vector3f(7, 11, 3),
                new Vector3f(10, 11, 2),
                new Vector3f(11, 11, 3),
                new Vector3f(13, 11, 5),
                new Vector3f(16, 11, 4),
                new Vector3f(19, 11, 3),
                new Vector3f(19, 11, 6),
                new Vector3f(19, 11, 8),
                new Vector3f(17, 11, 9),
                new Vector3f(15, 11, 10),
                new Vector3f(16, 11, 12),
                new Vector3f(18, 11, 16),
                new Vector3f(15, 11, 18),
                new Vector3f(12, 11, 20),
                new Vector3f(10, 11, 17),
                new Vector3f(8, 11, 14),
                new Vector3f(6, 11, 13),
                new Vector3f(4, 11, 12),
                new Vector3f(5, 11, 10),
                new Vector3f(6, 11, 8),
                new Vector3f(5, 11, 6),
                new Vector3f(4, 11, 4),
        };

        // Setup camera
        camera = new AnimatedCamera(cameraPoints, new Vector3f(75f, -10f, 0f));
//        camera = new FreeCamera();

        // Setup lights
        sceneLight = new SceneLight();

        map.getTiles("light").forEach(
                t -> sceneLight.pointLights.add(new PointLight(
                                new Vector3f(1f, 1f, 1f),
                                new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                                0.4f,
                                new Vector2f(1f, 100f)
                        )
                )
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
        float imageSize = (float) GameWindow.getGameWindow().getWindowWidth() / 2f;
        gui.setComponent(new GUIImage(imageSize, imageSize, Utilities.getResourcePath("textures/logo.png")));
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        // Load mesh for door
        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));

        // Define tile and door entity
        Tile puzzle1Tile = map.getTile("door1");
        DoorEntity puzzle1Door = new DoorEntity(
                doorMesh,
                new Vector3f(puzzle1Tile.getPosition().x - 0.5f, 0f, puzzle1Tile.getPosition().y),
                new Vector3f(puzzle1Tile.getRotation()),
                0.5f,
                puzzle1Tile
        );
        entities.add(puzzle1Door);

        // Load mesh for question mark
        Mesh question_mesh = AssetStore.getMesh("entities", "question_mark");
        question_mesh.setMaterial(new Material(0f));

        // Define tile and indicator entity
        Tile trigger1Tile = map.getTile("trigger1");
        IndicatorEntity trigger1Entity = new IndicatorEntity(
                question_mesh,
                new Vector3f(trigger1Tile.getPosition().x, 1f, trigger1Tile.getPosition().y),
                trigger1Tile
        );
        entities.add(trigger1Entity);
    }

    public void input(MouseInput mouseinput) {}

    @Override
    public void update(float delta, MouseInput mouseInput) {
        camera.update(delta);

        for (Entity entity : entities) {
            entity.update(delta);
        }

        gui.update(delta);
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
