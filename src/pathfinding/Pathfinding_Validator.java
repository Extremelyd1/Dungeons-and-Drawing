package pathfinding;

import game.map.Map;
import game.map.loader.SimpleMapLoader;
import game.map.tile.Tile;

public class Pathfinding_Validator {
    public static void main(String[] args) {
        new Pathfinding_Validator().run();
    }

    void run() {
        Map map = new Map();
        try {
            map.load(new SimpleMapLoader());
        } catch(Exception e) {
            System.err.println("Failed to load map:" + e);
        }
        Tile[][] grid = map.getTiles();
        //@TODO: Make Swing UI
    }
}
