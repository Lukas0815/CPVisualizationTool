package CableTree;

import Input.XMLParser;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
        this.angle = angle/60; //Arcminutes to degree
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

    public void draw(Pane drawPane) {
        Rectangle rec = new Rectangle(pos.getX(), pos.getY(), this.width, this.height);
        Text cavityName = new Text(this.name);
        int fontsize = 2 * XMLParser.scale;
        cavityName.setFont(new Font(fontsize));
        cavityName.setX(pos.getX() + width/4);
        cavityName.setY(pos.getY() + height - fontsize/2);
        rec.setFill(Color.GRAY);
        rec.setStroke(Color.BLACK);
        rec.setOpacity(1);
        System.out.println("Drawing cavity! at pos: " + pos + " width: " +  width + " height: " + height);
        drawPane.getChildren().addAll(rec, cavityName);
    }
}
