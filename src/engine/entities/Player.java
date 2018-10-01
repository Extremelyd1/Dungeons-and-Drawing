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

    public Player(Mesh mesh, Map map){
        super(mesh, map);
    }

    public Player(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, map, position, rotation);
    }

    public Player(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, map, position, rotation, speed);
    }

    public Player(Mesh mesh, Map map, Vector3f position, float scale) {
        super(mesh, map, position, new Vector3f(0), scale, 1);
    }

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

        float xChange = 0;
        float zChange = 0;

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
        Vector3f newPosition = new Vector3f(getPosition()).add(xChange, 0, zChange);

        int x = (int) Math.floor(getPosition().x);
        int z = (int) Math.floor(getPosition().z);

        int xNew = (int) Math.floor(newPosition.x);
        int zNew = (int) Math.floor(newPosition.z);

        Tile xChangeTile = getMap().getTile(xNew, z);
        Tile zChangeTile = getMap().getTile(x, zNew);

        if (!xChangeTile.isSolid()) {
            getPosition().add(xChange, 0, 0);
        }
        if (!zChangeTile.isSolid()) {
            getPosition().add(0, 0, zChange);
        }
    }

}
