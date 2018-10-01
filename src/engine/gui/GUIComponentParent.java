package engine.gui;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GUIComponentParent extends GUIComponent {

    private List<GUIComponent> children;

    public GUIComponentParent() {
        super();
        children = new ArrayList<>();
    }

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

    /* Sets the scale */
    public void setScale(float scale) {
        float difference = scale / this.getScale();
        super.setScale(scale);

        for (GUIComponent child : children) {
            float childScale = child.getScale() * difference;
            child.setScale(childScale);
        }
    }

    /* Sets the rotation */
    public void setRotation(float amount) {
        float difference = amount - this.getRotation().z;
        super.setRotation(amount);

        for (GUIComponent child : children) {
            float childAmount = child.getRotation().z + difference;
            child.setRotation(childAmount);
        }
    }

    public void addChild(GUIComponent component) {
        children.add(component);
    }
}
