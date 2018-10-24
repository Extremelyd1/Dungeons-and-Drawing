package game.level;

import engine.GameWindow;
import engine.MouseInput;
import engine.camera.AnimatedCamera;
import engine.camera.Camera;
import engine.camera.FreeCamera;
import engine.entities.DoorEntity;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.gui.FloatingScrollText;
import engine.gui.GUIImage;
import engine.gui.TitleScreenText;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.sound.SoundBuffer;
import engine.sound.SoundListener;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import engine.util.AssetStore;
import engine.util.Utilities;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TitleScreenLevel extends Level {

    private Map map;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    /**
     * Keeps track of a list of entities that should be removed
     */
    private SceneLight sceneLight;
    private GUI gui;

    private SoundManager soundManager;

    public TitleScreenLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();

        // Load map
        map = new MapFileLoader("/levels/titlescreen.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        Vector3f[] cameraPoints = new Vector3f[]{
                new Vector3f(10, 11, 10),
                new Vector3f(15, 11, 15),
                new Vector3f(20, 11, 10),
                new Vector3f(40, 11, 20),
                new Vector3f(40, 11, 40),
                new Vector3f(40, 11, 45),
                new Vector3f(35, 11, 40),
                new Vector3f(30, 11, 40),
                new Vector3f(25, 11, 35),
                new Vector3f(20, 11, 20),
                new Vector3f(20, 11, 15),
                new Vector3f(10, 11, 15)
        };

        // Setup camera
        camera = new AnimatedCamera(cameraPoints, new Vector3f(75f, -10f, 0f));

        // Setup lights
        sceneLight = new SceneLight();

        map.getTiles("crate_light").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.701f, 0.439f, 0f),
                            new Vector3f(t.getPosition().x, 2.5f, t.getPosition().y),
                            0.6f,
                            new PointLight.Attenuation(0f, 0f, 0f),
                            new Vector2f(0.1f, 100f)
                    )
            );
        });

        sceneLight.directionalLight = new DirectionalLight(
                new Vector3f(0.0f, 7.0f, 0.0f),       // position
                new Vector3f(0.8f, 0.8f, 0.8f),     // color
                new Vector3f(0.0f, 1.0f, 0.4f),     // direction
                0.2f,                                // intensity
                new Vector2f(1.0f, 10.0f),             // near-far plane
                false);

        // Setup gui
        gui = new GUI();
        gui.initialize();
        float imageSize = (float) GameWindow.getGameWindow().getWindowWidth() / 2f;
        gui.addComponent(new GUIImage(imageSize, imageSize, "/textures/logo.png"));
        gui.addComponent(new TitleScreenText("Press 'spacebar' to start..."));

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        // Load mesh for door
        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));

        // Define tile and door entity
        Tile doorTile1 = map.getTile("door1");
        DoorEntity door1 = new DoorEntity(
                doorMesh,
                new Vector3f(doorTile1.getPosition().x, 0f, doorTile1.getPosition().y - 0.5f),
                new Vector3f(doorTile1.getRotation()),
                0.5f,
                doorTile1
        );
        entities.add(door1);

        Tile doorTile2 = map.getTile("door2");
        DoorEntity door2 = new DoorEntity(
                doorMesh,
                new Vector3f(doorTile2.getPosition().x, 0f, doorTile2.getPosition().y + 0.5f),
                new Vector3f(doorTile2.getRotation()),
                0.5f,
                doorTile2
        );
        entities.add(door2);

        // Setup sound
        soundManager = new SoundManager();
        soundManager.init();

        SoundBuffer music = new SoundBuffer("/sound/impossible.ogg");
        soundManager.addSoundBuffer(music);
        SoundSource sourceMusic = new SoundSource(true, true);
        sourceMusic.setBuffer(music.getBufferId());
        soundManager.addSoundSource("titlescreen-music", sourceMusic);
        soundManager.setListener(SoundListener.getSoundListener());

        sourceMusic.play();

    }

    @Override
    public void input(MouseInput mouseinput) {
        if (KeyBinding.isExposureIncreasePressed()) {
            renderer.setHdrExposure(renderer.getHdrExposure() + 0.008f);
        } else if (KeyBinding.isExposureDecreasePressed()) {
            renderer.setHdrExposure(renderer.getHdrExposure() - 0.008f);
        } else if (KeyBinding.isExposureResetPressed()) {
            renderer.setHdrExposure(1.2f);
        }
    }

    @Override
    public void update(float delta, MouseInput mouseInput) {
        camera.update(delta);

        for (Entity entity : entities) {
            entity.update(delta);
        }

        gui.update(delta, mouseInput);
        soundManager.updateListenerPosition(camera);

        if (KeyBinding.isStartPressed()) {
            levelController.next();
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
        gui.render();
    }

    @Override
    public void terminate() {
        soundManager.terminate();
        sceneLight.cleanup();
    }
}
