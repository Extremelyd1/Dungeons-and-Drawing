package game.debug;

import engine.entities.Entity;
import graphics.Material;
import graphics.Mesh;

/**
 * Utility object that is used in the debug menu to show the axis in game
 */
public class Axis extends Entity {

    public Axis(Mesh mesh) {
        super(mesh);

        this.getMesh().setMaterial(new Material(0f));
    }

    public void update() {
        // TODO: Here we should determine the position of the axis.
        // TODO: It should be at the center of the screen
    }

    public void render() {
        // TODO: The axis should be rendered in front of all objects
        mesh.render();
    }

}
