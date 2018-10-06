package game.map.loader;

import game.map.Map;

/**
 * Interface for loading maps
 */
public interface MapLoader {

    /**
     * This method loads a map from a source.
     *
     * @return A map object that stores all tiles
     * @throws Exception when the loading goes wrong
     */
    public Map load() throws Exception;

}
