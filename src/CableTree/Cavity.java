package CableTree;

import Input.ColorScheme;
import Input.Parameters;
import Input.XMLParser;
import javafx.geometry.Point2D;
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
    private Housing housing;

    public Cavity(String name, double x, double y, double width, double height, int angle){
        this.name = name;
        this.pos = new Position(x,y);
        this.width = width;
        this.height = height;
        this.angle = angle/60; //Arcminutes to degree
        this.active = false;
    }

    public Cavity(String name, double x, double y, double width, double height, int angle, Housing h){
        this.name = name;
        this.pos = new Position(x,y);
        this.width = width;
        this.height = height;
        this.angle = angle/60; //Arcminutes to degree
        this.active = false;
        this.housing = h;
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
        return this.housing.getName() + ":" + this.name;
    }


    public Polygon getBlockingArea(Palette p) {
        Polygon blockArea = new Polygon();
        double x = this.getPos().getX() + this.getWidth() /2;
        double y = this.getPos().getY() + this.getHeight()/2;
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

        return blockArea;
    }

    public Rectangle getDiagonallyCloseArea() {
        Rectangle rec = new Rectangle();
        rec.setX(this.pos.getX() + this.width/2 - Parameters.diagH);
        rec.setY(this.pos.getY() + this.height/2);
        rec.setWidth(Parameters.diagH *2);
        rec.setHeight(Parameters.diagV);

        return rec;
    }

    public Polygon getShortOneSidedArea(Wire w){

        //We will use a polygon to show the affected area
        Polygon area = new Polygon();
        double x = getPos().getX() + getWidth() /2;
        double y = getPos().getY() + getHeight()/2;
        double tanBeta = Math.tan(Parameters.shortAngle);
        double h_beta_left = tanBeta * w.getLength();
        double h_beta_right = tanBeta * w.getLength();

        area.getPoints().addAll(
                x, y,
                x - w.getLength(), (y + h_beta_left),
                x + (double) w.getLength(), (y + h_beta_right)
        );

        return area;
    }

    public Rectangle getChamberTripletArea(){

        Rectangle rec = new Rectangle();

        //compute the points
        rec.setX(getPos().getX() + getWidth()/2 - Parameters.blockingH);
        rec.setY(getPos().getY() + getHeight()/2);
        //TODO: the height is not the correct parameter for chamber triplet
        rec.setWidth(Parameters.blockingH * 2);
        rec.setHeight(Parameters.blockingH);

        return rec;
    }

    public Point2D getMiddlePoint() {
        return new Point2D(this.pos.getX() + this.width/2, this.pos.getY() + this.height/2);
    }

    public Housing getHousing() {
        return housing;
    }
}
