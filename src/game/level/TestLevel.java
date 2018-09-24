package game.level;

import engine.Camera;
import engine.GameEngine;
import engine.GameWindow;
import engine.MouseInput;
import engine.entities.GameEntity;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.loader.PLYLoader;
import game.LevelController;
import game.Renderer;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class TestLevel extends Level {

    private GameEntity[] gameEntities;

    private Vector3f ambientLight;
    private PointLight[] pointLightList;
    private SpotLight[] spotLightList;
    private DirectionalLight directionalLight;
    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.10f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    public TestLevel(LevelController levelController) {
        super(levelController);

        renderer = new Renderer();
        camera = new Camera();
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
        Vector3i cameraInc = new Vector3i(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W) || window.isKeyPressed(GLFW_KEY_UP)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S) || window.isKeyPressed(GLFW_KEY_DOWN)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A) || window.isKeyPressed(GLFW_KEY_LEFT)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D) || window.isKeyPressed(GLFW_KEY_RIGHT)) {
            cameraInc.x = 1;
        }
        if (GameEngine.DEBUG_MODE) {
            if (window.isKeyPressed(GLFW_KEY_Z)) {
                cameraInc.y = -1;
            } else if (window.isKeyPressed(GLFW_KEY_X)) {
                cameraInc.y = 1;
            }
        }

        // Update camera position
        camera.movePosition(
                cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP
        );

        // Update camera based on mouse
        if (GameEngine.DEBUG_MODE) {
            if (mouseInput.isRightButtonPressed()) {
                Vector2f rotVec = mouseInput.getDisplVec();
                camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            }
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

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
