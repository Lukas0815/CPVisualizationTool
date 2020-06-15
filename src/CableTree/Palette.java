package CableTree;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

    public void draw(Pane drawPane) {
        Rectangle rec = new Rectangle(pos.getX(), pos.getY(), this.width, this.height);
        drawPane.setPrefSize(this.width, this.height);
        rec.setFill(Color.BLUE);
        rec.setOpacity(0.5);
        drawPane.getChildren().addAll(rec);
        System.out.println("Drawing Pallett: pos: " + pos.toString() + " width: " + width + " height: " +  height);
    }

    public String getName() {
        return name;
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
}
