package game;

import graphics.Mesh;
import engine.Camera;
import engine.GameEngine;
import engine.GameEntity;
import static org.lwjgl.glfw.GLFW.*;
import engine.GameWindow;
import engine.IGameLogic;
import engine.MouseInput;
import engine.OBJLoader;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import graphics.Material;
import graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * === NOTES ===
 * 
 *   1) Did not understand why a 2D shape did not scale past a certain point.
 *      Had to do with the fact that the z was scaled as fast in the negative
 *      direction as the scale was applied in the positive direction
 * 
 * =============
 * 
 * @author Cas Wognum (TU/e, 1012585)
 */
public class DummyGame implements IGameLogic {
    
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
    
    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera(); 
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        
        camera.setRotation(45.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void init(GameWindow window) throws Exception {
        renderer.init(window);
        
        float reflectance = 0.1f;
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/texture_wall.png");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);
        
        int sizeX = 10;
        int sizeY = 12;
        
        int[][] heightMap = new int[][] {
            new int[]{3, 3, 3, 3, 3, 3, 3, 3, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 3},
            new int[]{3, 1, 3, 3, 3, 3, 3, 3, 3, 3},
        };
        
        int size = 0;
        for (int i = 0; i < sizeX; i++) {          
            for (int j = 0; j < sizeY; j++) {
                size += heightMap[j][i];
            }
        }
        
        gameEntities = new GameEntity[size];
        
        int count = 0; 
        for (int i = 0; i < sizeX; i++) {          
            for (int j = 0; j < sizeY; j++) {
                for (int k = 0; k < heightMap[j][i]; k++) {
                    gameEntities[count] = new GameEntity(mesh);
                    gameEntities[count].setScale(0.5f);
                    gameEntities[count].setPosition(
                            -4.5f + 1.0f * i,   // x
                            -8.0f + 1.0f * k,   // y
                            -4.0f - 1.0f * j    // z
                    );
                    count++;
                }
            }
        }
        
        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        // Point Light
        Vector3f lightPosition = new Vector3f(0.5f, -6.0f, -9.0f);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(new Vector3f(1, 0, 0), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        pointLightList = new PointLight[]{pointLight};
        
        // Spot Light
        lightPosition = new Vector3f(0.5f, -6.0f, -9.0f);
        pointLight = new PointLight(new Vector3f(0.2f, 0.2f, 1), lightPosition, lightIntensity);
        att = new PointLight.Attenuation(0.0f, 0.0f, 0.01f);
        pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0f, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(180));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        spotLightList = new SpotLight[]{spotLight, new SpotLight(spotLight)};

        lightPosition = new Vector3f(-1, 0, 0);
        directionalLight = new DirectionalLight(new Vector3f(0f, 0.3f, 0f), lightPosition, lightIntensity);
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

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
            cameraInc.y * CAMERA_POS_STEP,
            cameraInc.z * CAMERA_POS_STEP);
        
        // Update camera based on mouse            
        if (GameEngine.DEBUG_MODE) {
            if (mouseInput.isRightButtonPressed()) {
                Vector2f rotVec = mouseInput.getDisplVec();
                camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            }
        }
    }

    @Override
    public void render(GameWindow window) {
        renderer.render(window, camera, gameEntities, ambientLight,
                pointLightList, spotLightList, directionalLight);
    }
    
    @Override
    public void terminate() {
        renderer.terminate();
        
        for (GameEntity ge : gameEntities) {
            ge.getMesh().terminate();
        }
    }
}
