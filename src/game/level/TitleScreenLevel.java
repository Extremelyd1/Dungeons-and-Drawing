package game.level;

import engine.GameWindow;
import engine.MouseInput;
import engine.camera.AnimatedCamera;
import engine.camera.Camera;
import engine.camera.FreeCamera;
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
        map = new MapFileLoader("/levels/titlescreen.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        Vector3f[] cameraPoints = new Vector3f[]{
                new Vector3f(10, 11, 10),
                new Vector3f(15, 11, 15),
                new Vector3f(20, 11, 10),
                new Vector3f(40, 11, 20),
                new Vector3f(40, 11, 40),
                new Vector3f(40, 11, 45),
                new Vector3f(35, 11, 40),
                new Vector3f(30, 11, 40),
                new Vector3f(25, 11, 35),
                new Vector3f(20, 11, 20),
                new Vector3f(20, 11, 15),
                new Vector3f(10, 11, 15)
        };

        // Setup camera
        camera = new AnimatedCamera(cameraPoints, new Vector3f(75f, -10f, 0f));
//        camera = new FreeCamera();

        // Setup lights
        sceneLight = new SceneLight();

        map.getTiles("crate_light").forEach(
                t -> sceneLight.pointLights.add(new PointLight(
                                new Vector3f(1f, 1f, 1f),
                                new Vector3f(t.getPosition().x, 2.2f, t.getPosition().y),
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
        map.getTiles("door").forEach(t -> {
            boolean inverted = t.hasTag("inverted");
            DoorEntity door = new DoorEntity(
                    doorMesh,
                    new Vector3f(t.getPosition().x, 0f, t.getPosition().y + 0.5f),
                    new Vector3f(t.getRotation()),
                    0.5f,
                    t,
                    inverted
            );
            entities.add(door);
        });

    }

    public void input(MouseInput mouseinput) {
    }

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
