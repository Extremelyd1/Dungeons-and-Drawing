package game.level;

import engine.MouseInput;
import engine.animation.ModelAnimation;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.camera.FreeCamera;
import engine.entities.DoorEntity;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Player;
import engine.gui.FloatingScrollText;
import engine.gui.PuzzleGUI;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.*;
import engine.loader.PLYLoader;
import engine.loader.animatedModelLoader.AnimatedModelLoader;
import engine.loader.animatedModelLoader.AnimationLoader;
import engine.sound.SoundBuffer;
import engine.sound.SoundListener;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import engine.util.AssetStore;
import engine.util.MyFile;
import game.GUI;
import game.LevelController;
import game.Renderer;
import game.map.Map;
import game.map.loader.MapFileLoader;
import game.map.tile.Tile;
import game.puzzle.Puzzle;
import game.puzzle.Solution;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class FullLevel1 extends Level {

    private Map map;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<Entity> entities;
    /**
     * Keeps track of a list of entities that should be removed
     */
    private List<Entity> entitiesToRemove;
    private SceneLight sceneLight;
    private GUI gui;
    private SoundManager soundManager;

    private Puzzle testPuzzle;

    private boolean paused = false;

    public FullLevel1(LevelController levelController) {
        super(levelController);
    }

    @Override
    public void init() throws Exception {
        entities = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();

        // Load map
        map = new MapFileLoader("/level4.lvl").load();

        // Setup rendering
        renderer = new Renderer();
        renderer.init();

        // Setup player
        AnimatedModel playerModel = AnimatedModelLoader.loadEntity("/models/entities/model.dae", "/textures/diffuse.png");
        ModelAnimation playerAnimation = AnimationLoader.loadAnimation("/models/entities/model.dae");
        playerModel.doAnimation(playerAnimation);
        player = new Player(playerModel, map);
        player.setSpeed(5);
        //player.setScale(new Vector3f(1, 2, 1));
        player.setPosition(2, 0.5f, 3);

        //Vector2i spawn = map.getTile("spawn").getPosition();
        //player.setPosition(spawn.x, 0.5f, spawn.y);

        //entities.add(player);

        // Setup camera
//        camera = new FollowCamera(
//                player,
//                new Vector3f(75f, -10f, 0f),
//                new Vector3f(3, 20, 3)
//        );
        camera = new FreeCamera();

        // Setup lights
        sceneLight = new SceneLight();

        /*sceneLight.pointLights.add(new PointLight(
                        new Vector3f(1f, 1f, 1f),
                        new Vector3f(player.getPosition().x, player.getPosition().y + 0.5f, player.getPosition().z),
                        0.7f,
                        new Vector2f(0.5f, 5f)
                ));
        sceneLight.pointLights.get(0).setToDynamicOnly();*/

        /*Vector3f lightPosition6 = new Vector3f(2.0f, 15.0f, 2.0f);
        float lightIntensity6 = 0.1f;
        PointLight.Attenuation att6 = new PointLight.Attenuation(0.0f, 0.0f, 0.5f);
        Vector3f coneDir2 = new Vector3f(-1f, -0.5f, 0);
        float cutoff2 = (float) Math.cos(Math.toRadians(6.5f));
        float outerCutOff2 = (float) Math.cos(Math.toRadians(11.5f));
        SpotLight spotLight2 = new SpotLight(new Vector3f(1.0f, 1.0f, 1.0f), lightPosition6,
                lightIntensity6, coneDir2, cutoff2, outerCutOff2, att6, new Vector2f(1.0f, 50f));
        sceneLight.spotLights.add(spotLight2);
        Vector3f coneDir = new Vector3f(sceneLight.spotLights.get(0).getPosition()).sub(new Vector3f(player.getPosition())).mul(-1);
        sceneLight.spotLights.get(0).setConeDirection(coneDir);*/

        map.getTiles("light").forEach(
                t -> sceneLight.pointLights.add(new PointLight(
                                new Vector3f(1f, 1f, 1f),
                                new Vector3f(t.getPosition().x, 3.5f, t.getPosition().y),
                                0.4f,
                                new Vector2f(1f, 100f)
                        )
                )
        );

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
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.2f));

        // Load mesh for door
        Mesh doorMesh = AssetStore.getMesh("entities", "wooden_door");
        doorMesh.setMaterial(new Material(0f));

        // Define tile and door entity
        Tile puzzle1Tile = map.getTile("door1");
        DoorEntity puzzle1Door = new DoorEntity(
                doorMesh,
                new Vector3f(puzzle1Tile.getPosition().x - 0.5f, 0f, puzzle1Tile.getPosition().y),
                new Vector3f(puzzle1Tile.getRotation()),
                0.5f,
                puzzle1Tile
        );
        entities.add(puzzle1Door);

        // Load mesh for question mark
        Mesh question_mesh = AssetStore.getMesh("entities", "question_mark");
        question_mesh.setMaterial(new Material(0f));

        // Define tile and indicator entity
        Tile trigger1Tile = map.getTile("trigger1");
        IndicatorEntity trigger1Entity = new IndicatorEntity(
                question_mesh,
                new Vector3f(trigger1Tile.getPosition().x, 1f, trigger1Tile.getPosition().y),
                trigger1Tile
        );
        entities.add(trigger1Entity);

        // Define puzzle that uses the aforementioned indicator and door
        testPuzzle = new Puzzle(
                "To open a door you draw:",
                // Possible guesses
                new String[]{"key", "cactus", "hat"},
                // Solutions and their corresponding actions
                new Solution[]{new Solution("key", () -> {
                    gui.setComponent(new ScrollingPopup("Indeed! A key opens the door", () ->
                            paused = false
                    ));
                    puzzle1Door.open();
                    trigger1Entity.remove(() -> entitiesToRemove.add(trigger1Entity));
                    trigger1Entity.getTile().removeTag("trigger");
                    // Default solution and its action
                })}, new Solution("", () -> {
            gui.removeComponent();
            paused = false;
        })
                , 20
        );

        // Sound
        soundManager = new SoundManager();
        soundManager.init();

        SoundBuffer buffBack = new SoundBuffer("/sound/impossible.ogg");
        soundManager.addSoundBuffer(buffBack);
        SoundSource sourceBack = new SoundSource(true, true);
        sourceBack.setBuffer(buffBack.getBufferId());
        soundManager.addSoundSource("music", sourceBack);
        soundManager.setListener(SoundListener.getSoundListener());
        sourceBack.play();
    }

    @Override
    public void input(MouseInput mouseInput) {
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float delta, MouseInput mouseInput) {
        if (!paused) {
            camera.update();
            player.update(delta);
            //sceneLight.pointLights.get(0).setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 0.5f, player.getPosition().z + 1.5f));
            sceneLight.directionalLight.setPosition(new Vector3f(player.getPosition()).add(new Vector3f(0.0f, 6.0f, 0.0f)));

            for (Entity entity : entities) {
                entity.update(delta);
            }

            // Remove entities
            entities.removeAll(entitiesToRemove);
            entitiesToRemove = new ArrayList<>();

            Tile currentPlayerTile = map.getTile(
                    Math.round(player.getPosition().x),
                    Math.round(player.getPosition().z)
            );

            // Check for tiles that have a trigger
            if (currentPlayerTile.hasTag("trigger")) {
                if (!gui.hasComponent()) {
                    // Show interact hint
                    gui.setComponent(new FloatingScrollText("Press 'e' to interact"));
                    // Check the exact trigger
                }
                if (KeyBinding.isInteractPressed() && currentPlayerTile.hasTag("trigger1")) {
                    // Show puzzle GUI
                    gui.setComponent(new PuzzleGUI(testPuzzle));
                    paused = true;
                }
                // If not on any trigger anymore, remove floating text
            } else if (gui.hasComponent()) {
                gui.removeComponent();
            }
        }

        gui.update(delta);

        // Sound
        soundManager.updateListenerPosition(camera);
    }

    @Override
    public void render() {
        renderer.render(
                camera,
                entities,
                sceneLight,
                player,
                map
        );
        gui.render();
    }

    @Override
    public void terminate() {
        gui.terminate();
        soundManager.cleanup();
    }
}
