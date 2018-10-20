package game.puzzle;

import engine.gui.DrawingCanvas;
import game.NeuralNetwork;
import game.action.Action;
import game.action.PostPuzzleAction;

public class Puzzle {

    private String description;
    private String[] options;
    private Solution[] solutions;
    private Solution defaultSolution;
    private float time;

    public Puzzle(String description, String[] options, Solution[] solutions, Solution defaultSolution, float time) {
        this.description = description;
        this.options = options;
        this.solutions = solutions;
        this.defaultSolution = defaultSolution;
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

    public Solution getDefaultSolution() {
        return defaultSolution;
    }

    public float getTime() {
        return time;
    }

    public PostPuzzleAction evaluate(String value) {

        System.err.println("I guessed " + value);

        for (Solution solution : solutions) {
            if (solution.getValue().equals(value)) {
                return solution.getAction();
            }
        }

        return defaultSolution.getAction();
    }
}
