package game.level;

import engine.MouseInput;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.loader.PLYLoader;
import pathfinding.A_star;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.SimpleMapLoader;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MapTestLevel extends Level {

    private Entity[] gameEntities;

    private Vector3f ambientLight;
    private PointLight[] pointLightList;

    private Map map;

    public MapTestLevel(LevelController levelController) {
        super(levelController);

        renderer = new Renderer();
        camera = new FreeCamera();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        map = new Map();
        map.load(new SimpleMapLoader());
        A_star alg = new A_star();
        alg.computePath(map.getTile(0,3), map.getTile(3, 3), map);

        ambientLight = new Vector3f(0.6f, 0.6f, 0.6f);

        // Set up a point light
        Vector3f lightPosition = new Vector3f(1.0f, 1.0f, -7.0f);
        float lightIntensity = 2.0f;
        PointLight pointLight = new PointLight(new Vector3f(1.0f, 0.3f, 0.0f), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        pointLightList = new PointLight[]{pointLight};

        Entity light;
        Mesh mesh_light = PLYLoader.loadMesh("/models/PLY/light.ply");
        Material material_light = new Material(new Vector4f(pointLight.getColor(), 1), 0.1f);
        mesh_light.setMaterial(material_light);

        light = new Entity(mesh_light, new Vector3f(pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z), 0.05f * lightIntensity);

        gameEntities = new Entity[]{light};
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

    }

    @Override
    public void render() {
        renderer.render(
                camera,
                gameEntities,
                ambientLight,
                pointLightList,
                new SpotLight[]{},
                null,
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
