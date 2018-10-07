package game.map.loader;

import engine.util.AssetStore;
import engine.util.Utilities;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3f;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class MapFileLoader implements MapLoader {

    private String resourcePath;

    public MapFileLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public Map load() throws Exception {

        List<String> lineList = Utilities.readAllLines(resourcePath);

        int index = 0;

        int width;
        int height;

        {
            String sizeLine = lineList.get(index++);
            String[] sizeArray = sizeLine.split(" ");
            if (sizeArray.length != 2) {
                throw new Exception("Level file is not defined correctly");
            }

            width = tryParseInt(sizeArray[0]);
            height = tryParseInt(sizeArray[1]);
        }

        Tile[][] tileList = new Tile[width][height];
        java.util.Map<String, List<Tile>> taggedTiles = new java.util.HashMap<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                String tileLine = lineList.get(index++);

                String[] tileProperties = tileLine.split(" ");
                if (tileProperties.length != 3 && tileProperties.length != 4) {
                    throw new Exception("Level file is not defined correctly");
                }

                String meshName = tileProperties[0];
                // Skip 'air' or 'empty' tiles
                if (meshName.equals("air") || meshName.equals("empty")) {
                    continue;
                }
                int rotationIndex = tryParseInt(tileProperties[1]);
                boolean solid = tryParseInt(tileProperties[2]) == 1;

                Vector3f rotation = new Vector3f(0, rotationIndex * 90, 0);

                Vector2i position = new Vector2i(y, x);

                Mesh mesh = AssetStore.getMesh(meshName);

                Tile tile = new Tile(position, rotation, mesh, solid);
                tileList[y][x] = tile;

                if (tileProperties.length == 4) {
                    // Read tag list property
                    String[] tagList = tileProperties[3].split(":");
                    // Loop through defined tags and add tile to list in map
                    for (String tag : tagList) {
                        List<Tile> tilesWithTag = taggedTiles.get(tag);
                        // If list does not exist, make a new one
                        if (tilesWithTag == null) {
                            tilesWithTag = new ArrayList<>();
                            taggedTiles.put(tag, tilesWithTag);
                        }
                        tilesWithTag.add(tile);
                    }
                }
            }
        }

        return new Map(width, height, tileList, taggedTiles);
    }

    private int tryParseInt(String input) throws NumberFormatException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw e;
        }
    }
}
