package engine.entities;

import engine.GameWindow;
import engine.input.KeyBinding;
import game.map.Map;
import graphics.Mesh;
import org.joml.Vector3f;

/**
 * Player class, the controllable PC of the game
 */
public class Player extends LivingEntity {

    // The range of the cylindrical collision box around the player
    float collisionSize;

    public Player(Mesh mesh, Map map){
        super(mesh, map);
        collisionSize = 0.45f;
    }

    public Player(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, map, position, rotation);
        collisionSize = 0.45f;
    }

    public Player(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, map, position, rotation, speed);
        collisionSize = 0.45f;
    }

    public Player(Mesh mesh, Map map, Vector3f position, float scale) {
        super(mesh, map, position, new Vector3f(0), scale, 1);
        collisionSize = 0.45f;
    }

    /**
     * Updates the player logic
     * @param delta the time in seconds since previous update call
     */
    @Override
    public void update(float delta) {
        GameWindow window = GameWindow.getGameWindow();
        // Get input
        boolean forward = KeyBinding.isForwardPressed();
        boolean backward = KeyBinding.isBackwardPressed();
        boolean left = KeyBinding.isLeftPressed();
        boolean right = KeyBinding.isRightPressed();
        // Store movement changes for collision detection
        float xChange = 0;
        float zChange = 0;

        // Calculate the movement changes
        if (forward) {
            if (left) {
                xChange = (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2));
                zChange = (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2));

                this.getRotation().set(0, 315, 0);
            } else if (right) {
                xChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));
                zChange = (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2));

                this.getRotation().set(0, 45, 0);
            } else {
                zChange = -delta * this.getSpeed();

                this.getRotation().set(0, 0, 0);
            }
        } else if (backward) {
            if (left) {
                xChange = (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2));
                zChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));

                this.getRotation().set(0, 225, 0);
            } else if (right) {
                xChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));
                zChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));

                this.getRotation().set(0, 135, 0);
            } else {
                zChange = delta * this.getSpeed();

                this.getRotation().set(0, 180, 0);
            }
        } else if (left) {
            xChange = -delta * this.getSpeed();

            this.getRotation().set(0,270, 0);
        } else if (right) {
            xChange = delta * this.getSpeed();

            this.getRotation().set(0, 90, 0);
        }

        if (xChange == 0 && zChange == 0) {
            return;
        }

        /*
        Collision detection
         */
        // The position the player is occupying if applying the movement
        Vector3f newPosition = new Vector3f(getPosition()).add(xChange, 0, 0);

        if (!getMap().collidesSolid(newPosition.x - collisionSize, newPosition.x + collisionSize
                , getPosition().z - collisionSize, getPosition().z + collisionSize)) {
            getPosition().add(xChange, 0, 0);
        }

        newPosition = new Vector3f(getPosition()).add(0, 0, zChange);

        if (!getMap().collidesSolid(getPosition().x - collisionSize, getPosition().x + collisionSize
                , newPosition.z - collisionSize, newPosition.z + collisionSize)) {
            getPosition().add(0, 0, zChange);
        }
    }

}
