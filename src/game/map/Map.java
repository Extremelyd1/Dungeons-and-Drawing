package game.map;

import game.map.loader.MapLoader;
import game.map.tile.Tile;
import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {

    /**
     * 2d tile array in which the tiles are stored in a [x][y] fashion
     */
    private Tile[][] tiles;
    /**
     * Map that stores all tiles with a specific tag
     */
    private java.util.Map<String, List<Tile>> taggedTiles;
    /**
     * The width of the tile map
     */
    private int width;
    /**
     * The height of the tile map
     */
    private int height;

    public Map(Tile[][] tiles) {
        this(tiles.length, tiles[0].length, tiles, new HashMap<>());
    }

    public Map(Tile[][] tiles, java.util.Map<String, List<Tile>> taggedTiles) {
        this(tiles.length, tiles[0].length, tiles, taggedTiles);
    }

    public Map(int width, int height, Tile[][] tiles, java.util.Map<String, List<Tile>> taggedTiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
        this.taggedTiles = taggedTiles;
    }

    public Tile[][] getTiles() {
        return tiles;
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
            if (tiles[x - 1][y] != null) {
                neighbours.add(tiles[x - 1][y]);
            }
        }
        if (x < width - 1) {
            if (tiles[x + 1][y] != null) {
                neighbours.add(tiles[x + 1][y]);
            }
        }
        if (y > 0) {
            if (tiles[x][y - 1] != null) {
                neighbours.add(tiles[x][y - 1]);
            }
        }
        if (y < height - 1) {
            if (tiles[x][y + 1] != null) {
                neighbours.add(tiles[x][y + 1]);
            }
        }

        return neighbours.toArray(new Tile[]{});
    }

    /**
     * Get all tiles with the given tag
     * @param tag to search for
     * @return a list with all tiles that have the given tag
     */
    public List<Tile> getTiles(String tag) {
        return taggedTiles.get(tag);
    }

    /**
     * Gets the first tile with the given tag
     * @param tag to search for
     * @return the first tile with given tag
     */
    public Tile getTile(String tag) {
        List<Tile> tiles = taggedTiles.get(tag);
        if (tiles == null || tiles.isEmpty()) {
            return null;
        }
        return tiles.get(0);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
            Tile tile = getTile(x, y);
            if (tile == null) {
                return true;
            }
            if (tile.isSolid()) {
                return true;
            }
        }
        return false;
    }
}
