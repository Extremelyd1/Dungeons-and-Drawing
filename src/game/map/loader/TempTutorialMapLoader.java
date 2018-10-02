package game.map.loader;

import engine.loader.PLYLoader;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class TempTutorialMapLoader implements MapLoader {

    @Override
    public Tile[][] load() throws Exception {

        Mesh cube = PLYLoader.loadMesh("/models/basic/basic_brown_cube_1.ply");
        Mesh wall = PLYLoader.loadMesh("/models/basic/basic_yellow_wall_1.ply");

        cube.setMaterial(new Material(0f));
        wall.setMaterial(new Material(0f));

        return new Tile[][]{
                {
                        new Tile(new Vector2i(0, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(1, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(2, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(3, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(4, 0), new Vector3f(0, 0, 0), wall, true)
                },
                {
                        new Tile(new Vector2i(0, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(1, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(2, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(3, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(4, 1), new Vector3f(0, 0, 0), cube, false)
                }
        };
    }
}
