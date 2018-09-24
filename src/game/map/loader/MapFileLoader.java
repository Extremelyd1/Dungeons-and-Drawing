package game.map.loader;

import engine.util.AssetStore;
import engine.util.Utilities;
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
    public Tile[][] load() throws Exception {

        List<String> lineList = Utilities.readAllLines(resourcePath);

        int index = 0;

        int width;
        int height;

        {
            String sizeLine = lineList.get(index++);
            String[] sizeArray = sizeLine.split(" ");

            width = tryParseInt(sizeArray[0]);
            height = tryParseInt(sizeArray[1]);
        }

        Tile[][] tileList = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                String tileLine = lineList.get(index++);
                // TODO: could throw exception
                tileList[x][y] = tryParseTile(tileLine, x, y);
            }
        }

        return tileList;
    }

    // TODO: need to throw exception
    private Tile tryParseTile(String input, int x, int y) {
        String[] tileProperties = input.split(" ");

        String meshName = tileProperties[0];
        int rotationIndex = tryParseInt(tileProperties[1]);
        boolean solid = tryParseInt(tileProperties[2]) == 1;

        Vector3f rotation = new Vector3f(0, 0, rotationIndex * 90);

        Vector2i position = new Vector2i(x, y);

        Mesh mesh = AssetStore.getMesh(meshName);

        return new Tile(position, rotation, mesh, solid);
    }

    private int tryParseInt(String input) throws NumberFormatException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw e;
        }
    }
}
