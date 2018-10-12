package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.Player;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
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
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TutorialLevel extends Level {

    private Map map;
    private Player player;
    private SimpleMob mob;
    private Renderer renderer;
    private Camera camera;
    private Entity[] entities;
    private SceneLight sceneLight;
    private GUI gui;

    private IndicatorEntity indicatorEntity;

    public TutorialLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        // Load map
        map = new MapFileLoader("/level3.lvl").load();

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

        // Setup mob
        Mesh mobMesh = PLYLoader.loadMesh("/models/PLY/cube.ply");
        mobMesh.setMaterial(new Material(0.1f));
        mobMesh.setIsStatic(false);
        mob = new SimpleMob(mobMesh, map);
        mob.setScale(0.25f);
        mob.setPosition(2, 0.5f, 3);
        //mob.setPosition(spawn.x, 0.5f, spawn.y);
        mob.setSpeed(2.5f);
        mob.setTarget(player);

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
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        Mesh questionMesh = AssetStore.getMesh("entities", "question_mark");
        questionMesh.setMaterial(new Material(0f));

        Tile triggerTile1 = map.getTile("trigger");
        indicatorEntity = new IndicatorEntity(
                questionMesh,
                new Vector3f(triggerTile1.getPosition().x, 1f, triggerTile1.getPosition().y),
                triggerTile1
        );

        entities = new Entity[]{player, mob, indicatorEntity};

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
        mob.update(interval);
        indicatorEntity.update(interval);
        sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));
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
        gui.terminate();
    }
}
