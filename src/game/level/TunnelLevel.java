package game.level;

import engine.MouseInput;
import engine.animation.ModelAnimation;
import engine.camera.Camera;
import engine.camera.FollowCamera;
import engine.entities.Entity;
import engine.entities.IndicatorEntity;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Player;
import engine.gui.FloatingScrollText;
import engine.gui.PuzzleGUI;
import engine.gui.ScrollingPopup;
import engine.input.KeyBinding;
import engine.lights.AmbientLight;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.loader.animatedModelLoader.AnimatedModelLoader;
import engine.loader.animatedModelLoader.AnimationLoader;
import engine.util.AssetStore;
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
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

public class TunnelLevel extends Level {
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
    private ScrollingPopup text1, puzzleText, gemText;
    /**
     * Puzzles in the level
     */
    private Puzzle ghostPuzzle;

    /**
     * Flag whether the game is paused (because of gui)
     */
    private boolean paused;

    public TunnelLevel(LevelController levelController) {super(levelController);}

     @Override
    public void init() throws Exception {
         entities = new ArrayList<>();
         // Load map
         map = new MapFileLoader("/levels/TunnelLevel.lvl").load();

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

         // Mesh for the floor
         Mesh floorMesh = AssetStore.getTileMesh("stone_floor");
         floorMesh.setMaterial(new Material(0f));

         // Mesh for the pencil
         Mesh pencilMesh = AssetStore.getMesh("entities", "pencil");
         pencilMesh.setMaterial(new Material(0f));
         pencilMesh.setIsStatic(false);

         // Mesh for question mark
         Mesh questionMarkMesh = AssetStore.getMesh("entities", "question_mark");
         questionMarkMesh.setMaterial(new Material(0f));
         questionMarkMesh.setIsStatic(false);

         // Load ghost mesh
         Mesh ghostMesh = AssetStore.getMesh("entities", "ghost");
         ghostMesh.setMaterial(new Material(0f));

         // Load gem
         Mesh yellowGemMesh = AssetStore.getMesh("entities", "gem_yellow");
         yellowGemMesh.setMaterial(new Material(0f));
         yellowGemMesh.setIsStatic(false);

         Vector2i gemPosition = map.getTile("gem").getPosition();
         IndicatorEntity yellowGem = new IndicatorEntity(
                 yellowGemMesh,
                 new Vector3f(gemPosition.x, 1.5f, gemPosition.y),
                 new Vector3f(45f, 90f, 45f),
                 null
         );

         //Indicator Entities
         Tile puzzleTile = map.getTile("puzzle_ghost");
         IndicatorEntity puzzleIndicator = new IndicatorEntity(
                 pencilMesh,
                 new Vector3f(puzzleTile.getPosition().x, 1f, puzzleTile.getPosition().y),
                 puzzleTile
         );
         Tile textTile = map.getTile("text1");
         IndicatorEntity textIndicator = new IndicatorEntity(
                 questionMarkMesh,
                 new Vector3f(textTile.getPosition().x, 1f, textTile.getPosition().y),
                 textTile
         );
         //Texts
         text1 = new ScrollingPopup("As you walk through the small tunnel, you find a large room at the end of it.", () -> {
             gui.setComponent(new ScrollingPopup("As you enter the room you see a lot of skeletons laying on the ground, " +
                     "this place looks dangerous!", () -> {
                 gui.setComponent(new ScrollingPopup("In the corner of the room you see a precious gem, maybe you should pick it up?", () -> {
                     gui.removeComponent();
                     textIndicator.remove(() -> entitiesToRemove.add(textIndicator));
                     map.getTile("text1").removeTag("trigger");
                     paused = false;
                 }));
             }));
         });

         puzzleText = new ScrollingPopup("Hello there adventurer! You seem to have found me at a very" +
                 " inconvenient time.", () -> {
             gui.setComponent(new ScrollingPopup("For you see, I am dead!", () -> {
                 gui.setComponent(new ScrollingPopup("I tried to escape, and was so close!", () -> {
                     gui.setComponent(new ScrollingPopup("So now I have resorted to playing riddles with" +
                             " the occasional adventurer that passes through!", () -> {
                         gui.setComponent(new ScrollingPopup("I have a face but no eyes, hands but no arms. What am I?", () -> {
                             gui.setComponent(new PuzzleGUI(ghostPuzzle));
                         }));
                     }));
                 }));
             }));
         });

         gemText = new ScrollingPopup("I have found a yellow gem! Maybe I can find more of these!", () -> {
             gui.removeComponent();
             yellowGem.remove(() -> entitiesToRemove.add(yellowGem));
            map.getTile("gem").removeTag("trigger");
            paused = false;
         });

         // Spawning Ghost
         Vector2i puzzleGhostPos = map.getTile("spawnghost").getPosition();
         Entity puzzleGhost = new Entity(
                 ghostMesh,
                 new Vector3f(puzzleGhostPos.x, 2f, puzzleGhostPos.y),
                 new Vector3f(0f, 0f, 0f)
         );
         Tile ghostTile = map.getTile("spawnghost");
         ghostTile.setSolid(true);

         //Puzzles
         ghostPuzzle = new Puzzle(
                 "This does nothing...",
                 new String[]{},
                 new Solution[]{
                         new Solution( "key", (s) -> { //@TODO: change to answer riddle
                             gui.setComponent(new ScrollingPopup("Well played adventurer, I will let you through!", () -> {
                                 gui.removeComponent();
                                 ghostTile.setSolid(false);
                                 entitiesToRemove.add(puzzleGhost);
                                 // Remove indicators
                                 puzzleIndicator.remove(() -> entitiesToRemove.add(puzzleIndicator));
                                 // Remove triggers
                                 puzzleTile.removeTag("trigger");
                                 // Resume the game
                                 paused = false;
                             }));
                         })
                 },
                 // Default solution
                 new Solution("", (s) -> {
                     gui.setComponent(new ScrollingPopup("Try again adventurer!", () -> {
                         gui.removeComponent();
                         paused = false;
                     }));
                 }),
                 30
         );

        // Lighting
         sceneLight = new SceneLight();
         sceneLight.ambientLight = new AmbientLight(new Vector3f(0.3f));
         sceneLight.directionalLight = new DirectionalLight(
                 new Vector3f(0.0f, 7.0f, 0.0f),       // position
                 new Vector3f(0.2f, 0.4f, 0.8f),       // color
                 new Vector3f(0.0f, 1.0f, 0.4f),       // direction
                 0.2f,                                // intensity
                 new Vector2f(1.0f, 10.0f),              // near-far plane
                 false
         );
         map.getTiles("lantern_floor").forEach(t -> {
             sceneLight.pointLights.add(
                     new PointLight(
                             new Vector3f(0.968f, 0.588f, 0.290f),
                             new Vector3f(t.getPosition().x, 1f, t.getPosition().y),
                             0.7f,
                             new PointLight.Attenuation(0f, 1f, 0f),
                             new Vector2f(1f, 100f)
                     )
             );
             Tile floorLantern = map.getTile("lantern_floor");
             floorLantern.setSolid(true);
         });
         map.getTiles("lantern_crate").forEach(t -> {
             sceneLight.pointLights.add(
                     new PointLight(
                             new Vector3f(0.768f, 0.688f, 0.290f),
                             new Vector3f(t.getPosition().x, 2.5f, t.getPosition().y),
                             0.70f,
                             new PointLight.Attenuation(0f, 1f, 0f),
                             new Vector2f(1f, 100f)
                     )
             );
         });
         map.getTiles("yellow_light").forEach( t -> {
             sceneLight.pointLights.add(
                     new PointLight(
                             new Vector3f(0.96f, 0.96f, 0.26f),
                             new Vector3f(t.getPosition().x, 2.5f, t.getPosition().y),
                             1.5f,
                             new PointLight.Attenuation(0f, 1f, 0f),
                             new Vector2f(1f, 100f)
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
                 puzzleIndicator,
                 textIndicator,
                 puzzleGhost,
                 yellowGem
         ));
    }

    @Override
    public void input(MouseInput mouseInput) {

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
                if (currentPlayerTile.hasTag("puzzle_ghost")) {
                    gui.setComponent(puzzleText);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("gem")) {
                    gui.setComponent(gemText);
                    levelController.setGemFound(LevelController.GEM.YELLOW);
                    paused = true;
                }
                if (currentPlayerTile.hasTag("ladder")) {
                    levelController.switchToMainRoom(MainRoomLevel.MAIN_ROOM_SPAWN.FROM_LEVEL_2);
                }
            }
            if (currentPlayerTile.hasTag("exit")) {
                levelController.switchToMainRoom(MainRoomLevel.MAIN_ROOM_SPAWN.FROM_LEVEL_2);
            }
            if (currentPlayerTile.hasTag("text1")) {
                gui.setComponent(text1);
                paused = true;
            }
        } else if (gui.hasComponent()) {
            gui.removeComponent();
        }

        camera.update();
        player.update(interval);
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

    }
}
