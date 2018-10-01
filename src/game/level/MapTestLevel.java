package game.level;

import engine.MouseInput;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.Player;
import engine.lights.AmbientLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.SimpleMapLoader;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MapTestLevel extends Level {

    private Entity[] gameEntities;

    private SceneLight sceneLight;

    private Map map;

    private GUI gui;

    private Player player;

    public MapTestLevel(LevelController levelController) {
        super(levelController);

        renderer = new Renderer();
        camera = new FreeCamera();
        sceneLight = new SceneLight();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        //gui = new GUI("Dungeons and Drawings!");

        map = new Map();
        map.load(new SimpleMapLoader());

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.5f, 0.5f, 0.5f));

        // Set up a point light
        Vector3f lightPosition = new Vector3f(1.0f, 3.0f, -1.0f);
        float lightIntensity = 0.5f;
        PointLight pointLight = new PointLight(new Vector3f(1.0f, 0.3f, 0.0f), lightPosition, lightIntensity, new Vector2f(1f, 100f));
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 0.5f);
        pointLight.setAttenuation(att);
        sceneLight.pointLights.add(pointLight);

        Entity light;
        Mesh mesh_light = PLYLoader.loadMesh("/models/PLY/light.ply");
        Material material_light = new Material(new Vector4f(pointLight.getColor(), 1), 0.1f);
        mesh_light.setMaterial(material_light);

        light = new Entity(mesh_light, new Vector3f(pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z), 0.05f * lightIntensity);

        Mesh cube_mesh = PLYLoader.loadMesh("/models/PLY/cube.ply");
        cube_mesh.setMaterial(new Material(0.1f));

//        player = new Player(cube_mesh, map, new Vector3f(3, 1, 3), 0.5f);

        Mesh tree = PLYLoader.loadMesh("/models/PLY/tree.ply");
        Material material = new Material(0.1f);
        tree.setMaterial(material);

        // Tree 1
        Entity g = new Entity(tree);
        g.setPosition(0.0f, 0.0f, 0.0f);
        g.setRotation(-90,0,0);

        gameEntities = new Entity[]{g, light};
    }

    @Override
    public void input(MouseInput mouseInput) {
        // Move the camera based on input
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
//        player.update(interval);
    }

    @Override
    public void render() {
        renderer.render(
                camera,
                gui,
                gameEntities,
                sceneLight,
                map
        );
    }

    @Override
    public void terminate() {
        renderer.terminate();

        for (Entity ge : gameEntities) {
            ge.getMesh().terminate();
        }
    }
}
