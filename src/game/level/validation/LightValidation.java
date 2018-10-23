package game.level.validation;

import engine.GameWindow;
import engine.MouseInput;
import engine.animation.ModelAnimation;
import engine.camera.Camera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Player;
import engine.input.KeyBinding;
import engine.lights.*;
import engine.loader.PLYLoader;
import engine.loader.animatedModelLoader.AnimatedModelLoader;
import engine.loader.animatedModelLoader.AnimationLoader;
import game.LevelController;
import game.Renderer;
import game.level.Level;
import game.map.Map;
import game.map.loader.MapFileLoader;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class LightValidation extends Level {

    private Map map;
    private SceneLight sceneLight;
    private PointLight p1, p2;
    private SpotLight sp1, sp2;
    private Entity e1, e2;
    private boolean p1Forward, p2Forward, sp1Forward, sp2Forward;
    private Vector3f speedx, speedz, rspeedx, rspeedz;
    private ArrayList<Entity> entities;
    private Player player;

    public LightValidation(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        camera = new Camera(
                new Vector3f(-1f, 7.3f, 10.1f),
                new Vector3f(44.2f, 29.8f, 0)
        );

        entities = new ArrayList<>();

        Mesh mesh_light = null;
        try {
            mesh_light = PLYLoader.loadMesh("/models/PLY/light.ply");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Material material_light = new Material(0.1f);
        mesh_light.setMaterial(material_light);

        e1 = new Entity(mesh_light, new Vector3f(0), new Vector3f(0), 0.05f);
        e2 = new Entity(mesh_light, new Vector3f(0), new Vector3f(0), 0.05f);

        sceneLight = new SceneLight();
        setupLights1();

        speedx = new Vector3f(0.02f, 0, 0);
        speedz = new Vector3f(0, 0f, 0.02f);
        rspeedx = new Vector3f(0.008f, 0, 0);
        rspeedz = new Vector3f(0, 0, 0.008f);

        setupMap2();
        setupRenderer();
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
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_7)) {
            setupLights7();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_8)) {
            setupLights8();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_9)) {
            setupLights9();
        }

        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_A)) {
            setupAnimation();
        }

        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_M)) {
            setupMap1();
        }
        if (KeyBinding.isKeyPressed(GLFW.GLFW_KEY_N)) {
            setupMap2();
        }

        if (KeyBinding.isInteractPressed()) {
            System.out.println(camera.getPosition());
            System.out.println(camera.getRotation());
            System.out.println("end");
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

        if (p1 != null) {
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

            e1.setPosition(p1.getPosition());
        }

        if (p2 != null) {
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

            e2.setPosition(p2.getPosition());
        }

        if (sp1 != null) {
            if (sp1.getPosition().x > 7) {
                sp1Forward = false;
            } else if (sp1.getPosition().x < 0) {
                sp1Forward = true;
            }

            if (sp1Forward) {
                sp1.setPosition(new Vector3f(sp1.getPosition()).add(speedx));
                sp1.setConeDirection(new Vector3f(sp1.getConeDirection()).sub(rspeedx));
            } else {
                sp1.setPosition(new Vector3f(sp1.getPosition()).sub(speedx));
                sp1.setConeDirection(new Vector3f(sp1.getConeDirection()).add(rspeedx));
            }

            e1.setPosition(sp1.getPosition());
        }

        if (sp2 != null) {
            if (sp2.getPosition().z > 7) {
                sp2Forward = false;
            } else if (sp2.getPosition().z < 0) {
                sp2Forward = true;
            }

            if (sp2Forward) {
                sp2.setPosition(new Vector3f(sp2.getPosition()).add(speedz));
                sp2.setConeDirection(new Vector3f(sp2.getConeDirection()).sub(rspeedz));
            } else {
                sp2.setPosition(new Vector3f(sp2.getPosition()).sub(speedz));
                sp2.setConeDirection(new Vector3f(sp2.getConeDirection()).add(rspeedz));
            }

            e2.setPosition(sp2.getPosition());
        }

        if (player != null) {
            player.getAnimatedModel().getAnimator().update(interval * player.getSpeed() * 3, false);
        }

        entities.forEach(e -> {
            if (!(e instanceof Player)) {e.update(interval);}
        });

        camera.update();
    }

    private void setupRenderer() {
        renderer = new Renderer();
        try {
            renderer.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GameWindow.getGameWindow().setClearColor(2f, 2f, 2f, 0);
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

        entities.clear();
        entities.add(e1);
        entities.add(e2);

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

        entities.clear();
        entities.add(e1);
        entities.add(e2);

        sceneLight.pointLights.add(p1);
        sceneLight.pointLights.add(p2);
    }

    private void setupLights3() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        entities.clear();

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.4f));
    }

    private void setupLights4() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        entities.clear();

        sceneLight.ambientLight = new AmbientLight(new Vector3f(1f, 0f, 0f));
    }

    private void setupLights5() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(3.0f, 7.0f, 3.0f),       // position
                new Vector3f(0.2f, 0.4f, 0.8f),       // color
                new Vector3f(0.0f, 1.0f, 0.4f),       // direction
                1f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                false
        );
        sceneLight.directionalLight.setOrthoProjection(-20, 20, -20, 20, new Vector2f(1.0f, 10.0f));

        entities.clear();

        setupRenderer();
    }

    private void setupLights6() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(3.0f, 7.0f, 3.0f),       // position
                new Vector3f(0.9f, 0.4f, 0.8f),       // color
                new Vector3f(0.5f, 0.6f, 0.2f),       // direction
                0.5f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                false
        );
        sceneLight.directionalLight.setOrthoProjection(-20, 20, -20, 20, new Vector2f(1.0f, 10.0f));

        entities.clear();

        setupRenderer();
    }

    private void setupLights7() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.spotLights.add(
                new SpotLight(
                        new Vector3f(1f, 1f, 1f), // Color
                        new Vector3f(0f, 2f, 0f), // position
                        1f,
                        new Vector3f(1f, -0.3f, 1f),
                        (float) Math.cos(Math.toRadians(20)),
                        (float) Math.cos(Math.toRadians(22)),
                        new Vector2f(0.1f, 20f),
                        4096
                )
        );
        sceneLight.spotLights.add(
                new SpotLight(
                        new Vector3f(1f, 1f, 1f), // Color
                        new Vector3f(7f, 2f, 0f), // position
                        1f,
                        new Vector3f(-1f, -0.3f, 1f),
                        (float) Math.cos(Math.toRadians(20)),
                        (float) Math.cos(Math.toRadians(22)),
                        new Vector2f(0.1f, 20f),
                        4096
                )
        );

        sp1 = sceneLight.spotLights.get(0);
        sp2 = sceneLight.spotLights.get(1);

        sp1.setToDynamicOnly();
        sp2.setToDynamicOnly();

        entities.clear();
        entities.add(e1);
        entities.add(e2);

        setupRenderer();
    }

    private void setupLights8() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();

        sceneLight.spotLights.add(
                new SpotLight(
                        new Vector3f(0.5f, 1f, 0.5f), // Color
                        new Vector3f(0f, 2f, 0f), // position
                        1f,
                        new Vector3f(1f, -0.3f, 1f),
                        (float) Math.cos(Math.toRadians(20)),
                        (float) Math.cos(Math.toRadians(22)),
                        new Vector2f(0.1f, 20f),
                        4096
                )
        );
        sceneLight.spotLights.add(
                new SpotLight(
                        new Vector3f(0.2f, 0.3f, 1f), // Color
                        new Vector3f(7f, 2f, 0f), // position
                        1f,
                        new Vector3f(-1f, -0.3f, 1f),
                        (float) Math.cos(Math.toRadians(20)),
                        (float) Math.cos(Math.toRadians(22)),
                        new Vector2f(0.1f, 20f),
                        4096
                )
        );

        sp1 = sceneLight.spotLights.get(0);
        sp2 = sceneLight.spotLights.get(1);

        sp1.setToDynamicOnly();
        sp2.setToDynamicOnly();

        entities.clear();
        entities.add(e1);
        entities.add(e2);

        setupRenderer();
    }

    private void setupLights9() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();
        entities.clear();

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.4f));

        p1 = new PointLight(
                new Vector3f(1f),
                new Vector3f(0, 1.5f, 0),
                0.5f,
                new PointLight.Attenuation(0f, 0f, 0f),
                new Vector2f(0.1f, 100f)
        );
        p1.setToDynamicOnly();
        p1Forward = true;
        sceneLight.pointLights.add(p1);

        sceneLight.spotLights.add(
                new SpotLight(
                        new Vector3f(0.2f, 0.3f, 1f), // Color
                        new Vector3f(7f, 2f, 0f), // position
                        1f,
                        new Vector3f(-1f, -0.3f, 1f),
                        (float) Math.cos(Math.toRadians(20)),
                        (float) Math.cos(Math.toRadians(22)),
                        new Vector2f(0.1f, 20f),
                        4096
                )
        );
        sp2 = sceneLight.spotLights.get(0);
        sp2.setToDynamicOnly();

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(3.0f, 7.0f, 3.0f),       // position
                new Vector3f(0.8f),       // color
                new Vector3f(0.5f, 0.6f, 0.2f),       // direction
                0.2f,                                // intensity
                new Vector2f(1.0f, 10.0f),              // near-far plane
                false
        );
        sceneLight.directionalLight.setOrthoProjection(-20, 20, -20, 20, new Vector2f(1.0f, 10.0f));

        entities.add(e1);
        entities.add(e2);

        setupRenderer();
    }

    private void setupAnimation() {
        sceneLight.cleanup();
        sceneLight = new SceneLight();
        entities.clear();

        AnimatedModel playerModel = AnimatedModelLoader.loadEntity("/models/entities/player_model.dae");
        playerModel.getMesh().setMaterial(new Material(0.0f));
        playerModel.getMesh().setIsStatic(false);
        ModelAnimation playerAnimation = AnimationLoader.loadAnimation(playerModel);
        playerModel.doAnimation(playerAnimation);
        player = new Player(playerModel, map);
        player.setSpeed(4f);
        player.setScale(new Vector3f(0.40f));
        player.setPosition(new Vector3f(3f, 0.5f, 3f));
        player.setRotation(new Vector3f(0, 180, 0));

        p1 = new PointLight(
                new Vector3f(1f),
                new Vector3f(0, 1.5f, 0),
                0.5f,
                new PointLight.Attenuation(0f, 0f, 0f),
                new Vector2f(0.1f, 100f)
        );

        p2 = new PointLight(
                new Vector3f(1f),
                new Vector3f(7, 1.5f, 0),
                0.5f,
                new PointLight.Attenuation(0f, 0f, 0f),
                new Vector2f(0.1f, 100f)
        );

        sceneLight.pointLights.add(p1);
        sceneLight.pointLights.add(p2);

        entities.add(player);

        setupRenderer();
    }

    private void setupMap1() {
        try {
            map = new MapFileLoader("/levels/validation/simple_block_setup.lvl").load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupMap2() {
        try {
            map = new MapFileLoader("/levels/validation/simple_block_setup_flat.lvl").load();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    }
}
