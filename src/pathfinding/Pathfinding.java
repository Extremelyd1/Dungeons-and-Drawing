package pathfinding;

import game.map.Map;
import game.map.tile.Tile;

import java.util.Collection;
import java.util.List;

public interface Pathfinding {
    /**
     * Method that returns a list of Tiles corresponding to the shortest path from the start Tile to the target
     * Tile.
     * @param start Tile which is the start of our search
     * @param target Tile which is the end of our search
     * @param map Map which contains the space to search through
     * @return List<Tile> containing the shortest computed path from start to target
     */
    List<Tile> computePath(Tile start, Tile target, Map map);
}
