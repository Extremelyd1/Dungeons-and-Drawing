package engine.util;

import engine.loader.PLYLoader;
import graphics.Material;
import graphics.Mesh;

import java.util.HashMap;
import java.util.Map;

/**
 * Asset store.
 * <p>
 * Utility class for loading and saving models.
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
            e.printStackTrace();
            return null;
        }

        // Hardcoded reflectance
        mesh.setMaterial(new Material(0f));
        loadedMeshes.put(path, mesh);

        return mesh;
    }
}
