package game.state;

import engine.*;
import engine.camera.Camera;
import engine.input.KeyBinding;
import engine.entities.Entity;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.loader.PLYLoader;
import game.Renderer;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * === NOTES ===
 * <p>
 * 1) Did not understand why a 2D shape did not scale past a certain point.
 * Had to do with the fact that the z was scaled as fast in the negative
 * direction as the scale was applied in the positive direction
 * <p>
 * =============
 *
 * @author Cas Wognum (TU/e, 1012585)
 */
public class SandboxTestLevel implements IGameLogic {

    private final Camera camera;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private Entity[] gameEntities;

    private Vector3f ambientLight;
    private PointLight[] pointLightList;
    private SpotLight[] spotLightList;
    private DirectionalLight directionalLight;
    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.10f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    public SandboxTestLevel() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

//        float reflectance = 0.1f;
//        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
//        Texture texture = new Texture("/textures/texture_wall.png");
//        Material material = new Material(texture, reflectance);
//        mesh.setMaterial(material);

        // Load test .ply file
        Mesh mesh = PLYLoader.loadMesh("/models/PLY/tree.ply");
        Material material = new Material(0.1f);
        mesh.setMaterial(material);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        // Point Light
        Vector3f lightPosition = new Vector3f(1.0f, 1.0f, -7.0f);
        float lightIntensity = 2.0f;
        PointLight pointLight = new PointLight(new Vector3f(1.0f, 0.3f, 0.0f), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        pointLightList = new PointLight[]{pointLight};

        Entity light;
        if (GameEngine.DEBUG_MODE) {
            Mesh mesh_light = PLYLoader.loadMesh("/models/PLY/light.ply");
            Material material_light = new Material(new Vector4f(pointLight.getColor(), 1), 0.1f);
            mesh_light.setMaterial(material_light);

            light = new Entity(mesh_light, new Vector3f(pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z), 0.05f * lightIntensity);
        }

//        // Spot Light
//        lightPosition = new Vector3f(0.5f, -6.0f, -9.0f);
//        pointLight = new PointLight(new Vector3f(0.2f, 0.2f, 1), lightPosition, lightIntensity);
//        att = new PointLight.Attenuation(0.0f, 0.0f, 0.01f);
//        pointLight.setAttenuation(att);
//        Vector3f coneDir = new Vector3f(0f, 0, -1);
//        float cutoff = (float) Math.cos(Math.toRadians(180));
//        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
//        spotLightList = new SpotLight[]{};
//
//        lightPosition = new Vector3f(-1, 0, 0);
//        directionalLight = new DirectionalLight(new Vector3f(0, 1, 0), lightPosition, lightIntensity);

        Entity g = new Entity(mesh, new Vector3f(0.0f, -2.0f, -7.0f));

        if (GameEngine.DEBUG_MODE) {
            gameEntities = new Entity[]{g, light};
        } else {
            gameEntities = new Entity[]{g};
        }
    }

    @Override
    public void input(MouseInput mouseInput) {
        GameWindow window = GameWindow.getGameWindow();
        cameraInc.set(0, 0, 0);
        if (KeyBinding.isForwardPressed()) {
            cameraInc.z = -1;
        } else if (KeyBinding.isBackwardPressed()) {
            cameraInc.z = 1;
        }
        if (KeyBinding.isLeftPressed()) {
            cameraInc.x = -1;
        } else if (KeyBinding.isRightPressed()) {
            cameraInc.x = 1;
        }
        if (GameEngine.DEBUG_MODE) {
            if (KeyBinding.isUpPressed()) {
                cameraInc.y = -1;
            } else if (KeyBinding.isDownPressed()) {
                cameraInc.y = 1;
            }
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.moveRelative(
                cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP
        );

        // Update camera based on mouse
        if (GameEngine.DEBUG_MODE) {
            if (mouseInput.isRightButtonPressed()) {
                Vector2f rotVec = mouseInput.getDisplVec();
                camera.getRotation().add(new Vector3f(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0));
            }
        }
    }

    @Override
    public void render() {
        renderer.render(
                camera,
                gameEntities,
                ambientLight,
                pointLightList,
                spotLightList,
                new DirectionalLight(new Vector3f(0.3f, 0.3f, 0.3f), new Vector3f(-1, 0, 0), 1f),
                null
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