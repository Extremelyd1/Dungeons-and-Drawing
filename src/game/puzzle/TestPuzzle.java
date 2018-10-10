package game.puzzle;

import game.action.Action;
import game.action.TestAction;

public class TestPuzzle extends Puzzle {
    public TestPuzzle() {
        super("This is a test puzzle", new String[] {"Option 1", "Option 2", "Option 3"}, null, 20);
    }

    @Override
    public Action evaluate() {
        return new TestAction();
    }
}
