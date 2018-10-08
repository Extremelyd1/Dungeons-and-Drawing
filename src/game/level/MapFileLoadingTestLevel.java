package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.Player;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.loader.TempTutorialMapLoader;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class MapFileLoadingTestLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private Entity[] entities;
    private SceneLight sceneLight;

    public MapFileLoadingTestLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        // Load map
        map = new MapFileLoader("/levels/test.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        player = new Player(playerMesh, map);
        player.setPosition(new Vector3f(0, 0.5f, 2));
        player.setSpeed(5);

        entities = new Entity[]{player};

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(3, 11, 3)
        );

        // Setup lights
        sceneLight = new SceneLight();
        sceneLight.pointLights.add(new PointLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(6f, 1.5f, 4f),
                0.5f,
                new Vector2f(1f, 100f)
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
        camera.update();
        player.update(interval);
    }

    @Override
    public void render() {
        renderer.render(
                camera,
                null,
                entities,
                sceneLight,
                map
        );
    }

    @Override
    public void terminate() {

    }
}
