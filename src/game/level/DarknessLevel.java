package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.Player;
import engine.lights.*;
import engine.loader.PLYLoader;
import engine.sound.SoundBuffer;
import engine.sound.SoundListener;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import engine.util.AssetStore;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

public class DarknessLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private SceneLight sceneLight;
    private GUI gui;
    private SoundManager soundManager;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> entitiesToRemove;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    /**
     *  Light sources used for the game
     */
    private SpotLight flashLight;
    private Vector3f previousPosition;

    public DarknessLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {

        // Load map
        map = new MapFileLoader("/levels/darkness_level.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        playerMesh.setIsStatic(false);
        player = new Player(playerMesh, map);
        player.setSpeed(3f);
        player.setScale(new Vector3f(1, 2, 1));

        Vector2i spawn = map.getTile("spawn").getPosition();
        player.setPosition(spawn.x, 0.5f, spawn.y);

        // Setup camera
        camera = new FollowCamera(
                player,
                new Vector3f(75f, -10f, 0f),
                new Vector3f(3, 11, 3)
        );

        // Load mesh for question mark
        Mesh questionMarkMesh = AssetStore.getMesh("entities", "question_mark");
        questionMarkMesh.setMaterial(new Material(0f));
        questionMarkMesh.setIsStatic(false);

        // Load mesh for pencil
        Mesh pencilMesh = AssetStore.getMesh("entities", "pencil");
        pencilMesh.setMaterial(new Material(0f));
        pencilMesh.setIsStatic(false);

        // Create interactive tiles
        Tile pencilTile1 = map.getTile("puzzle_trigger");
        IndicatorEntity pencilIndicator = new IndicatorEntity(
                pencilMesh,
                new Vector3f(pencilTile1.getPosition().x, 1f, pencilTile1.getPosition().y),
                pencilTile1
        );

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

        sceneLight.ambientLight = new AmbientLight(new Vector3f(0f));

        Vector2i initialLightPosition = map.getTile("light").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.8f, 0.3f, 0.2f),
                        new Vector3f(initialLightPosition.x, 2.5f, initialLightPosition.y),
                        1f,
                        new PointLight.Attenuation(0f, 0.3f, 0f),
                        new Vector2f(0.1f, 100f)
                )
        );

        // Flashlight
        flashLight = new SpotLight(
                new Vector3f(1f, 1f, 1f),
                new Vector3f(player.getPosition().x + 1, 2f, player.getPosition().z),
                1f,
                new Vector3f(1f, -1f, 0f),
                (float) Math.cos(Math.toRadians(6.5f)),
                (float) Math.cos(Math.toRadians(11.5f)),
                new Vector2f(0.1f, 100f)
        );
        sceneLight.spotLights.add(flashLight);

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities = new ArrayList<>(Arrays.asList(
                player,
                pencilIndicator
        ));

        // Setup sound
        soundManager = new SoundManager();
        soundManager.init();

        SoundBuffer buffDarkness = new SoundBuffer("/sound/darkness.ogg");
        soundManager.addSoundBuffer(buffDarkness);
        SoundSource sourceBack = new SoundSource(false, true);
        sourceBack.setBuffer(buffDarkness.getBufferId());
        soundManager.addSoundSource("helloDarkness", sourceBack);
        soundManager.setListener(SoundListener.getSoundListener());
        sourceBack.play();

        paused = false;

    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        gui.update(interval);

        if (paused) {
            return;
        }

        previousPosition = new Vector3f(player.getPosition());

        entities.forEach(e -> e.update(interval));
        entitiesToRemove.forEach(e -> entities.remove(e));

        Tile currentPlayerTile = map.getTile(
                Math.round(player.getPosition().x),
                Math.round(player.getPosition().z)
        );

        if (flashLight != null) {
            float signX = Math.signum(player.getPosition().x - previousPosition.x);
            float signZ = Math.signum(player.getPosition().z - previousPosition.z);
            flashLight.setPosition(new Vector3f(player.getPosition().x + signX * 2,
                    3f, player.getPosition().z + signZ * 2));
            flashLight.setConeDirection(new Vector3f(signX, -1.0f, signZ));
        }

        camera.update();
        player.update(interval);
        sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));

        soundManager.updateListenerPosition(camera);
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
    }
}
