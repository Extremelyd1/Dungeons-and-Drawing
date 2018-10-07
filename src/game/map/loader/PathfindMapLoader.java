package game.map.loader;

import game.map.Map;
import game.map.tile.Tile;
import org.joml.Random;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * MapLoader that only returns a functional map without any meshes, for the Pathfinding Validator.
 */
public class PathfindMapLoader implements MapLoader{
    @Override
    public Map load() throws Exception {
        return generateGround(10);
    }

    private Map generateGround(int gridSize){
        Tile[][] tiles = new Tile[gridSize][gridSize];
        Random orientation = new Random(1234);

        for (int row = 0; row < gridSize; row++){
            for (int column = 0; column < gridSize; column++) {
                tiles[row][column] =
                        new Tile(new Vector2i(row, column), new Vector3f(-90, 0, 90 * orientation.nextInt(3)), null,false);
            }
        }

        return new Map(tiles);
    }
}
