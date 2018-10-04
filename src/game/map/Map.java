package game.map;

import game.map.loader.MapLoader;
import game.map.tile.Tile;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Map {

    /**
     * 2d tile array in which the tiles are stored in a [x][y] fashion
     */
    private Tile[][] tiles;
    /**
     * The width of the tile map
     */
    private int width;
    /**
     * The height of the tile map
     */
    private int height;

    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Loads a map. It assumes that the map has at least a width and height of 1
     *
     * @param loader Loader
     * @throws Exception if the loading goes wrong
     */
    public void load(MapLoader loader) throws Exception {
        tiles = loader.load();
        width = tiles.length;
        height = tiles[0].length;
    }

    /**
     * Query a tile based on its x and y position
     *
     * @param x x position in the array
     * @param y y position in the array
     * @return Tile
     */
    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public Tile[] getNeighbours(Tile tile) {
        List<Tile> neighbours = new ArrayList<>();
        int x = tile.getPosition().x;
        int y = tile.getPosition().y;

        if (x > 0) {
            neighbours.add(tiles[x - 1][y]);
        }
        if (x < width - 1) {
            neighbours.add(tiles[x + 1][y]);
        }
        if (y > 0) {
            neighbours.add(tiles[x][y - 1]);
        }
        if (y < height - 1) {
            neighbours.add(tiles[x][y + 1]);
        }

        return neighbours.toArray(new Tile[]{});
    }

    public Tile[] getNeighbours(Tile tile, boolean solid) {
        List<Tile> filteredTiles = new ArrayList<>();
        Tile[] neighbours = getNeighbours(tile);

        for (Tile t : neighbours) {
            // Check if we should only add solid tiles
            if (solid && t.isSolid()) {
                filteredTiles.add(t);
            }
            // Check if we should add non-solid tiles
            if (!solid && !t.isSolid()) {
                filteredTiles.add(t);
            }
        }

        return filteredTiles.toArray(new Tile[]{});
    }

    /**
     * Checks whether the specified square collides with
     * any solid tiles in this map
     * @return whether the circle collides with any solid tiles
     */
    public boolean collidesSolid(float x1, float x2, float y1, float y2) {
        Vector2f[] points = new Vector2f[]{
                new Vector2f(x1, y1),
                new Vector2f(x2, y1),
                new Vector2f(x1, y2),
                new Vector2f(x2, y2)
        };

        for (Vector2f point : points) {
            int x = Math.round(point.x);
            int y = Math.round(point.y);

            if (x < 0 || y < 0 || x >= tiles.length || y >= tiles[x].length) {
                return true;
            }
            if (getTile(x, y).isSolid()) {
                return true;
            }
        }
        return false;
    }
}
