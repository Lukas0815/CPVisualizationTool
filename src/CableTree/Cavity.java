package CableTree;

public class Cavity {

    private String name;
    private Position pos;
    private double width, height;
    private int angle;

    public Cavity(String name, double x, double y, double width, double height, int angle){
        this.name = name;
        this.pos = new Position(x,y);
        this.width = width;
        this.height = height;
    }

    public Position getPos() {
        return pos;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getAngle() {
        return angle;
    }
}
