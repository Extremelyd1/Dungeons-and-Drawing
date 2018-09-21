package game.map;

import game.map.loader.MapLoader;
import game.map.tile.Tile;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Map {

    /**
     * This number defines the width and height of a tile in world coordinates
     */
    private static final float TILE_DIMENSION = 1.0f;
    /**
     * This number defines the lowest point of the map, which will be the y-coord
     * for all tiles
     */
    private static final float MIN_Y = 0;

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
     */
    public void load(MapLoader loader) {
        tiles = loader.load();
        width = tiles.length;
        height = tiles[0].length;
    }

    /**
     * Query a tile based on its x and y position in the array
     *
     * @param x x position in the array
     * @param y y position in the array
     * @return Tile
     */
    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    /**
     * Query a tile based on world coordinates.
     * <p>
     * TODO: Is this method even useful if each tile has a 1 unit dimension in world coordinates?
     *
     * @param position The world coordinates of the tile
     * @return Tile
     */
    public Tile getTile(Vector3f position) {
        int x = (int) (position.x / TILE_DIMENSION);
        int y = (int) (position.y / TILE_DIMENSION);

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
     * Maps tile coordinates to world coordinates
     *
     * @param x x coordinate of the tile in the array
     * @param y y coordinate of the tile in the array
     * @return Position vector of the tile in world coordinates
     */
    public static Vector3f coordsToWorldCoords(int x, int y) {
        return new Vector3f(x, y, MIN_Y);
    }
}
