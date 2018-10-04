package pathfinding;

import game.map.Map;
import game.map.loader.SimpleMapLoader;
import game.map.tile.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Pathfinding_Validator {
    public static void main(String[] args) {
        new Pathfinding_Validator().run();
    }

    void run() {
        JFrame f = new JFrame();
        f.add(new GridPane(5,5));
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
            }
        }
    }

    //Class that defines what happens (i.e: the color changes) when a panel is clicked
    class BoxListener extends MouseAdapter {
        public void mouseClicked(MouseEvent me)
        {
            JPanel clickedBox =(JPanel)me.getSource(); // get the reference to the box that was clicked
            clickedBox.setBackground(Color.BLUE);
            // insert here the code defining what happens when a grid is clicked
        }
    }
}
