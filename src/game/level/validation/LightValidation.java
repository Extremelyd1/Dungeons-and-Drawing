package game.level.validation;

import engine.GameWindow;
import engine.MouseInput;
import engine.camera.Camera;
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

public class LightValidation extends Level {

    private Map map;
    private SceneLight sceneLight;
    private PointLight p1, p2;
    private boolean p1Forward, p2Forward;
    private Vector3f speedx, speedz;

    public LightValidation(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        camera = new Camera(
                new Vector3f(-1f, 7.3f, 10.1f),
                new Vector3f(44.2f, 29.8f, 0)
        );

        map = new MapFileLoader("/levels/validation/simple_block_setup.lvl").load();

        sceneLight = new SceneLight();
        setupLights1();

        speedx = new Vector3f(0.02f, 0, 0);
        speedz = new Vector3f(0, 0f, 0.02f);

        renderer = new Renderer();
        renderer.init();

        GameWindow.getGameWindow().setClearColor(2f, 2f, 2f, 0);
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }

        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_1)) {
            setupLights1();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_2)) {
            setupLights2();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_3)) {
            setupLights3();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_4)) {
            setupLights4();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_5)) {
            setupLights5();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_6)) {
            setupLights6();
        }

        if (KeyBinding.isInteractPressed()) {
            System.out.println(camera.getPosition());
            System.out.println(camera.getRotation());
            System.out.println("end");
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

        if (p1 != null && p2 != null) {
            if (p1.getPosition().x > 7) {
                p1Forward = false;
            } else if (p1.getPosition().x < 0) {
                p1Forward = true;
            }

            if (p1Forward) {
                p1.setPosition(new Vector3f(p1.getPosition()).add(speedx));
            } else {
                p1.setPosition(new Vector3f(p1.getPosition()).sub(speedx));
            }

            if (p2.getPosition().z > 7) {
                p2Forward = false;
            } else if (p2.getPosition().z < 0) {
                p2Forward = true;
            }

            if (p2Forward) {
                p2.setPosition(new Vector3f(p2.getPosition()).add(speedz));
            } else {
                p2.setPosition(new Vector3f(p2.getPosition()).sub(speedz));
            }
        }

        camera.update();
        if (sceneLight.directionalLight != null) {
            sceneLight.directionalLight.setPosition(new Vector3f(camera.getPosition()));
        }
    }

    private void setupLights1() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        p1 = new PointLight(
                new Vector3f(1f),
                new Vector3f(0, 1.5f, 0),
                0.5f,
                new PointLight.Attenuation(0f, 0f, 0f),
                new Vector2f(0.1f, 100f)
        );
        p1.setToDynamicOnly();
        p1Forward = true;

        p2 = new PointLight(
                new Vector3f(1f),
                new Vector3f(7, 1.5f, 0),
                0.5f,
                new PointLight.Attenuation(0f, 0f, 0f),
                new Vector2f(0.1f, 100f)
        );
        p2.setToDynamicOnly();
        p2Forward = true;

        sceneLight.pointLights.add(p1);
        sceneLight.pointLights.add(p2);
    }

    private void setupLights2() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        p1 = new PointLight(
                new Vector3f(0, 1, 0),
                new Vector3f(0, 1.5f, 0),
                0.5f,
                new PointLight.Attenuation(0f, 0f, 0f),
                new Vector2f(0.1f, 100f)
        );
        p1.setToDynamicOnly();
        p1Forward = true;

        p2 = new PointLight(
                new Vector3f(0, 0, 1),
                new Vector3f(7, 1.5f, 0),
                0.5f,
                new PointLight.Attenuation(0f, 0f, 0f),
                new Vector2f(0.1f, 100f)
        );
        p2.setToDynamicOnly();
        p2Forward = true;

        sceneLight.pointLights.add(p1);
        sceneLight.pointLights.add(p2);
    }

    private void setupLights3() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.4f));
    }

    private void setupLights4() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.ambientLight = new AmbientLight(new Vector3f(1f, 0f, 0f));
    }

    private void setupLights5() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.2f, 0.4f, 0.8f),       // color
                new Vector3f(0.0f, 1.0f, 0.4f),       // direction
                1f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                true
        );
    }

    private void setupLights6() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.9f, 0.4f, 0.8f),       // color
                new Vector3f(0.5f, 0.6f, 0.2f),       // direction
                0.5f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                true
        );
    }

    @Override
    public void render() {
        renderer.render(
                camera,
                new Entity[]{},
                sceneLight,
                map
        );
    }

    @Override
    public void terminate() {

    }
}
