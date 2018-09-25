package game.level;

import engine.GameEngine;
import engine.GameWindow;
import engine.MouseInput;
import engine.camera.FreeCamera;
import engine.entities.GameEntity;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.loader.PLYLoader;
import game.LevelController;
import game.Renderer;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TestLevel extends Level {

    private GameEntity[] gameEntities;

    private Vector3f ambientLight;
    private PointLight[] pointLightList;
    private SpotLight[] spotLightList;
    private DirectionalLight directionalLight;
    private float lightAngle;

    public TestLevel(LevelController levelController) {
        super(levelController);

        renderer = new Renderer();
        camera = new FreeCamera();
    }

    @Override
    public void init(GameWindow window) throws Exception {
        renderer.init(window);

        // Load tree.ply file
        Mesh mesh = PLYLoader.loadMesh("/models/PLY/tree.ply");
        Material material = new Material(0.1f);
        mesh.setMaterial(material);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        // Set up a point light
        Vector3f lightPosition = new Vector3f(1.0f, 1.0f, -7.0f);
        float lightIntensity = 2.0f;
        PointLight pointLight = new PointLight(new Vector3f(1.0f, 0.3f, 0.0f), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        pointLightList = new PointLight[]{pointLight};

        GameEntity light;
        if (GameEngine.DEBUG_MODE) {
            Mesh mesh_light = PLYLoader.loadMesh("/models/PLY/light.ply");
            Material material_light = new Material(new Vector4f(pointLight.getColor(), 1), 0.1f);
            mesh_light.setMaterial(material_light);

            light = new GameEntity(mesh_light);
            light.setPosition(pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z);
            light.setScale(0.05f * lightIntensity);
        }

        GameEntity g = new GameEntity(mesh);
        g.setPosition(0.0f, -2.0f, -7.0f);

        if (GameEngine.DEBUG_MODE) {
            gameEntities = new GameEntity[]{g, light};
        } else {
            gameEntities = new GameEntity[]{g};
        }
    }

    @Override
    public void input(GameWindow window, MouseInput mouseInput) {
        // Move the camera based on input
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(window, mouseInput);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.update();
    }

    @Override
    public void render(GameWindow window) {
        renderer.render(
                window,
                camera,
                gameEntities,
                ambientLight,
                pointLightList,
                spotLightList,
                directionalLight
        );
    }

    @Override
    public void terminate() {
        renderer.terminate();

        for (GameEntity ge : gameEntities) {
            ge.getMesh().terminate();
        }
    }
}
