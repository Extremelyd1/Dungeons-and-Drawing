package game.level;

import engine.MouseInput;
import engine.animation.ModelAnimation;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Player;
import engine.gui.FloatingScrollText;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
import engine.loader.animatedModelLoader.AnimatedModelLoader;
import engine.loader.animatedModelLoader.AnimationLoader;
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

public class TreasureRoomLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private SceneLight sceneLight;
    private GUI gui;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> entitiesToRemove;

    /**
     * Texts in the level
     */
    private ScrollingPopup text1;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    public TreasureRoomLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();

        // Load map
        map = new MapFileLoader("/levels/treasure_room_level.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        AnimatedModel playerModel = AnimatedModelLoader.loadEntity("/models/entities/player_model.dae");
        playerModel.getMesh().setMaterial(new Material(0.0f));
        playerModel.getMesh().setIsStatic(false);
        ModelAnimation playerAnimation = AnimationLoader.loadAnimation(playerModel);
        playerModel.doAnimation(playerAnimation);
        player = new Player(playerModel, map);
        player.setSpeed(3f);
        player.setScale(new Vector3f(0.25f));

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

        // Create interactive tiles
        Tile textTile1 = map.getTile("text_1");
        IndicatorEntity textIndicator1 = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(textTile1.getPosition().x, 1f, textTile1.getPosition().y),
                textTile1
        );

        // Create dialogue
        text1 = new ScrollingPopup("You did it, traveller! Your drawing skills are so magnificent. Bob Ross would be so proud of you.", () -> {
            gui.setComponent(new ScrollingPopup("Now, you can take a bath in these old coins. They are worth nothing but hey, it was at least a fine adventure, right? Right?", () -> {
                gui.setComponent(new ScrollingPopup("Anyway, please don't forget to lock the doors when you leave. We don't want to spoil the treasure for the next *cough* ignorant, naive, soul.", () -> {
                    gui.setComponent(new ScrollingPopup("So, yea, this is awkward. I think I'll crash the application to exit the game, so, any final words? No? Alright, bye!", () -> {
                        this.terminate();
                        System.exit(0);
                    }));
                }));
            }));
        });

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

        map.getTiles("lantern").forEach(t -> {
            sceneLight.pointLights.add(
                    new PointLight(
                            new Vector3f(0.968f, 0.788f, 0.390f),
                            new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                            0.2f,
                            new PointLight.Attenuation(0f, 0f, 0f),
                            new Vector2f(0.1f, 100f)
                    )
            );
        });

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities.addAll(Arrays.asList(
                player,
                textIndicator1
        ));

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

        entities.forEach(e -> e.update(interval));
        entitiesToRemove.forEach(e -> entities.remove(e));

        Tile currentPlayerTile = map.getTile(
                Math.round(player.getPosition().x),
                Math.round(player.getPosition().z)
        );

        if (currentPlayerTile.hasTag("trigger")) {
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }
            if (KeyBinding.isInteractPressed()) {
                if (currentPlayerTile.hasTag("text_1")) {
                    gui.setComponent(text1);
                    paused = true;
                }
            }
        } else if (gui.hasComponent()) {
            gui.removeComponent();
        }

        camera.update();
        player.update(interval);
        sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));
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
        sceneLight.cleanup();
    }
}
