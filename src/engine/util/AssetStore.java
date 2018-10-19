package engine.util;

import engine.animation.Animation;
import engine.animation.Animator;
import engine.animation.LinearAnimator;
import engine.animation.TrigonometricAnimator;
import engine.animation.keyframe.KeyFrame;
import engine.loader.PLYLoader;
import graphics.Material;
import graphics.Mesh;

import java.util.HashMap;
import java.util.Map;

/**
 * Asset store.
 * <p>
 * Utility class for loading and saving assets.
 */
public class AssetStore {

    /**
     * Base folder where all models are located
     */
    private static String modelBaseFolder = "/models/";

    /**
     * Map of all loaded meshes
     */
    private static Map<String, Mesh> loadedMeshes = new HashMap<>();

    /**
     * Map of all loaded animations
     */
    private static Map<String, Animation> loadedAnimations = new HashMap<>();

    /**
     * Load a mesh from a folder
     *
     * @param folder Folder name under base folder
     * @param name   Name of ply
     * @return Mesh
     */
    public static Mesh getMesh(String folder, String name) {
        return getMesh(folder + "/" + name);
    }

    /**
     * Load a mesh from a folder
     *
     * @param folder Folder name under base folder
     * @param name   Name of ply
     * @param force  Force a reload
     * @return Mesh
     */
    public static Mesh getMesh(String folder, String name, boolean force) {
        return getMesh(folder + "/" + name, force);
    }

    /**
     * Load a mesh from a path
     *
     * @param path Path to ply from base folder
     * @return Mesh
     */
    public static Mesh getMesh(String path) {
        return getMesh(path, false);
    }

    /**
     * Load a tile mesh located in the tiles/ folder
     *
     * @param name Name of tile ply
     * @return Mesh
     */
    public static Mesh getTileMesh(String name) {
        return getMesh("tiles", name);
    }

    /**
     * Load an entity mesh
     *
     * @param name Name of entity ply
     * @return Mesh
     */
    public static Mesh getEntityMesh(String name) {
        return getMesh("entities", name);
    }

    /**
     * Load a mesh from a path
     *
     * @param path  Path to ply from base folder
     * @param force Force a reload
     * @return Mesh
     */
    public static Mesh getMesh(String path, boolean force) {
        // If mesh is already loaded, return it
        if (!force && loadedMeshes.containsKey(path)) {
            return loadedMeshes.get(path);
        }

        // Load mesh
        String filePath = modelBaseFolder + path + ".ply";
        Mesh mesh;
        try {
            mesh = PLYLoader.loadMesh(filePath);
        } catch (Exception e) {
            System.err.println("Failed to load: " + filePath);
            e.printStackTrace();
            return null;
        }

        // Hardcoded reflectance
        mesh.setMaterial(new Material(0f));
        loadedMeshes.put(path, mesh);

        return mesh;
    }

    /**
     * Load a (hardcoded) animation by name
     *
     * @param name The name of the animation to load
     * @return The loaded/cached animation
     */
    public static Animator getAnimator(String name) {
        Animation animation = getAnimation(name);

        if (name.equals("door")) {
            return new LinearAnimator(animation, false, false);
        } else if (name.equals("indicatorRotation")) {
            return new LinearAnimator(animation, true, true);
        } else if (name.equals("indicatorMovement")) {
            return new TrigonometricAnimator(animation, true, true);
        } else if (name.equals("linear1sec")) {
            return new LinearAnimator(animation, false, true);
        } else if (name.equals("rotatingLock")) {

        } else if (name.equals("flyingLock")) {
            return new LinearAnimator(animation, false, true);
        }

        return null;
    }

    /**
     * Load an animation by name
     * @param name the name of the animation to load
     * @return the loaded/cached animation
     */
    public static Animation getAnimation(String name) {
        if (loadedAnimations.containsKey(name)) {
            return loadedAnimations.get(name);
        }

        Animation animation = null;

        if (name.equals("door")) {
            KeyFrame[] keyFrames = new KeyFrame[2];
            keyFrames[0] = new KeyFrame(0f, 0f);
            keyFrames[1] = new KeyFrame(3f, 90f);

            animation = new Animation(3f, keyFrames);
            loadedAnimations.put(name, animation);
        } else if (name.equals("indicatorRotation")) {
            KeyFrame[] keyFrames = new KeyFrame[2];
            keyFrames[0] = new KeyFrame(0f, 0f);
            keyFrames[1] = new KeyFrame(5f, 360f);

            animation = new Animation(5f, keyFrames);
            loadedAnimations.put(name, animation);
        } else if (name.equals("indicatorMovement")) {
            KeyFrame[] keyFrames = new KeyFrame[3];
            keyFrames[0] = new KeyFrame(0f, 0f);
            keyFrames[1] = new KeyFrame(1f, 1f);
            keyFrames[2] = new KeyFrame(2f, 0f);

            animation = new Animation(2f, keyFrames);
            loadedAnimations.put(name, animation);
        } else if (name.equals("linear1sec")) {
            KeyFrame[] keyFrames = new KeyFrame[2];
            keyFrames[0] = new KeyFrame(0f, 0f);
            keyFrames[1] = new KeyFrame(1f, 1f);

            animation = new Animation(1f, keyFrames);
            loadedAnimations.put(name, animation);
        } else if (name.equals("rotatingLock")) {

        } else if (name.equals("flyingLock")) {
            KeyFrame[] keyFrames = new KeyFrame[3];
            keyFrames[0] = new KeyFrame(0f, 0f);
            keyFrames[1] = new KeyFrame(1f, 0f);
            keyFrames[2] = new KeyFrame(6f, 5f);

            animation = new Animation(6f, keyFrames);
            loadedAnimations.put(name, animation);
        }

        return animation;
    }

}
