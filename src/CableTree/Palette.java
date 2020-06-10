package CableTree;

public class Palette {

    private String name;
    private Position pos;
    private double width, height;

    public Palette(String name, double x, double y, double width, double height){
        this.name = name;
        this.pos = new Position(x,y);
        this.width = width;
        this.height = height;
    }
}
