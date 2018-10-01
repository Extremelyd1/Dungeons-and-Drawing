package pathfinding;

import game.map.Map;
import game.map.tile.Tile;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Class that implements the A* algorithm and executes it.
 * @Author Koen Degeling (1018025)
 */
public class A_star {
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
        PriorityQueue<Pair> open = new PriorityQueue<>();
        // List containing all the 'closed' nodes
        LinkedList<Pair> closed = new LinkedList<>();
        open.add(new Pair(start, 0, Math.abs(start.getPosition().x-target.getPosition().x)
                + Math.abs(start.getPosition().y-target.getPosition().y), null)); //We add the first node to the open queue
        outerloop:
        while (!open.isEmpty()) {
            Pair q = open.poll(); // Get the fist tile from the queue
            // We get all of q's successors and put them on the open list
            for (Tile n : map.getNeighbours(q.t) ) {
                // If the tile is walkable, add the tile to the open list
                if (n.getPosition().x == target.getPosition().x && n.getPosition().y == target.getPosition().y) {
                    closed.add(q);
                    closed.add(new Pair(n, q.g+1, 0, q));
                    break outerloop;
                }
                if (!n.isSolid() && !inClosed(n, closed)) {
                    int h = Math.abs(n.getPosition().x-target.getPosition().x)
                        + Math.abs(n.getPosition().y-target.getPosition().y);
                    open.add(new Pair(n, q.g+1, h, q));
                }
            }
            closed.add(q); //We add q to the closed list containing all the tiles
        }
        Pair pt = closed.getLast();
        LinkedList<Tile> path = new LinkedList<>();
        while (pt.p != null) {
            path.addFirst(pt.t);
            pt = pt.p;
        }
        path.addFirst(pt.t);
        for (Tile t : path) {
            System.out.println("("+t.getPosition().x + "," + t.getPosition().y+")");
        }
        return path;
    }

    private boolean inClosed(Tile n, List<Pair> closed) {
        for (Pair x : closed) {
            if (x.t == n) return true;
        }
        return false;
    }

    /**
     * (Tile, g(t)) pair, defining a tile and the corresponding f(t) weight function.
     */
    private class Pair implements Comparable<Pair> {
        Tile t;
        int g, f, h;
        Pair p;

        public Pair(Tile t, int g, int h, Pair p) {
            this.t = t;
            this.g = g; this.h = h;
            this.f = this.g + this.h;
            this.p = p;
        }

        @Override
        public int compareTo(Pair o) {
            if(o.f < this.f) return 1; else return -1;
        }
    }
}
