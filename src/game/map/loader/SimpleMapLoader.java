package game.map.loader;

import engine.loader.PLYLoader;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
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
        mesh.setMaterial(new Material(0.1f));
        //Tile tile = new Tile(new Vector2i(0, 0), new Vector3f(0, 0, 0), mesh,true);

        return new Tile[][]{
                {
                        new Tile(new Vector2i(0, 0), new Vector3f(0, 0, 0), mesh,true),
                        new Tile(new Vector2i(1, 0), new Vector3f(0, 90, 0), mesh,true),
                        new Tile(new Vector2i(2, 0), new Vector3f(0, 270, 0), mesh,true),
                        new Tile(new Vector2i(3, 0), new Vector3f(0, 0, 0), mesh,true),
                        new Tile(new Vector2i(4, 0), new Vector3f(0, 90, 0), mesh,true),
                        new Tile(new Vector2i(5, 0), new Vector3f(0, 270, 0), mesh,true)
                },
                {
                        new Tile(new Vector2i(0, 1), new Vector3f(0, 0, 0), mesh,true),
                        new Tile(new Vector2i(1, 1), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(2, 1), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(3, 1), new Vector3f(0, 0, 0), mesh,false),
                        new Tile(new Vector2i(4, 1), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(5, 1), new Vector3f(0, 270, 0), mesh,true)
                },
                {
                        new Tile(new Vector2i(0, 2), new Vector3f(0, 180, 0), mesh,true),
                        new Tile(new Vector2i(1, 2), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(2, 2), new Vector3f(0, 180, 0), mesh,false),
                        new Tile(new Vector2i(3, 2), new Vector3f(0, 0, 0), mesh,false),
                        new Tile(new Vector2i(4, 2), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(5, 2), new Vector3f(0, 270, 0), mesh,true)
                },
                {
                        new Tile(new Vector2i(0, 3), new Vector3f(0, 180, 0), mesh,true),
                        new Tile(new Vector2i(1, 3), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(2, 3), new Vector3f(0, 180, 0), mesh,false),
                        new Tile(new Vector2i(3, 3), new Vector3f(0, 0, 0), mesh,false),
                        new Tile(new Vector2i(4, 3), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(5, 3), new Vector3f(0, 270, 0), mesh,true)
                },
                {
                        new Tile(new Vector2i(0, 4), new Vector3f(0, 180, 0), mesh,true),
                        new Tile(new Vector2i(1, 4), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(2, 4), new Vector3f(0, 180, 0), mesh,false),
                        new Tile(new Vector2i(3, 4), new Vector3f(0, 0, 0), mesh,false),
                        new Tile(new Vector2i(4, 4), new Vector3f(0, 90, 0), mesh,false),
                        new Tile(new Vector2i(5, 4), new Vector3f(0, 270, 0), mesh,true)
                },
                {
                        new Tile(new Vector2i(0, 5), new Vector3f(0, 180, 0), mesh,true),
                        new Tile(new Vector2i(1, 5), new Vector3f(0, 90, 0), mesh,true),
                        new Tile(new Vector2i(2, 5), new Vector3f(0, 180, 0), mesh,true),
                        new Tile(new Vector2i(3, 5), new Vector3f(0, 0, 0), mesh,true),
                        new Tile(new Vector2i(4, 5), new Vector3f(0, 90, 0), mesh,true),
                        new Tile(new Vector2i(5, 5), new Vector3f(0, 270, 0), mesh,true)
                }
        };
    }
}
