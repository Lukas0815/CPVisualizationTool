package CableTree;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.List;

public class Housing {

    private String name;
    private String type;
    private Position pos;
    private double width, height;
    private int angle;
    private List<Cavity> cavities;

    public Housing (String name, String type, double x, double y, double width, double height, List<Cavity> cavities, int angle){
        this.name = name;
        this.type = type;
        this.pos = new Position(x,y);
        this.width = width;
        this.height = height;
        this.angle = angle / 60; //Arcminutes to degree
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

    public void draw(Pane drawPane) {
        Rectangle rec = new Rectangle(pos.getX(), pos.getY(), this.width, this.height);
        rec.setFill(Color.RED);
        rec.setOpacity(1);
        rec.getTransforms().add(new Rotate(angle, pos.getX(), pos.getY()));
        if (angle != 0)
            rec.setX(pos.getX()-height);
        System.out.println("Angle: " + this.angle);
        System.out.println("Drawing rec: x: " + pos.getX() + " y: " + pos.getY() + " width: " + width + " height: " + height);
        drawPane.getChildren().add(rec);
    }
}
