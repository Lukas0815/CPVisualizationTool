package CableTree;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Wire {

    private int length;
    private String type;
    private Cavity[] cavities;  //Start and end cavity

    public Wire(int length, String type, Cavity start, Cavity end){
        this.length = length;
        this.type = type;
        this.cavities = new Cavity[2];
        this.cavities[0] = start;
        this.cavities[1] = end;
    }

    @Override
    public String toString() {
        return "Wire: <" + cavities[0] + "," + cavities[1] + ">";
    }

    public int getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public Cavity[] getCavities() {
        return cavities;
    }

    public void draw(Pane pane) {
        Path path = getWireShape();

        pane.getChildren().add(path);
    }

    public Path getWireShape(){

        Position pos1;
        Position pos2;

        if (cavities[0].getPos().getY() > cavities[1].getPos().getY()){
            pos1 = cavities[0].getMiddlePoint();
            pos2 = cavities[1].getMiddlePoint();
        } else {
            pos1 = cavities[1].getMiddlePoint();
            pos2 = cavities[0].getMiddlePoint();
        }

       return getWireShape(pos1, pos2);
    }

    public Path getWireShape(Position pos1, Position pos2){
        Path path = new Path();

        double w = pos2.getX() - pos1.getX();
        double curveX = pos1.getX() + 0.5 * w;
        double curveY = pos1.getY() + length - w;

        MoveTo moveTo = new MoveTo(pos1.getX(), pos1.getY());

        QuadCurveTo quadToLow = new QuadCurveTo();
        quadToLow.setControlX(curveX);
        quadToLow.setControlY(curveY);
        quadToLow.setX(pos2.getX());
        quadToLow.setY(pos2.getY());

        path.getElements().add(new MoveTo(pos1.getX(), pos1.getY()));
        path.getElements().add(quadToLow);

        path.setStroke(Color.BLACK);
        path.setStrokeWidth(1);

        return path;
    }
}
