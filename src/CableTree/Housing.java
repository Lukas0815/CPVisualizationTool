package CableTree;

import java.util.List;

public class Housing {

    private String name;
    private String type;
    private Position pos;
    private double width, height;
    private List<Cavity> cavities;

    public Housing (String name, String type, double x, double y, double width, double height, List<Cavity> cavities){
        this.name = name;
        this.type = type;
        this.pos = new Position(x,y);
        this.width = width;
        this.height = height;
        this.cavities = cavities;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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

    public List<Cavity> getCavities() {
        return cavities;
    }
}
