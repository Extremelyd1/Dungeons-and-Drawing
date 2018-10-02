package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.Player;
import engine.lights.DirectionalLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.TempTutorialMapLoader;
import graphics.Material;
import graphics.Mesh;
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
        camera = new FreeCamera();

        // Setup player
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        player = new Player(playerMesh, map);
        player.setPosition(new Vector3f(0, 0.5f, 1)); // TODO: Coordinate is 0.5 ??

        entities = new Entity[]{player};

        // Setup lights
        sceneLight = new SceneLight();
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

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
