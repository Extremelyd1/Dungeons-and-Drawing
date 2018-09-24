package game.puzzle;

public abstract class Puzzle {

    private String description;
    private float time;
    private String[] options;

    public Puzzle(String description, float time, String[] options) {
        this.description = description;
        this.time = time;
        this.options = options;
    }

    public String getDescription() {
        return description;
    }

    public float getTime() {
        return time;
    }

    public String[] getOptions() {
        return options;
    }
}
