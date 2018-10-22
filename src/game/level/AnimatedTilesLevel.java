package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.DoorEntity;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.animatedModel.Player;
import engine.lights.AmbientLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import engine.util.AssetStore;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;

public class AnimatedTilesLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private Entity[] entities;
    private SceneLight sceneLight;

    public AnimatedTilesLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        // Load map
        map = new MapFileLoader("/levels/level.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        //player = new Player(playerMesh, map);
        Vector2i spawnLocation = map.getTile("spawn").getPosition();
        player.setPosition(new Vector3f(spawnLocation.x, 0.5f, spawnLocation.y));
        player.setSpeed(5);

        ArrayList<Entity> entityList = new ArrayList<>();
        entityList.add(player);

        Mesh crateMesh = AssetStore.getTileMesh("crate");
        crateMesh.setMaterial(new Material(0f));
        Mesh prisonBarMesh = AssetStore.getTileMesh("prison_bars");
        prisonBarMesh.setMaterial(new Material(0f));
        map.getTiles("door").forEach((t) ->
                entityList.add(new DoorEntity(
                        prisonBarMesh,
                        new Vector3f(t.getPosition().x, 0, t.getPosition().y),
                        new Vector3f(0),
                        0.5f,
                        t,
                        entityList.size() == 1
                ))
        );
        map.getTiles("spawn").forEach((t) ->
                entityList.add(new IndicatorEntity(
                        crateMesh,
                        new Vector3f(t.getPosition().x, 2f, t.getPosition().y),
                        new Vector3f(0),
                        0.25f,
                        t
                ))
        );

        entities = new Entity[entityList.size()];
        int index = 0;
        for (Entity entity : entityList) {
            entities[index++] = entity;
        }

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(3, 11, 3)
        );

        // Setup lights
        sceneLight = new SceneLight();
        map.getTiles("point_light").forEach((t) ->
                sceneLight.pointLights.add(new PointLight(
                        new Vector3f(1f, 1f, 1f),
                        new Vector3f(t.getPosition().x, 0.5f, t.getPosition().y),
                        0.5f,
                        new Vector2f(1f, 100f)
                ))
        );
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f, 0.2f, 0.2f));
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
        for (Entity entity : entities) {
            entity.update(interval);
        }

        int xTile = Math.round(player.getPosition().x);
        int yTile = Math.round(player.getPosition().z);

        if (map.getTiles("trigger").contains(map.getTile(xTile, yTile))) {
            ((DoorEntity) entities[1]).open();
            ((DoorEntity) entities[2]).open();
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
    }

    @Override
    public void terminate() {

    }
}
