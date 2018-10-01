package engine.gui;

import java.util.ArrayList;
import java.util.List;

public class Layer extends GUIComponent {

    private static Layer[] layers = new Layer[20];
    private List<GUIComponent> elements;

    private Layer(int depth) {
        super();
        this.elements = new ArrayList<>();
        this.setDepth(depth / 10.0f);
    }

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
    }

    public void add(GUIComponent element, int order) {
        this.elements.add(order, element);
    }

    public List<GUIComponent> getElements() {
        return elements;
    }
}
