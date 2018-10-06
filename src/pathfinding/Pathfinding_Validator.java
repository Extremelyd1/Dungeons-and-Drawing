package pathfinding;

import game.map.Map;
import game.map.loader.PathfindMapLoader;
import game.map.loader.SimpleMapLoader;
import game.map.tile.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Pathfinding_Validator {
    Map map;
    int count;
    Tile start; Tile target;
    JButton bt; JPanel[][] grid;
    public static void main(String[] args) {
        new Pathfinding_Validator().run();
    }

    public void run() {
        // Make a new map for our A*-search
        map = new Map();
        try {
            map.load(new PathfindMapLoader());
        } catch(Exception e) {
            System.err.println("Map not loaded: " + e);
        }
        //Make a new frame with all the components
        JFrame f = new JFrame("A*-search Validator");
        grid = new JPanel[map.getWidth()][map.getHeight()];
        f.add(new GridPane(map.getWidth(), map.getHeight()), BorderLayout.CENTER);
        bt = new JButton("Run A*-search");
        bt.setEnabled(false);
        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                A_star alg = new A_star();
                long time = System.nanoTime();
                List<Tile> path = alg.computePath(start, target, map);
                time = System.nanoTime() - time;
                for (Tile s : alg.getClosedTiles()) {
                    grid[s.getPosition().x][s.getPosition().y].setBackground(Color.BLUE);
                }
                for (Tile s : alg.getOpenedTiles()) {
                    grid[s.getPosition().x][s.getPosition().y].setBackground(Color.CYAN);
                }
                for (Tile p : path) {
                    grid[p.getPosition().x][p.getPosition().y].setBackground(Color.GRAY);
                }
                grid[start.getPosition().x][start.getPosition().y].setBackground(Color.GREEN);
                grid[target.getPosition().x][target.getPosition().y].setBackground(Color.RED);
                System.out.println("Grid coverage: " + (alg.getClosedTiles().size())
                        /(float)(map.getHeight()*map.getWidth()));
                System.out.println("Time to compute in nsecond: " + time);
            }
        });
        f.add(bt, BorderLayout.SOUTH);
        JButton rst = new JButton("Reset");
        rst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid.length; j++) {
                        grid[i][j].setBackground(Color.WHITE);
                        count = 0;
                    }
                }
                try {
                    map.load(new PathfindMapLoader());
                } catch (Exception ex) {
                    System.err.println("Failed to load map" + ex);
                }
            }
        });
        f.add(rst, BorderLayout.NORTH);
        f.setVisible(true);
        f.setSize(new Dimension(500, 500));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    class GridPane extends JPanel {

        public GridPane(int row, int col) {

            int count = 0 ; // use to give a name to each box so that you can refer to them later
            setLayout(new GridLayout(row, col));
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            for (int i = 1; i <= (row * col); i++) {
                JPanel pan = new JPanel();

                pan.setEnabled(true);
                pan.setBackground(Color.WHITE);
                pan.setPreferredSize(new Dimension(3, 3));
                pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                pan.addMouseListener(new BoxListener()); // add a mouse listener to make the panels clickable
                pan.setName(count+"");
                ++count;
                add(pan);
                grid[(count-1)/row][(count-1)%col] = pan;
            }
        }
    }

    //Class that defines what happens (i.e: the color changes) when a panel is clicked
    class BoxListener extends MouseAdapter {
        public void mouseClicked(MouseEvent me)
        {
            JPanel clickedBox =(JPanel)me.getSource(); // get the reference to the box that was clicked
            int n = Integer.parseInt(clickedBox.getName());
            int m = map.getTiles().length;
            if (count==0) {
                start = map.getTile(n/m, n%m);
                clickedBox.setBackground(Color.GREEN);
                count++;
            } else if (count==1) {
                target = map.getTile(n/m, n%m);
                clickedBox.setBackground(Color.RED);
                count++;
            } else {
                map.getTile(n/m, n%m).setSolid(true);
                clickedBox.setBackground(Color.DARK_GRAY);
            }
            if (count > 1) bt.setEnabled(true);

            // insert here the code defining what happens when a grid is clicked
        }
    }
}
