package game.level.validation;

import com.sun.jna.platform.win32.Winnetwk;
import engine.MouseInput;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import game.LevelController;
import game.Renderer;
import game.level.Level;
import game.map.Map;
import game.map.loader.MapFileLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class HdrTestLevel extends Level {

    private Map map;
    private Renderer renderer;
    private SceneLight sceneLight;

    private ArrayList<Entity> entities;

    public HdrTestLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();

        // Load map
        map = new MapFileLoader("/levels/hdr_test_level.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        camera = new FreeCamera();

        // Setup lights
        sceneLight = new SceneLight();
        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.2f, 0.4f, 0.8f),       // color
                new Vector3f(0.0f, 1.0f, 0.4f),       // direction
                0.2f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                false
        );
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        map.getTiles("light").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.968f, 0.788f, 0.390f),
                            new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                            0.6f,
                            new PointLight.Attenuation(0f, 0f, 0f),
                            new Vector2f(0.1f, 100f)
                    )
            );
        });
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }

        if (KeyBinding.isExposureIncreasePressed()) {
            renderer.setHdrExposure(renderer.getHdrExposure() + 0.008f);
        } else if (KeyBinding.isExposureDecreasePressed()) {
            renderer.setHdrExposure(renderer.getHdrExposure() - 0.008f);
        } else if (KeyBinding.isExposureResetPressed()) {
            renderer.setHdrExposure(1.2f);
        } else if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_H)) {
            renderer.setHdrEnable(true);
        } else if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_J)) {
            renderer.setHdrEnable(false);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

        camera.update(interval);
        sceneLight.directionalLight.setPosition(new Vector3f(camera.getPosition()));
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
        sceneLight.cleanup();
    }

}
