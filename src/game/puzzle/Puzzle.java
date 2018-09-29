package game.puzzle;

import game.action.Action;

public abstract class Puzzle {

    private String description;
    private String[] options;
    private Solution[] solutions;
    private float time;

    public Puzzle(String description, String[] options, Solution[] solutions, float time) {
        this.description = description;
        this.options = options;
        this.solutions = solutions;
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public String[] getOptions() {
        return options;
    }

    public Solution[] getSolutions() {
        return solutions;
    }

    public float getTime() {
        return time;
    }

    public Action evaluate() {
        // TODO: Evaluate image and return appropiate action

        return null; // TODO: Replace with action
    }
}
