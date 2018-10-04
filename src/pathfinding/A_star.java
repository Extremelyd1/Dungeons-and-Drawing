package pathfinding;

import game.map.Map;
import game.map.tile.Tile;
import java.util.*;

/**
 * Class that implements the A* algorithm and executes it.
 * @Author Koen Degeling (1018025)
 */
public class A_star implements Pathfinding{
    /* Open queue which keeps track of all the opened Tiles */
    private PriorityQueue<Node> open;
    /* Closed list which contains all the closed Tiles */
    private Hashtable<Tile, Boolean> closed;

    /**
     * Method that computes the path and returns a list of tiles, the path we have to take to reach
     * the target.
     * @param start the starting position of our A*-algorithm
     * @param target the target position of our A*-algorithm
     * @param map the map, containing the information of our graph
     * @return List containing the tiles we have to visit in sequence to reach the target.
     */
    @Override
    public List<Tile> computePath(Tile start, Tile target, Map map) {
        // Initialise our open queue and comparator
        open = new PriorityQueue<>();
        // List containing all the 'closed' nodes
        closed = new Hashtable<>();
        //We add the first node to the open queue
        open.add(new Node(start, 0, Math.abs(start.getPosition().x-target.getPosition().x)
                + Math.abs(start.getPosition().y-target.getPosition().y), null));
        Node q = new Node(null, 0, 0, null);

        outerloop:
        while (!open.isEmpty()) {
            q = open.poll(); // Get the fist tile from the queue
            // We get all of q's successors and put them on the open list
            for (Tile n : map.getNeighbours(q.t) ) {
                // If the tile is walkable, add the tile to the open list
                if (n==target) {
                    closed.put(q.t, true);
                    closed.put(n, true);
                    q = new Node(n, q.g+1, 0, q);
                    break outerloop;
                }
                if (!n.isSolid() && !closed.containsKey(n)) {
                    int h = Math.abs(n.getPosition().x-target.getPosition().x)
                        + Math.abs(n.getPosition().y-target.getPosition().y);
                    open.add(new Node(n, q.g+1, h, q)); // Add new node to open queue
                }
            }
            closed.put(q.t, true); //We add q to the closed list containing all the opened tiles
        }
        Node pt = q;
        LinkedList<Tile> path = new LinkedList<>();
        while (pt.p != null) {
            path.addFirst(pt.t);
            pt = pt.p;
        }
        path.addFirst(pt.t); // Add the start tile (without a parent)
        return path;
    }

    public LinkedList<Tile> getOpenedTiles() {
        LinkedList<Tile> a = new LinkedList<>();
        for (Node x : open) {
            a.add(x.t);
        }
        return a;
    }

    public Set<Tile> getClosedTiles() {
        return closed.keySet();
    }
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