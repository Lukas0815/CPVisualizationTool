package CableTree;

import Input.Parameters;
import Input.XMLParser;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.math.*;

import static java.lang.Math.tan;

public class Cavity {

    private String name;
    private Position pos;
    private double width, height;
    private int angle;
    private boolean active;

    public Cavity(String name, double x, double y, double width, double height, int angle){
        this.name = name;
        this.pos = new Position(x,y);
        this.width = width;
        this.height = height;
        this.angle = angle/60; //Arcminutes to degree
        this.active = false;
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

    public void setActive(boolean isActive){
        this.active = isActive;
    }

    public boolean getActive(){
        return this.active;
    }

    public String getName() {
        return name;
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
        if(active){
            rec.setOpacity(1);
        } else {
            rec.setOpacity(0.3);
        }

        drawPane.getChildren().addAll(rec, cavityName);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Shape drawBlocking(Palette p) {
        //We will use a polygon to show the affected area by the blocking constraint
        Polygon blockArea = new Polygon();
        double x = pos.getX() + width /2;
        double y = pos.getY() + height/2;
        double h = p.getHeight();

        double tanAlpha = tan(Parameters.blockingAngle);
        double h_alpha = x * tanAlpha;

        blockArea.getPoints().addAll(new Double[]{
                x, y,
                x+ Parameters.blockingH, y,
                x+ Parameters.blockingH, h,
                0.0, h,
                h_alpha, h, //not true, needs calculation with angle
        });

        blockArea.setOpacity(0.3);
        blockArea.setFill(Color.GRAY);

        return blockArea;
    }

    public Shape drawDiagonallyClose(){
        Rectangle rec = new Rectangle();
        double x = this.pos.getX()+width/2;
        double y = this.pos.getY()+height/2;
        double h = Parameters.diagH;
        double v = Parameters.diagV;
        rec.setX(x-h);
        rec.setY(y);
        rec.setWidth(2 *h);
        rec.setHeight(v);

        return rec;
    }

    public Shape drawShortOneSided(Palette p){

        //We will use a polygon to show the affected area
        Polygon area = new Polygon();
        double x = pos.getX() + width /2;
        double y = pos.getY() + height/2;
        double tanBeta = Math.tan(Parameters.shortAngle);
        double h_beta_left = tanBeta * x;
        double h_beta_right = tanBeta * (p.getWidth()-x);

        area.getPoints().addAll(
                x, y,
                0.0, (y + h_beta_left),
                p.getWidth(), (y + h_beta_right)
        );


        System.out.println(area.getPoints());

        area.setFill(Color.BLACK);
        area.setOpacity(1);
        return area;
    }
}
