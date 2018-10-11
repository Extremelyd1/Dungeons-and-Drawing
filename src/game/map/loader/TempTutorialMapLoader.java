package game.map.loader;

import engine.loader.PLYLoader;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Material;
import graphics.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class TempTutorialMapLoader implements MapLoader {

    @Override
    public Map load() throws Exception {

        Mesh cube = PLYLoader.loadMesh("/models/tiles/basic_brown_cube_1.ply");
        Mesh wall = PLYLoader.loadMesh("/models/tiles/corner_wall.ply");
        Mesh cell = PLYLoader.loadMesh("/models/tiles/prison_bars.ply");
        Mesh arc = PLYLoader.loadMesh("/models/tiles/arc.ply");
        Mesh crate = PLYLoader.loadMesh("/models/tiles/crate.ply");
        Mesh pebbles = PLYLoader.loadMesh("/models/tiles/floor_pebbles.ply");
        Mesh bush = PLYLoader.loadMesh("/models/tiles/test.ply");
        Mesh door = PLYLoader.loadMesh("/models/tiles/wooden_door.ply");

        cube.setMaterial(new Material(0f));
        wall.setMaterial(new Material(0f));
        cell.setMaterial(new Material(0f));
        arc.setMaterial(new Material(0f));
        crate.setMaterial(new Material(0f));
        pebbles.setMaterial(new Material(0f));
        bush.setMaterial(new Material(0f));
        door.setMaterial(new Material(0f));

        int x = 0;

        Tile[][] tileList = new Tile[][]{
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x++, 4), new Vector3f(0, 0, 0), wall, true)
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), pebbles, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x++, 4), new Vector3f(0, 0, 0), wall, true)
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 90, 0), arc, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 0, 0), wall, true)
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), bush, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 0, 0), cube, false),
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), crate, true),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 0, 0), bush, false),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 0, 0), cube, false),
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), bush, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 0, 0), cube, false),
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 90, 0), cell, true)
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 0, 0), door, true),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 0, 0), cube, false)
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), pebbles, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 180, 0), door, true),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 0, 0), cube, false)
                },
                {
                        new Tile(new Vector2i(x, 0), new Vector3f(0, 0, 0), wall, true),
                        new Tile(new Vector2i(x, 1), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 2), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 3), new Vector3f(0, 0, 0), cube, false),
                        new Tile(new Vector2i(x, 4), new Vector3f(0, 0, 0), cell, true),
                        new Tile(new Vector2i(x++, 5), new Vector3f(0, 0, 0), pebbles, false)
                },
        };

        return new Map(tileList);
    }
}
