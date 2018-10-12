package engine.gui;

import engine.GameWindow;
import engine.MouseInput;
import game.puzzle.Puzzle;

/**
 * Renders all the GUI needed for a puzzle
 */
public class PuzzleGUI extends GUIComponent{

    private static final float PADDING = 25;

    private DrawingList list;
    private DrawingCanvas canvas;

    public PuzzleGUI(Puzzle puzzle) {

        this.canvas = new DrawingCanvas();
        this.list = new DrawingList(puzzle, canvas);

        float windowHeight = GameWindow.getGameWindow().getWindowHeight();
        float totalPuzzleGUIWidth = windowHeight + PADDING;

        setComponentHeight(windowHeight * 0.75f);
        setComponentWidth(totalPuzzleGUIWidth);
        setCentered(true);
    }

    @Override
    public void render() {
        canvas.render();
        list.render();
    }

    @Override
    public void update(MouseInput mouse, float delta) {
        super.update(mouse, delta);

        canvas.setPosition(this.getPosition().x, this.getPosition().y);
        list.setPosition(this.getPosition().x + getComponentHeight() + PADDING, this.getPosition().y);

        canvas.update(mouse, delta);
        list.update(mouse, delta);
    }
}
