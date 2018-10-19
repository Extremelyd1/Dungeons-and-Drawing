package game.puzzle;

import game.action.Action;
import game.action.PostPuzzleAction;

public class Solution {

    private String value;
    private PostPuzzleAction action;

    public Solution(String value, PostPuzzleAction action) {
        this.value = value;
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public PostPuzzleAction getAction() {
        return action;
    }
}
