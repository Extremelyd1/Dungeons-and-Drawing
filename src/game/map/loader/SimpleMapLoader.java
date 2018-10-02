package game.map.loader;

import engine.loader.PLYLoader;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
import org.joml.Random;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * This simple map loader is meant for testing purposes.
 * <p>
 * It has a hard coded map.
 */
public class SimpleMapLoader implements MapLoader {
    @Override
    public Tile[][] load() throws Exception {

        Mesh mesh = PLYLoader.loadMesh("/models/test_1.ply");
        Material material = new Material(
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
            null,
            0.0f
        );
        mesh.setMaterial(material);

        return generateGround(mesh,  50);
    }

    private Tile[][] generateGround(Mesh mesh, int gridSize){
        Tile[][] tiles = new Tile[gridSize][gridSize];
        Random orientation = new Random(1234);
        int s = gridSize / 2;

        for (int row = -s; row < s; row++){
            for (int column = -s; column < s; column++) {
                tiles[row + s][column + s] =
                        new Tile(new Vector2i(row, column), new Vector3f(-90, 0, 90 * orientation.nextInt(3)), mesh,true);
            }
        }

        return tiles;
    }
}
