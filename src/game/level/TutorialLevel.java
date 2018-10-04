package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.Player;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.TempTutorialMapLoader;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TutorialLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private Entity[] entities;
    private SceneLight sceneLight;

    public TutorialLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        // Load map
        map = new Map();
        map.load(new TempTutorialMapLoader());

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
                new Vector3f(6f, 2.5f, 4f),
                0.5f,
                new Vector2f(1f, 100f)
        ));
        sceneLight.pointLights.add(new PointLight(
                new Vector3f(0.2f, 0.6f, 0.7f),
                new Vector3f(2f, 3.5f, 2f),
                0.2f,
                new Vector2f(1f, 100f)
        ));
        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f,7.0f,0.0f),       // position
                new Vector3f(0.8f, 0.8f, 0.8f),     // color
                new Vector3f(0.0f, 1.0f, 0.4f),     // direction
                0.2f,                                // intensity
                new Vector2f(1.0f, 10.0f),             // near-far plane
                false);
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
        sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));
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