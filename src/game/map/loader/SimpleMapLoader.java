package game.map.loader;

import engine.loader.PLYLoader;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector2i;

/**
 * This simple map loader is meant for testing purposes.
 * <p>
 * It has a hard coded map.
 */
public class SimpleMapLoader implements MapLoader {
    @Override
    public Tile[][] load() throws Exception {

        Mesh mesh = PLYLoader.loadMesh("/models/PLY/cube.ply");

        return new Tile[][]{
                {new Tile(new Vector2i(0, 0), mesh)},
                {new Tile(new Vector2i(0, 0), mesh)},
                {new Tile(new Vector2i(0, 0), mesh)},
        };
    }
}
