package game.map.loader;

import game.map.tile.Tile;

/**
 * Interface for loading maps
 */
public interface MapLoader {

    /**
     * This method loads a map from a source. The array should be loaded into a
     * 2-dimensional format, following [x][y].
     *
     * @return A two dimensional tile array
     * @throws Exception when the loading goes wrong
     */
    public Tile[][] load() throws Exception;

}
