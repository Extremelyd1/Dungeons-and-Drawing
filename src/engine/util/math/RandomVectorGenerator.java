package engine.util.math;

import org.joml.Random;
import org.joml.Vector3f;

/**
 * Utility class that can choose random between vectors.
 */
public class RandomVectorGenerator {

    /**
     * Seed for the random
     */
    private final int seed = 123456789;
    /**
     * List of vectors to choose from
     */
    private Vector3f[] vectors;
    /**
     * Random object
     */
    private Random random;

    public RandomVectorGenerator(Vector3f... vectors) {
        this.vectors = vectors;
        this.random = new Random(seed);
    }

    /**
     * Generates a new vector based on a vector from the supplied vectors. It
     * copies the values into a new object.
     *
     * @return New copy of a vector in the supplied list
     */
    public Vector3f generate() {
        int index = random.nextInt(vectors.length);

        return new Vector3f(vectors[index]);
    }
}
