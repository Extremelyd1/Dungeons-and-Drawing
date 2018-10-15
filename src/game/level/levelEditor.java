package game.level;

import engine.GameWindow;
import engine.MouseInput;
import engine.camera.Camera;
import engine.camera.FreeCamera;
import engine.entities.Entity;
import engine.lights.AmbientLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.util.AssetStore;
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
import org.lwjgl.glfw.GLFWKeyCallbackI;
import sun.security.ssl.Debug;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class levelEditor extends Level implements GLFWKeyCallbackI {

    private Map map;
    private Renderer renderer;
    private final Camera camera;

    // Map related
    private int width = 50, height = 50;
    private Tile[][] tiles = new Tile[width][height];

    // Movement related
    private float movementPrecision = 1.0f;

    // Models related
    private List<Entity> entities;
    private int index = 9;
    private List<String> tileModels = new ArrayList<>(17);
    private List<String> entityModels = new ArrayList<>(2);
    private Mesh mesh;
    private Material mat;
    private Entity currentEntity;

    // Lighting related
    private SceneLight sceneLight;
    int lightNumber = 0;
    private float intensity = 1.0f;
    private boolean editingLights = false;
    private boolean editingEntity = false;
    private int entityIndex = 0;

    public levelEditor(LevelController levelController) {
        super(levelController);
        renderer = new Renderer();
        camera = new FreeCamera();
        sceneLight = new SceneLight();

        // Existing tileModels
        tileModels.add("arc");
        tileModels.add("corner_wall");
        tileModels.add("crate");
        tileModels.add("floor_pebbles");
        tileModels.add("ladder");
        tileModels.add("ladder_up");
        tileModels.add("prison_bars");
        tileModels.add("stone_1");
        tileModels.add("stone_2");
        tileModels.add("stone_floor");
        tileModels.add("three_crates_normal");
        tileModels.add("three_crates_rotated");
        tileModels.add("two_crates_normal");
        tileModels.add("two_crates_rotated");
        tileModels.add("wooden_bar");
        tileModels.add("wooden_door");
        tileModels.add("wooden_wall");

        // Existing entityModels
        entityModels.add("question_mark");
        entityModels.add("wooden_door");
    }

    @Override
    public void init() throws Exception {
        //map = new Map(tiles); //Enable this to generate a new Map
        MapFileLoader mapFileLoader = new MapFileLoader("/levels/generatedEditorLevel_tiles.lvl");
        mapFileLoader.setEditorMode(true);
        map = mapFileLoader.load();
        tiles = map.getTiles();
        width = map.getWidth();
        height = map.getHeight();


        //Set up ambient light of the scene
        sceneLight.ambientLight = new AmbientLight(new Vector3f(0.6f, 0.6f, 0.6f));

        entities = new ArrayList<>();

        mesh = AssetStore.getTileMesh(tileModels.get(index));
        mat = new Material(0.0f);
        mesh.setMaterial(mat);
        currentEntity = new Entity(mesh);
        currentEntity.setScale(0.5f);

        entities.add(currentEntity);

        // Setup rendering
        renderer.init();

        // Setup keyboard
        glfwSetKeyCallback(GameWindow.getGameWindow().getWindowHandle(), this);

        // Setup Lights
        for (int i = 0; i < 10; i++) {
            sceneLight.pointLights.add(new PointLight(
                    new Vector3f(1f, 1f, 1f),
                    new Vector3f(2, 2, 2),
                    0.0f,
                    new Vector2f(0.1f, 60f)
            ));
        }
        loadLights();
        loadEntities();
    }

    @Override
    public void input(MouseInput mouseInput) {
        // Move the camera based on input
        if (camera instanceof FreeCamera) {
            ((FreeCamera) camera).handleInput(mouseInput);
        }
    }

    @Override
    public void update(float delta, MouseInput mouseInput) {

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

    private void saveMap() {
        try {
            String fullPath;
            File file;
            Writer writer;
            // Tiles
            fullPath = System.getProperty("user.dir") + "/resources/levels/";
            file = new File(fullPath, "generatedEditorLevel_tiles.lvl");
            file.createNewFile();
            writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write(width + " " + height + "\n");
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (tiles[c][r] == null) {
                        writer.write("air 0 0\n");
                    } else {
                        String name = (tiles[c][r].getMesh()).getName();
                        float rotation = tiles[c][r].getRotation().y;
                        int rotationN = (int) rotation / 90;
                        writer.write(name + " " + rotationN + " 0 \n");
                    }
                }
            }
            writer.close();

            // Point Lights
            fullPath = System.getProperty("user.dir") + "/resources/levels/";
            file = new File(fullPath, "generatedEditorLevel_lights.lvl");
            file.createNewFile();
            writer = new OutputStreamWriter(new FileOutputStream(file));
            for (int i = 0; i < 10; i++) {
                writer.write(sceneLight.pointLights.get(i).getIntensity() + " " +
                        sceneLight.pointLights.get(i).getPosition().x + " " +
                        sceneLight.pointLights.get(i).getPosition().y + " " +
                        sceneLight.pointLights.get(i).getPosition().z + "\n");
            }
            writer.close();

            // Entities
            fullPath = System.getProperty("user.dir") + "/resources/levels/";
            file = new File(fullPath, "generatedEditorLevel_entities.lvl");
            file.createNewFile();
            writer = new OutputStreamWriter(new FileOutputStream(file));
            for (int i = 1; i < entities.size(); i++) {
                writer.write(entities.get(i).getMesh().getName() + " " +
                        entities.get(i).getPosition().x + " " +
                        entities.get(i).getPosition().y + " " +
                        entities.get(i).getPosition().z + " " +
                        entities.get(i).getRotation().y + "\n");
            }
            writer.close();

        } catch (Exception e) {
            Debug.println("Map Editor", e.toString());
        }
    }

    public void loadLights() {
        String fullPath = System.getProperty("user.dir") + "/resources/levels/";
        File file = new File(fullPath, "generatedEditorLevel_lights.lvl");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                String[] parameters = line.split(" ");
                sceneLight.pointLights.get(index).setIntensity(Float.parseFloat(parameters[0]));
                sceneLight.pointLights.get(index).setPosition(new Vector3f(
                        Float.parseFloat(parameters[1]),
                        Float.parseFloat(parameters[2]),
                        Float.parseFloat(parameters[3])
                ));
                index++;
            }
        } catch (Exception e) {
            Debug.println("Map Editor", e.toString());
        }
    }

    public void loadEntities() {
        String fullPath = System.getProperty("user.dir") + "/resources/levels/";
        File file = new File(fullPath, "generatedEditorLevel_entities.lvl");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parameters = line.split(" ");
                Mesh mesh = AssetStore.getEntityMesh(parameters[0]);
                mesh.setName(parameters[0]);
                mesh.setMaterial(new Material(0.0f));
                Entity entity = new Entity(mesh);
                entity.setPosition(new Vector3f(
                        Float.parseFloat(parameters[1]),
                        Float.parseFloat(parameters[2]),
                        Float.parseFloat(parameters[3])));
                entity.setRotation(0, Float.parseFloat(parameters[4]), 0);
                entity.setScale(0.5f);
                entities.add(entity);
            }
        } catch (Exception e) {
            Debug.println("Map Editor", e.toString());
        }
    }

    public void invoke(long window, int key, int scancode, int action, int mods) {
        //Debug.println("KEY EVENT", "key: " + key + ", action:" + action);
        if (action == GLFW_RELEASE) {
            if (key == 328) { //Numpad 8 Move model up
                float coordinateZ = currentEntity.getPosition().z;
                if (coordinateZ > 0) coordinateZ -= movementPrecision;
                else Debug.println("Map Editor", "Top Boundary");
                currentEntity.setPosition(new Vector3f(currentEntity.getPosition().x,
                        currentEntity.getPosition().y, coordinateZ));
            } else if (key == 322) { //Numpad 2 Move model down
                float coordinateZ = currentEntity.getPosition().z;
                if (coordinateZ < height - 1) coordinateZ += movementPrecision;
                else Debug.println("Map Editor", "Bottom Boundary");
                currentEntity.setPosition(new Vector3f(currentEntity.getPosition().x,
                        currentEntity.getPosition().y, coordinateZ));
            } else if (key == 324) { //Numpad 4 Move model left
                float coordinateX = currentEntity.getPosition().x;
                if (coordinateX > 0) coordinateX -= movementPrecision;
                else Debug.println("Map Editor", "Bottom Boundary");
                currentEntity.setPosition(new Vector3f(coordinateX,
                        currentEntity.getPosition().y, currentEntity.getPosition().z));
            } else if (key == 326) { //Numpad 6 Move model right
                float coordinateX = currentEntity.getPosition().x;
                if (coordinateX < width - 1) coordinateX += movementPrecision;
                else Debug.println("Map Editor", "Bottom Boundary");
                currentEntity.setPosition(new Vector3f(coordinateX,
                        currentEntity.getPosition().y, currentEntity.getPosition().z));
            } else if (key == 334) { //Numpad + Raise model
                currentEntity.setPosition(new Vector3f(currentEntity.getPosition()).add(new Vector3f(0,movementPrecision,0)));
            } else if (key == 333) { //Numpad - Lower model
                currentEntity.setPosition(new Vector3f(currentEntity.getPosition()).add(new Vector3f(0,-movementPrecision,0)));
            } else if (key == 335) { //Numpad ENTER save model into map
                if (!editingLights) {
                    if (!editingEntity) {
                        Mesh mesh = AssetStore.getTileMesh(tileModels.get(index));
                        mesh.setName(tileModels.get(index));
                        mesh.setMaterial(new Material(0.0f));
                        tiles[Math.round(currentEntity.getPosition().x)][Math.round(currentEntity.getPosition().z)] = new Tile(
                                new Vector2i(Math.round(currentEntity.getPosition().x), Math.round(currentEntity.getPosition().z)),
                                currentEntity.getRotation(),
                                mesh,
                                true);
                        renderer.resetShadowMap();
                    } else {
                        Mesh mesh = AssetStore.getEntityMesh(entityModels.get(entityIndex));
                        mesh.setName(entityModels.get(entityIndex));
                        mesh.setMaterial(new Material(0.0f));
                        Entity entity = new Entity(mesh);
                        entity.setRotation(new Vector3f(currentEntity.getRotation()));
                        entity.setPosition(new Vector3f(currentEntity.getPosition()));
                        entity.setScale(0.5f);
                        entities.add(entity);
                    }
                } else {
                    sceneLight.pointLights.get(lightNumber).setPosition(new Vector3f(currentEntity.getPosition()));
                    renderer.resetShadowMap();
                }
            } else if (key == 320) { // Numpad 0 Save Map to file
                saveMap();
            } else if (key == 329) { // Numpad 9 Change model or change light intensity
                if (!editingLights) {
                    if (!editingEntity) {
                        if (index < tileModels.size() - 1) index++;
                        else index = 0;
                        mesh = AssetStore.getTileMesh(tileModels.get(index));
                    } else {
                        if (entityIndex < entityModels.size() - 1) entityIndex++;
                        else entityIndex = 0;
                        mesh = AssetStore.getEntityMesh(entityModels.get(entityIndex));
                    }
                    mesh.setMaterial(new Material(0.0f));
                    currentEntity.setMesh(mesh);
                } else {
                    intensity += 0.1f;
                    sceneLight.pointLights.get(lightNumber).setIntensity(intensity);
                    Debug.println("Map Edition", "Light intensity: " + intensity);
                }
            } else if (key == 327) { // Numpad 7 Change model or change light intensity
                if (!editingLights) {
                    if (!editingEntity) {
                        if (index > 0) index--;
                        else index = tileModels.size() - 1;
                        mesh = AssetStore.getTileMesh(tileModels.get(index));
                    } else {
                        if (entityIndex > 0) entityIndex--;
                        else entityIndex = entityModels.size() - 1;
                        mesh = AssetStore.getEntityMesh(entityModels.get(entityIndex));
                    }
                    mesh.setMaterial(new Material(0.0f));
                    currentEntity.setMesh(mesh);
                } else {
                    if (intensity > 0) intensity -= 0.1f;
                    sceneLight.pointLights.get(lightNumber).setIntensity(intensity);
                    Debug.println("Map Edition", "Light intensity: " + intensity);
                }
            } else if (key == 323) { // Numpad 3 Rotate model
                if (!editingLights) {
                    currentEntity.setRotation(0, currentEntity.getRotation().y + 90, 0);
                }
            } else if (key == 321) { // Numpad 1 Rotate model
                if (!editingLights) {
                    currentEntity.setRotation(0, currentEntity.getRotation().y - 90, 0);
                }
            } else if (key == 261) { // Numpad Del Delete tile
                if (!editingLights) {
                    if (!editingEntity) {
                        tiles[Math.round(currentEntity.getPosition().x)][Math.round(currentEntity.getPosition().z)] = null;
                        map = new Map(tiles);
                        renderer.resetShadowMap();
                    } else {
                        int index = findClosestEntity(currentEntity.getPosition());
                        if (index != -1) {
                            entities.remove(index);
                        }
                    }
                }
            } else if (key >= 48 && key <= 57) { // Lights
                int lightNumber = key - 48;
                if (!editingLights || lightNumber != this.lightNumber) {
                    Debug.println("Map Editor", "Editing Light " + lightNumber);
                    mesh = AssetStore.getTileMesh("crate");
                    mesh.setMaterial(new Material(0.0f));
                    currentEntity.setMesh(mesh);
                    currentEntity.setScale(0.125f);
                    if (sceneLight.pointLights.get(lightNumber).getIntensity() != 0.0f) {
                        currentEntity.setPosition(sceneLight.pointLights.get(lightNumber).getPosition());
                        intensity = sceneLight.pointLights.get(lightNumber).getIntensity();
                    } else {
                        sceneLight.pointLights.get(lightNumber).setPosition(currentEntity.getPosition());
                    }
                    sceneLight.pointLights.get(lightNumber).setIntensity(intensity);
                    renderer.resetShadowMap();
                    editingLights = true;
                    this.lightNumber = lightNumber;
                    movementPrecision = 0.25f;
                } else {
                    Debug.println("Map Editor", "Stopped editing Light " + lightNumber);
                    if (!editingEntity) {
                        Mesh mesh = AssetStore.getTileMesh(tileModels.get(index));
                        mesh.setName(tileModels.get(index));
                        mesh.setMaterial(new Material(0.0f));
                        currentEntity.setMesh(mesh);
                        currentEntity.setScale(0.5f);
                        Vector3f position = new Vector3f(
                                Math.round(currentEntity.getPosition().x),
                                Math.round(currentEntity.getPosition().y),
                                Math.round(currentEntity.getPosition().z)
                        );
                        currentEntity.setPosition(position);
                        movementPrecision = 1.0f;
                    } else {
                        movementPrecision = 0.25f;

                        Mesh mesh = AssetStore.getEntityMesh(entityModels.get(entityIndex));
                        mesh.setName(entityModels.get(entityIndex));
                        mesh.setMaterial(new Material(0.0f));
                        currentEntity.setMesh(mesh);
                        currentEntity.setScale(0.5f);
                        Vector3f position = new Vector3f(
                                Math.round(currentEntity.getPosition().x),
                                Math.round(currentEntity.getPosition().y),
                                Math.round(currentEntity.getPosition().z)
                        );
                        currentEntity.setPosition(position);
                    }
                    editingLights = false;
                }
            } else if (key == 325) { // Numpad 5 Switch to entity mode
                if (!editingEntity) {
                    Debug.println("Map Editor", "Entity editing mode");
                    movementPrecision = 0.25f;
                    editingEntity = true;

                    Mesh mesh = AssetStore.getEntityMesh(entityModels.get(entityIndex));
                    mesh.setName(entityModels.get(entityIndex));
                    mesh.setMaterial(new Material(0.0f));
                    currentEntity.setMesh(mesh);
                    currentEntity.setScale(0.5f);
                    Vector3f position = new Vector3f(
                            Math.round(currentEntity.getPosition().x),
                            Math.round(currentEntity.getPosition().y),
                            Math.round(currentEntity.getPosition().z)
                    );
                    currentEntity.setPosition(position);
                } else {
                    Debug.println("Map Editor", "Tile editing mode");
                    movementPrecision = 1.0f;
                    editingEntity = false;

                    Mesh mesh = AssetStore.getTileMesh(tileModels.get(index));
                    mesh.setName(tileModels.get(index));
                    mesh.setMaterial(new Material(0.0f));
                    currentEntity.setMesh(mesh);
                    currentEntity.setScale(0.5f);
                    Vector3f position = new Vector3f(
                            Math.round(currentEntity.getPosition().x),
                            Math.round(currentEntity.getPosition().y),
                            Math.round(currentEntity.getPosition().z)
                    );
                    currentEntity.setPosition(position);
                }
            } else if (key == 332) { // Numpad 2 Show entity index
                int index = findClosestEntity(currentEntity.getPosition());
                if (index != -1) Debug.println("Map Editor", "Entity " + (index - 1));
            }
        }
    }

    private int findClosestEntity(Vector3f pos){
        int index = -1;
        float minDistance = Float.MAX_VALUE;
        float distance;
        for (int i = 1; i < entities.size(); i++) {
            distance = (new Vector3f(pos).sub(new Vector3f(entities.get(i).getPosition()))).length();
            if (distance < minDistance && distance < 2.0f) {
                minDistance = distance;
                index = i;
            }
        }
        return index;
    }
}
