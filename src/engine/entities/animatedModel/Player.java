package engine.entities.animatedModel;

import engine.GameWindow;
import engine.entities.LivingEntity;
import engine.input.KeyBinding;
import engine.util.Timer;
import game.map.Map;
import graphics.AnimatedMesh;
import graphics.Mesh;
import graphics.Texture;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F6;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Player class, the controllable PC of the game
 */
public class Player extends LivingEntity {

    private AnimatedModel animatedModel;

    // The range of the cylindrical collision box around the player
    float collisionSize;

    private boolean checkCollision = true;

    public Player(AnimatedModel model, Map map){
        super(null, map);
        this.animatedModel = model;
        collisionSize = 0.45f;
    }

    public Player(AnimatedModel model, Map map, Vector3f position, Vector3f rotation) {
        super(null, map, position, rotation);
        this.animatedModel = model;
        collisionSize = 0.45f;
    }

    public Player(AnimatedModel model, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(null, map, position, rotation, speed);
        this.animatedModel = model;
        collisionSize = 0.45f;
    }

    public Player(AnimatedModel model, Map map, Vector3f position, float scale) {
        super(null, map, position, new Vector3f(0), scale, 1);
        this.animatedModel = model;
        collisionSize = 0.45f;
    }

    /**
     * Updates the player logic
     * @param delta the time in seconds since previous update call
     */
    @Override
    public void update(float delta) {

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

                this.getRotation().set(0, 45, 0);
            } else if (right) {
                xChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));
                zChange = (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2));

                this.getRotation().set(0, 315, 0);
            } else {
                zChange = -delta * this.getSpeed();

                this.getRotation().set(0, 0, 0);
            }
        } else if (backward) {
            if (left) {
                xChange = (float) (-delta * this.getSpeed() * 1 / Math.sqrt(2));
                zChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));

                this.getRotation().set(0, 135, 0);
            } else if (right) {
                xChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));
                zChange = (float) (delta * this.getSpeed() * 1 / Math.sqrt(2));

                this.getRotation().set(0, 225, 0);
            } else {
                zChange = delta * this.getSpeed();

                this.getRotation().set(0, 180, 0);
            }
        } else if (left) {
            xChange = -delta * this.getSpeed();

            this.getRotation().set(0,90, 0);
        } else if (right) {
            xChange = delta * this.getSpeed();

            this.getRotation().set(0, 270, 0);
        }

        if (xChange == 0 && zChange == 0) {
            animatedModel.getAnimator().update(delta * getSpeed(), true);
            return;
        }

        animatedModel.getAnimator().update(delta * getSpeed() * 3, false);

        if (!checkCollision) {
            getPosition().add(xChange, 0, zChange);
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

    @Override
    public Mesh getMesh() {
        return animatedModel.getMesh();
    }

    @Override
    public void render() {
        animatedModel.getMesh().render();
    }

    public AnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    public void toggleCollisionDetection() {
        checkCollision = !checkCollision;
    }

}
