package engine.util;

import engine.loader.PLYLoader;
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
        String filePath = "/models/PLY/" + meshName;
        Mesh mesh;
        try {
            mesh = PLYLoader.loadMesh(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        loadedMeshes.put(meshName, mesh);
        return mesh;
    }

}
