package pathfinding;

import game.map.Map;
import game.map.tile.Tile;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Class that implements the A* algorithm and executes it.
 * @Author Koen Degeling (1018025)
 */
public class A_star {
    public static void main(String[] args) {
        
    }
    /**
     * Method that computes the path and returns a list of tiles, the path we have to take to reach
     * the target.
     * @param start the starting position of our A*-algorithm
     * @param target the target position of our A*-algorithm
     * @param map the map, containing the information of our graph
     * @return List containing the tiles we have to visit in sequence to reach the target.
     */
    public List<Tile> computePath(Tile start, Tile target, Map map) {
        // Initialise our open queue and comparator
        PriorityQueue<Pair> open = new PriorityQueue<Pair>() {
            // Open queue inserts 'open' nodes which we sort on their g(t) value
            public int compare(Pair p, Pair q) {
                if (p.g < q.g) return -1; else return 1;
            }
        };
        // List containing all the 'closed' nodes
        LinkedList<Tile> closed = new LinkedList<>();
        open.add(new Pair(start, 0, Math.abs(start.getPosition().x-target.getPosition().x)
                + Math.abs(start.getPosition().y-target.getPosition().y))); //We add the first node to the open queue

        while (!open.isEmpty()) {
            Pair q = open.poll(); // Get the fist tile from the queue
            // We get all of q's successors and put them on the open list
            for (Tile n : map.getNeighbours(q.t) ) {
                // If the tile is walkable, add the tile to the open list
                if (n == target) {
                    break;
                }
                if (!n.isSolid()) {
                    int h = Math.abs(n.getPosition().x-target.getPosition().x)
                        + Math.abs(n.getPosition().y-target.getPosition().y);
                    open.add(new Pair(n, q.g+1, h));
                }
            }
            closed.add(q.t); //We add q to the closed list containing all the tiles
        }
        return closed;
    }

    /**
     * (Tile, g(t)) pair, defining a tile and the corresponding f(t) weight function.
     */
    private class Pair {
        Tile t;
        int g, f, h;

        public Pair(Tile t, int g, int h) {
            this.t = t;
            this.g = g; this.h = h;
            this.f = this.g + this.h;
        }
    }
}
