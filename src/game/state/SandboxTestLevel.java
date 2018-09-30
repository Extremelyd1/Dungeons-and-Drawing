package game.state;

import engine.*;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.loader.OBJLoader;
import engine.loader.PLYLoader;
import game.Renderer;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import sun.security.ssl.Debug;

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
    private GameEntity[] gameEntities;

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

        camera.setRotation(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void init(GameWindow window) throws Exception {
        renderer.init(window);

        ambientLight = new Vector3f(0.02f, 0.02f, 0.02f);

        // Spot Light 1
        Vector3f lightPosition5 = new Vector3f(15.5f, 0.0f, -10.0f);
        float lightIntensity5 = 1.0f;
        PointLight.Attenuation att5 = new PointLight.Attenuation(0.0f, 0.0f, 0.1f);
        Vector3f coneDir = new Vector3f(-1f, -0.2f, 0);
        float cutoff = (float) Math.cos(Math.toRadians(6.5f));
        float outerCutOff = (float) Math.cos(Math.toRadians(11.5f));
        SpotLight spotLight = new SpotLight(new Vector3f(1.0f, 1.0f, 1.0f), lightPosition5,
                lightIntensity5, coneDir, cutoff, outerCutOff, att5, new Vector2f(1.0f, 200.0f));
        // Spot Light 2
        Vector3f lightPosition6 = new Vector3f(15.5f, 0.0f, -10.0f);
        float lightIntensity6 = 0.1f;
        PointLight.Attenuation att6 = new PointLight.Attenuation(0.0f, 0.0f, 0.5f);
        Vector3f coneDir2 = new Vector3f(-1f, -0.2f, 0);
        float cutoff2 = (float) Math.cos(Math.toRadians(6.5f));
        float outerCutOff2 = (float) Math.cos(Math.toRadians(11.5f));
        SpotLight spotLight2 = new SpotLight(new Vector3f(1.0f, 1.0f, 1.0f), lightPosition6,
                lightIntensity6, coneDir2, cutoff2, outerCutOff2, att6, new Vector2f(1.0f, 200.0f));
        // Add Spot Lights
        spotLightList = new SpotLight[]{spotLight, spotLight2};

        // Point Light 1
        Vector3f lightPosition = new Vector3f(0.0f, 2.0f, 7.0f);
        float lightIntensity = 0.7f;
        PointLight pointLight = new PointLight(new Vector3f(1.0f, 1.0f, 1.0f), lightPosition, lightIntensity, new Vector2f(1.0f, 200.0f));
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 0.5f);
        pointLight.setAttenuation(att);
        // Point Light 2
        Vector3f lightPosition2 = new Vector3f(7.0f, 2.0f, 0.0f);
        float lightIntensity2 = 0.7f;
        PointLight pointLight2 = new PointLight(new Vector3f(1.0f, 1.0f, 1.0f), lightPosition2, lightIntensity2, new Vector2f(1.0f, 200.0f));
        PointLight.Attenuation att2 = new PointLight.Attenuation(0.0f, 0.0f, 0.5f);
        pointLight.setAttenuation(att2);
        // Point Light 3
        Vector3f lightPosition3 = new Vector3f(-5.0f, 2.0f, 13.0f);
        float lightIntensity3 = 0.7f;
        PointLight pointLight3 = new PointLight(new Vector3f(1.0f, 1.0f, 1.0f), lightPosition3, lightIntensity3, new Vector2f(1.0f, 200.0f));
        PointLight.Attenuation att3 = new PointLight.Attenuation(0.0f, 0.0f, 0.5f);
        pointLight.setAttenuation(att3);
        // Add Point Lights
        pointLightList = new PointLight[]{pointLight, pointLight2, pointLight3};

        GameEntity light, light2, light3, light4, light5;
        if (GameEngine.DEBUG_MODE) {
            Mesh mesh_light = PLYLoader.loadMesh("/models/PLY/light.ply");
            Material material_light = new Material(new Vector4f(pointLight.getColor(), 1), 0.1f);
            mesh_light.setMaterial(material_light);

            light = new GameEntity(mesh_light);
            light.setPosition(pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z);
            light.setScale(new Vector3f(0.05f,0.05f,0.05f));

            light2 = new GameEntity(mesh_light);
            light2.setPosition(pointLight2.getPosition().x, pointLight2.getPosition().y, pointLight2.getPosition().z);
            light2.setScale(new Vector3f(0.05f,0.05f,0.05f));

            light3 = new GameEntity(mesh_light);
            light3.setPosition(pointLight3.getPosition().x, pointLight3.getPosition().y, pointLight3.getPosition().z);
            light3.setScale(new Vector3f(0.05f,0.05f,0.05f));

            light4 = new GameEntity(mesh_light);
            light4.setPosition(spotLight.getPosition().x, spotLight.getPosition().y, spotLight.getPosition().z);
            light4.setScale(new Vector3f(0.05f,0.05f,0.05f));

            light5 = new GameEntity(mesh_light);
            light5.setPosition(spotLight2.getPosition().x, spotLight2.getPosition().y, spotLight2.getPosition().z);
            light5.setScale(new Vector3f(0.05f,0.05f,0.05f));
        }
        Mesh cube = PLYLoader.loadMesh("/models/PLY/cube.ply");
        Material cubeMat = new Material(0.1f);
        cube.setMaterial(cubeMat);
        Mesh tree = PLYLoader.loadMesh("/models/PLY/tree.ply");
        Material material = new Material(0.1f);
        tree.setMaterial(material);

        // Tree 1
        GameEntity g = new GameEntity(tree);
        g.setPosition(0.0f, -2.0f, 0.0f);
        g.setRotation(-90,0,0);
        // Tree 2
        GameEntity g6 = new GameEntity(tree);
        g6.setPosition(-5.0f, -2.0f, 7.0f);
        g6.setRotation(-90,0,0);
        // Tree 3
        GameEntity g7 = new GameEntity(tree);
        g7.setPosition(7.5f, -2.2f, -10.0f);
        g7.setRotation(-90,0,0);
        g7.setScale(0.2f);
        // Tree 4
        GameEntity g8 = new GameEntity(tree);
        g8.setPosition(12.5f, -2.2f, 10.0f);
        g8.setRotation(-90,0,0);
        g8.setScale(0.4f);

        // Floor
        GameEntity g2 = new GameEntity(cube);
        g2.setPosition(0.0f, -3.15f, 0.0f);
        g2.setScale(new Vector3f(25f,1f,25f));

        // Wall 1
        GameEntity g3 = new GameEntity(cube);
        g3.setPosition(-2.0f, 1.0f, 9.0f);
        g3.setScale(new Vector3f(1f,3.2f,6f));
        // Wall 2
        GameEntity g4 = new GameEntity(cube);
        g4.setPosition(2.9f, 1.0f, -8.0f);
        g4.setScale(new Vector3f(1f,3.2f,6f));
        // Wall 3
        GameEntity g5 = new GameEntity(cube);
        g5.setPosition(-8.0f, 1.0f, 0.0f);
        g5.setScale(new Vector3f(1f,3.2f,6f));

        if (GameEngine.DEBUG_MODE) {
            gameEntities = new GameEntity[]{g, g2, g3, g4, g5, g6, g7, g8, light, light2, light3, light4, light5};
        } else {
            gameEntities = new GameEntity[]{g, g2, g3, g4, g5, g6, g7};
        }

    }

    @Override
    public void input(GameWindow window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
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
    }

    private float move;
    @Override
    public void update(float interval, MouseInput mouseInput) {
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

        GameEntity gameEntity = gameEntities[9];
        PointLight pointLight = pointLightList[1];

        move += 0.01f;
        if (move > 99999) move = 0;

        Vector3f pos = new Vector3f(7.0f + 2 * (float)Math.sin(move), 2.0f, 2.0f + 7 * (float)Math.sin(-move));
        gameEntity.setPosition(pos);
        pointLight.setPosition(pos);

        GameEntity gameEntity2 = gameEntities[9];
        SpotLight spotLight = spotLightList[1];

        Vector3f pos2 = new Vector3f(12.5f + 8 * (float)Math.sin(move), 2.0f, 10.0f + 8 * (float)Math.cos(move));
        gameEntity2.setPosition(pos2);
        spotLight.setPosition(pos2);

        Vector3f coneDir = new Vector3f(pos2).sub(new Vector3f(12.5f, -2.2f, 10.0f)).mul(-1);
        spotLight.setConeDirection(coneDir);

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
