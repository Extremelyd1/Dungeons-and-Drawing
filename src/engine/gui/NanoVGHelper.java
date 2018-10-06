package engine.gui;

public class NanoVGHelper {

    private static NanoVGHelper ourInstance = new NanoVGHelper();

    public static NanoVGHelper getInstance() {
        return ourInstance;
    }

    private NanoVGHelper() {

    }
}
