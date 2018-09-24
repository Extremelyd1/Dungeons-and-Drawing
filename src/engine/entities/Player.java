package engine.entities;

import engine.KeyboardInput;
import engine.entities.GameEntity;
import graphics.Mesh;
import org.lwjgl.glfw.GLFW;

public class Player extends GameEntity {

    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = -50;
    private static final float TERRAIN = 0;

    private float current_speed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(Mesh mesh){
        super(mesh);
    }

    public void move(){
        checkIntputs();
        increaseRotation(0, currentTurnSpeed, 0);
        float distance = current_speed;
        float dx = (float)(distance * Math.sin(Math.toRadians(super.getXRotation())));
        float dz = (float)(distance * Math.cos(Math.toRadians(super.getXRotation())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed+=GRAVITY;
        super.increasePosition(0, upwardsSpeed, 0);
        if (super.getPosition().y < TERRAIN){
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = TERRAIN;
        }
    }

    private void jump(){
        if(!isInAir) {
        this.upwardsSpeed = JUMP_POWER;
        isInAir = true;
        }
    }

    private void checkIntputs(){
        if (KeyboardInput.keys[GLFW.GLFW_KEY_T]){
            this.current_speed = RUN_SPEED;
        } else if(KeyboardInput.keys[GLFW.GLFW_KEY_G]){
            this.current_speed = -RUN_SPEED;
        } else {
            this.current_speed = 0;
        }

        if (KeyboardInput.keys[GLFW.GLFW_KEY_H]){
            this.currentTurnSpeed = -TURN_SPEED;
        } else if(KeyboardInput.keys[GLFW.GLFW_KEY_F]){
            this.currentTurnSpeed = +TURN_SPEED;
        } else{
            this.currentTurnSpeed = 0;
        }

        if (KeyboardInput.keys[GLFW.GLFW_KEY_SPACE]){
            jump();
        }
    }


}
