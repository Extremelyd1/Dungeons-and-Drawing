package game.map.loader;

import game.map.tile.Tile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MapFileLoader implements MapLoader {

    private String resourcePath;

    public MapFileLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public Tile[][] load() throws Exception {

        throw new NotImplementedException();

    }
}
