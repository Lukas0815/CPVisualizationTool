package CableTree;

import Input.XMLParser;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    public Cavity getCavity(String name){

        //TODO: looping through is not optimal
        for (Cavity c : cavities){
            if(c.getName().equals(name)){
                return c;
            }
        }

        return null;
    }

    public void draw(Pane drawPane) {
        Text housingName = new Text(this.name);
        int fontsize = 2 * XMLParser.scale;
        housingName.setFont(new Font(fontsize));
        housingName.setX(pos.getX());
        housingName.setY(pos.getY());

        Rectangle rec = new Rectangle(pos.getX(), pos.getY(), this.width, this.height);
        rec.setFill(Color.RED);
        rec.setOpacity(1);

        rec.getTransforms().add(new Rotate(angle, pos.getX()+width/2, pos.getY()+height/2));

        drawPane.getChildren().addAll(rec, housingName);
    }
}
