package engine.gui;

public class RGBA {

    public int r;
    public int g;
    public int b;
    public int a;

    public RGBA(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
    }

    public RGBA(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

}
