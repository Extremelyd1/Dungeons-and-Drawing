package game.level;

import engine.MouseInput;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.Player;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.loader.PLYLoader;
import game.GUI;
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

    private GUI gui;

    private Player player;

    public MapTestLevel(LevelController levelController) {
        super(levelController);

        renderer = new Renderer();
        camera = new FreeCamera();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        gui = new GUI("Dungeons and Drawings!");

        map = new Map();
        map.load(new SimpleMapLoader());

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

        Mesh cube_mesh = PLYLoader.loadMesh("/models/PLY/cube.ply");
        cube_mesh.setMaterial(new Material(0.1f));

        player = new Player(cube_mesh, map, new Vector3f(3, 1, 3), 0.5f);

        gameEntities = new Entity[]{player, light};
    }

    @Override
    public void input(MouseInput mouseInput) {
        // Move the camera based on input
        if (camera instanceof FreeCamera && mouseInput.isRightButtonPressed()) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        player.update(interval);
    }

    @Override
    public void render() {
        renderer.render(
                camera,
                gui,
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
