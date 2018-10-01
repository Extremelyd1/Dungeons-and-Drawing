package engine.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer class is used to group GUI components at different depths
 */
public class Layer extends GUIComponent {

    private static Layer[] layers = new Layer[20];
    private List<GUIComponent> elements;

    private Layer(int depth) {
        super();
        this.elements = new ArrayList<>();
        this.setDepth(depth / 10.0f);
    }

    /**
     * Singleton constructor that allows only 20 depths. The z should be between
     * -1.0f and 1.0f and therefore, the layer 'index' is limited between 10 and -10
     *
     * @param depth the depth of the layer
     * @return the layer object with the queried depth
     */
    public static Layer getLayer(int depth) {
        if (depth >= 10 || depth <= -10) {
            throw new IllegalArgumentException("engine.gui.Layer.getLayer(): " +
                    depth + " is an invalid depth");
        }

        if (layers[depth + 10] == null) {
            layers[depth + 10] = new Layer(depth);
        }

        return layers[depth + 10];

    }

    private void setDepth(float z) {
        this.getPosition().z = z;
    }

    public void add(GUIComponent element) {
        this.elements.add(element);
        element.getPosition().z = this.getPosition().z;
    }

    public void add(GUIComponent element, int order) {
        this.elements.add(order, element);
        element.getPosition().z = this.getPosition().z;
    }

    public List<GUIComponent> getElements() {
        return elements;
    }
}
