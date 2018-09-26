package engine.entities;

import engine.GameWindow;
import engine.KeyboardInput;
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

    @Override
    public void update(float delta) {
        GameWindow window = GameWindow.getGameWindow();
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
        }

        // TODO: Update the player based on delta time
    }

}
