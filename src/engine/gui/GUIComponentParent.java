package engine.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that extends the normal GUI Component with a parent-child system. If the parent is
 * transformed (moved, rotated or scaled), all children are transformed as well in the same
 * way.
 */
public class GUIComponentParent extends GUIComponent {

    private List<GUIComponent> children;

    public GUIComponentParent() {
        super();
        children = new ArrayList<>();
    }

    /**
     * Sets the position for the parent and moves all children along
     */
    @Override
    public void setPosition(float x, float y) {
        float differenceX = x - this.getPosition().x;
        float differenceY = y - this.getPosition().y;
        super.setPosition(x, y);

        for (GUIComponent child : children) {
            float childX = child.getPosition().x + differenceX;
            float childY = child.getPosition().y + differenceY;
            child.setPosition(childX, childY);
        }
    }

    /**
     * Sets the scale for the parent and scales all children along
     */
    @Override
    public void setScale(float scale) {
        float difference = scale / this.getScale();
        super.setScale(scale);

        for (GUIComponent child : children) {
            float childScale = child.getScale() * difference;
            child.setScale(childScale);
        }
    }

    /**
     * Sets the rotation for the parent and rotates all children along
     */
    @Override
    public void setRotation(float amount) {
        float difference = amount - this.getRotation().z;
        super.setRotation(amount);

        for (GUIComponent child : children) {
            float childAmount = child.getRotation().z + difference;
            child.setRotation(childAmount);
        }
    }

    /**
     * Adds a child to the parent
     * @param component the child
     */
    public void addChild(GUIComponent component) {
        children.add(component);
    }
}
