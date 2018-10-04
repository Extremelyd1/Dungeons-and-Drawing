package game.level;

import engine.MouseInput;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.Player;
import engine.lights.*;
import engine.loader.PLYLoader;
import engine.util.ColorInterpolator;
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
import sun.security.ssl.Debug;

import java.util.Random;

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

        //Set up ambient light of the scene
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f, 0.2f, 0.2f));

        //Set up a directional light
        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f,10.0f,0.0f),      // position
                new Vector3f(0.5f, 0.5f, 0.8f),     // color
                new Vector3f(0.0f, 1.0f, 0.0f),     // direction
            0.2f,                                    // intensity
                new Vector2f(1.0f, 10.0f),             // near-far plane
                2048);                              // resolution

        // Set up a point light
        Vector3f lightPosition1 = new Vector3f(1.0f, 3.0f, -1.0f);
        float lightIntensity1 = 0.0f;
        PointLight pointLight1 = new PointLight(new Vector3f(0.88f, 0.72f, 0.13f), lightPosition1, lightIntensity1, new Vector2f(1f, 10f));
        PointLight.Attenuation att1 = new PointLight.Attenuation(0.0f, 0.2f, 0.5f);
        pointLight1.setAttenuation(att1);
        sceneLight.pointLights.add(pointLight1);

        // Set up a spot light
        Vector3f lightPosition2 = new Vector3f(-5.0f, 1.0f, -5.0f);
        float lightIntensity2 = 0.3f;
        SpotLight spotLight1 = new SpotLight(
                new Vector3f(0.88f, 0.72f, 0.13f),
                lightPosition2,
                lightIntensity2,
                new Vector3f(1f, -0.1f, 1f),
                (float)Math.cos(Math.toRadians(7)),
                (float)Math.cos(Math.toRadians(25)),
                new Vector2f(1.0f, 6.5f),
                4096);
        PointLight.Attenuation att2 = new PointLight.Attenuation(0.0f, 0.2f, 0.0f);
        spotLight1.setAttenuation(att2);
        sceneLight.spotLights.add(spotLight1);

        Entity light1, light2;
        Mesh mesh_light = PLYLoader.loadMesh("/models/PLY/light.ply");
        Material material_light = new Material(new Vector4f(pointLight1.getColor(), 1), 0.1f);
        mesh_light.setMaterial(material_light);
        light1 = new Entity(mesh_light, new Vector3f(pointLight1.getPosition().x, pointLight1.getPosition().y, pointLight1.getPosition().z), 0.05f * lightIntensity1);
        light2 = new Entity(mesh_light, new Vector3f(spotLight1.getPosition().x, spotLight1.getPosition().y, spotLight1.getPosition().z), 0.05f * lightIntensity2);

        Mesh cube_mesh = PLYLoader.loadMesh("/models/PLY/cube.ply");
        cube_mesh.setMaterial(new Material(0.1f));

        Mesh tree = PLYLoader.loadMesh("/models/PLY/tree.ply");
        Material material = new Material(
                new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
                new Vector4f(0.25f, 0.25f, 0.25f, 1.0f),
                new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
                null,
                0.0f
        );
        tree.setMaterial(material);

        // Tree 1
        Entity g = new Entity(tree);
        g.setScale(0.25f);
        g.setPosition(-3.0f, 0.5f, -3.0f);
        g.setRotation(-90,0,23);

        // Tree 2
        Entity g2 = new Entity(tree);
        g2.setPosition(-2.0f, 0.0f, 2.0f);
        g2.setRotation(-90,0,0);

        gameEntities = new Entity[]{g, g2, light1, light2};

        //        player = new Player(cube_mesh, map, new Vector3f(3, 1, 3), 0.5f);
    }

    @Override
    public void input(MouseInput mouseInput) {
        // Move the camera based on input
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    private Random rand = new Random(1234);
    private ColorInterpolator flame = new ColorInterpolator();
    @Override
    public void update(float interval, MouseInput mouseInput) {
        PointLight light = sceneLight.pointLights.get(0);
        if (flame.getStatus()){
            Vector3f color =
                    new Vector3f(0.88f, 0.72f, 0.13f).add(
                            new Vector3f(0.01f, -0.05f, -0.005f).mul((rand.nextInt(2) / 1.0f)));
            flame.setInterpolation(light.getColor(), color, 14.28f / 15f);
        }
        flame.updateColor(interval * 1000f);
        light.setColor(flame.getInterpolationResult());
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
