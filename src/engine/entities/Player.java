package engine.entities;

import engine.GameWindow;
import engine.KeyboardInput;
import engine.input.KeyBinding;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Player class, the controllable PC of the game
 */
public class Player extends LivingEntity {

    // The range of the cylindrical collision box around the player
    float collisionRange;

    public Player(Mesh mesh, Map map){
        super(mesh, map);
        collisionRange = 0.3f;
    }

    public Player(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, map, position, rotation);
        collisionRange = 0.3f;
    }

    public Player(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, map, position, rotation, speed);
        collisionRange = 0.3f;
    }

    public Player(Mesh mesh, Map map, Vector3f position, float scale) {
        super(mesh, map, position, new Vector3f(0), scale, 1);
        collisionRange = 0.3f;
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
        Vector3f newPosition = new Vector3f(getPosition()).add(xChange, 0, zChange);
        // The added range to account for a collision box
        float xCollisionAddition = collisionRange * (xChange < 0 ? -1 : 1);
        float zCollisionAddition = collisionRange * (zChange < 0 ? -1 : 1);

        // The old player position
        int x = (int) Math.floor(getPosition().x);
        int z = (int) Math.floor(getPosition().z);
        // New coordinates based on position, movement and collision range
        int xNew = (int) Math.floor(newPosition.x + xCollisionAddition);
        int zNew = (int) Math.floor(newPosition.z + zCollisionAddition);

        // The respective tiles in x and z direction
        // that the player will occupy after movement
        Tile xChangeTile = getMap().getTile(xNew, z);
        Tile zChangeTile = getMap().getTile(x, zNew);
        // If not solid, move in x direction
        if (!xChangeTile.isSolid()) {
            getPosition().add(xChange, 0, 0);
        }
        // If not solid, move in z direction
        if (!zChangeTile.isSolid()) {
            getPosition().add(0, 0, zChange);
        }
    }

}
