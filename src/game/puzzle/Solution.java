package game.puzzle;

import game.action.Action;

public class Solution {

    private String value;
    private Action action;

    public Solution(String value, Action action) {
        this.value = value;
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public Action getAction() {
        return action;
    }
}
