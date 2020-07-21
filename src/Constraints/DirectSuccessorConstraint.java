package Constraints;

import CableTree.Cavity;
import CableTree.Palette;
import CableTree.Position;
import CableTree.Wire;
import Input.ColorScheme;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import sample.Arrow;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class DirectSuccessorConstraint extends Constraint{

    private Position cableStorePos;
    private Shape drawShape, arrowS, arrowA;

    public DirectSuccessorConstraint(Cavity s, Cavity a, Wire w, Position cableStorePos) {
        super(s, a, w);
        this.cableStorePos = cableStorePos;

        this.drawShape = new Circle(cableStorePos.getX(), cableStorePos.getY(), w.getLength());

        this.arrowA = computeArrow(a.getMiddlePoint(), cableStorePos, w.getLength());
        this.arrowS = computeArrow(s.getMiddlePoint(), cableStorePos, w.getLength());
    }

    private Arrow computeArrow(Position cavPos, Position cableStorePos, double wirelength){
        /* Calculate the arrows end */
        //Calculate alpha angle
        double distanceS = pythagorasDistance(cavPos, cableStorePos);
        double ankathete = Math.abs(cableStorePos.getX() - cavPos.getX());
        double alpha = Math.acos(ankathete / distanceS);

        //calculate sides
        double hypothenuse = Math.abs(distanceS - wirelength);
        double height = cavPos.getY() <= cableStorePos.getY() ? Math.sin(alpha) * hypothenuse : Math.sin(alpha) * hypothenuse * -1;
        double width = cavPos.getX() <= cableStorePos.getX() ? Math.cos(alpha) * hypothenuse : Math.cos(alpha) * hypothenuse * -1;

        return new Arrow(cavPos.getX(), cavPos.getY(), cavPos.getX() + width, cavPos.getY() + height);
    }

    @Override
    public String toString() {
        return super.toString() + "\n Direct Successor";
    }

    @Override
    public void draw(Pane pane, Palette p, ColorScheme cs) {
        this.drawShape.setFill(cs.getDirectColor());
        this.drawShape.setOpacity(cs.getHeatOpacity());
        this.arrowA.setFill(cs.getDirectColor());
        this.arrowS.setFill(cs.getDirectColor());

        pane.getChildren().add(this.drawShape);
        pane.getChildren().add(this.arrowA);
        pane.getChildren().add(this.arrowS);
    }

    @Override
    public void hide(Pane pane) {
        pane.getChildren().remove(this.drawShape);
        pane.getChildren().remove(this.arrowA);
        pane.getChildren().remove(this.arrowS);
    }

    public Collection<Shape> getShapes() {
        return Arrays.asList(this.drawShape, this.arrowA, this.arrowS);
    }

    public Shape getMoveArea(){
        return this.drawShape;
    }

    public Collection<Shape> getArrows(){
        return Arrays.asList(this.arrowA, this.arrowS);
    }

    //Gives distance betweent two points using pythagoras
    private double pythagorasDistance(Position pos_a, Position pos_b){
        double ax = pos_a.getX();
        double ay = pos_a.getY();
        double bx = pos_b.getX();
        double by = pos_b.getY();


        double a = ax < bx ? bx - ax : ax - bx;
        double b = ay < by ? by - ay : ay - by;

        return Math.sqrt((a*a) + (b*b));
    }
}
