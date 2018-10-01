package engine.entities;

import engine.GameWindow;
import engine.KeyboardInput;
import engine.input.KeyBinding;
import graphics.Mesh;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Player class, the controllable PC of the game
 */
public class Player extends LivingEntity {

    public Player(Mesh mesh){
        super(mesh);
    }

    public Player(Mesh mesh, Vector3f position, Vector3f rotation) {
        super(mesh, position, rotation);
    }

    public Player(Mesh mesh, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, position, rotation, speed);
    }

    // TODO: parameter delta currently passed is not actually delta
    /**
     * Updates the player logic
     * @param delta the time in seconds since previous update call
     */
    @Override
    public void update(float delta) {
        GameWindow window = GameWindow.getGameWindow();
        boolean forward = KeyBinding.isForwardPressed();
        boolean backward = KeyBinding.isBackwardPressed();
        boolean left = KeyBinding.isLeftPressed();
        boolean right = KeyBinding.isRightPressed();

        if (forward) {
            if (left) {
                this.getPosition().add((float) (-delta * this.getSpeed() * 1 / Math.sqrt(2)), 0, (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2)));

                this.getRotation().set(0, 315, 0);
            } else if (right) {
                this.getPosition().add((float) (delta * this.getSpeed() * 1 / Math.sqrt(2)), 0, (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2)));

                this.getRotation().set(0, 45, 0);
            } else {
                this.getPosition().add(0, 0,-delta * this.getSpeed());

                this.getRotation().set(0, 0, 0);
            }
        } else if (backward) {
            if (left) {
                this.getPosition().add((float) (-delta * this.getSpeed() * 1 / Math.sqrt(2)), 0, (float) (delta * this.getSpeed() * 1 / Math.sqrt(2)));

                this.getRotation().set(0, 225, 0);
            } else if (right) {
                this.getPosition().add((float) (delta * this.getSpeed() * 1 / Math.sqrt(2)), 0, (float) (delta * this.getSpeed() * 1 / Math.sqrt(2)));

                this.getRotation().set(0, 135, 0);
            } else {
                this.getPosition().add(0, 0, delta * this.getSpeed());

                this.getRotation().set(0, 180, 0);
            }
        } else if (left) {
            this.getPosition().add(-delta * this.getSpeed(), 0, 0);

            this.getRotation().set(0,270, 0);
        } else if (right) {
            this.getPosition().add(delta * this.getSpeed(), 0, 0);

            this.getRotation().set(0, 90, 0);
        }

    }

}
