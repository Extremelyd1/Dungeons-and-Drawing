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

    /**
     *
     * @return List<Node> of all the tiles that are in the 'open' list after executing the A*-search
     */
    Collection<Tile> getOpenedTiles();

    /**
     *
     * @return List<Tile> of all the tiles in the closed list, these are the tiles that have been expanded
     */
    Collection<Tile> getClosedTiles();
    /**
     * (Tile, g(t)) pair, defining a tile and the corresponding f(t) weight function.
     */
    class Node implements Comparable<Node> {
        Tile t;
        int g, f, h;
        Node p;

        public Node(Tile t, int g, int h, Node p) {
            this.t = t;
            this.g = g; this.h = h;
            this.f = this.g + this.h;
            this.p = p;
        }

        @Override
        public int compareTo(Node o) {
            if(o.f < this.f) return 1; else return -1;
        }
    }
}
