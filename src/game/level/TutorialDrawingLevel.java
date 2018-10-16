package game.level;

import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.Player;
import engine.gui.FloatingScrollText;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.PLYLoader;
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

public class TutorialDrawingLevel extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private SceneLight sceneLight;
    private GUI gui;

    private ArrayList<Entity> entities;
    private ArrayList<Entity> entitiesToRemove;

    private ScrollingPopup text1;

    private boolean paused;

    public TutorialDrawingLevel(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        // Load map
        map = new MapFileLoader("/levels/tutorial_drawing_level.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        Mesh playerMesh = PLYLoader.loadMesh("/models/basic/basic_cylinder_two_colors_1.ply");
        playerMesh.setMaterial(new Material(0.5f));
        playerMesh.setIsStatic(false);
        player = new Player(playerMesh, map);
        player.setSpeed(4);
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

        // Create interactive tiles
        Tile tutorialTextTile = map.getTile("tutorial_text_1");
        IndicatorEntity tutorialText1Indicator = new IndicatorEntity(
                questionMarkMesh,
                new Vector3f(tutorialTextTile.getPosition().x, 1f, tutorialTextTile.getPosition().y),
                tutorialTextTile
        );

        // Create dialogue
        text1 = new ScrollingPopup("Welcome, traveller, I see you came from far?", () -> {
            gui.setComponent(new ScrollingPopup("Who I am you ask? Oh, don't you worry. I'm the ominous voice", () -> {
                gui.setComponent(new ScrollingPopup(
                        "You seek the treasure of the ancient dwarfs? Hahahaha, countless men like you tried it.", () -> {
                    gui.setComponent(new ScrollingPopup("We'll see how you're creativity holds up... Good luck traveller. Try to find the door", () -> {
                        gui.removeComponent();
                        tutorialText1Indicator.remove(() -> entitiesToRemove.add(tutorialText1Indicator));
                        tutorialTextTile.removeTag("tutorial_text_1");
                        paused = false;
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

        Vector2i moonLightPos = map.getTile("moon_light").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.2f, 0.4f, 0.8f),
                        new Vector3f(moonLightPos.x, 14f, moonLightPos.y),
                        0.4f,
                        new Vector2f(1f, 100f)
                )
        );

        Vector2i lanternCratePos = map.getTile("crate_lantern").getPosition();
        sceneLight.pointLights.add(
                new PointLight(
                        new Vector3f(0.8f, 0.2f, 0.2f),
                        new Vector3f(lanternCratePos.x, 3.5f, lanternCratePos.y),
                        0.6f,
                        new PointLight.Attenuation(0f, 1f, 1.1f),
                        new Vector2f(1f, 100f)
                )
        );

        // Setup gui
        gui = new GUI();
        gui.initialize();

        // Setup entities
        entitiesToRemove = new ArrayList<>();
        entities = new ArrayList<>(Arrays.asList(
                player,
                tutorialText1Indicator
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

        if (currentPlayerTile.hasTag("tutorial_text_1")) {
            if (!gui.hasComponent()) {
                gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
            }
            if (KeyBinding.isInteractPressed()) {
                gui.setComponent(text1);
                paused = true;
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
        gui.terminate();
    }
}
