package engine.util;

import engine.loader.PLYLoader;
import graphics.Material;
import graphics.Mesh;

import java.util.HashMap;
import java.util.Map;

public class AssetStore {

    // Store currently loaded meshes
    private static Map<String, Mesh> loadedMeshes = new HashMap<>();

    public static Mesh getMesh(String meshName) {
        return getMesh(meshName, false);
    }

    public static Mesh getMesh(String meshName, boolean force) {
        // If mesh is already loaded, return it
        if (!force && loadedMeshes.containsKey(meshName)) {
            return loadedMeshes.get(meshName);
        }
        // Load mesh
        String filePath = "/models/tiles/" + meshName + ".ply";
        Mesh mesh;
        try {
            mesh = PLYLoader.loadMesh(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // Hardcoded reflectance
        mesh.setMaterial(new Material(0f));
        loadedMeshes.put(meshName, mesh);
        return mesh;
    }

}
