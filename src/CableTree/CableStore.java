package CableTree;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class CableStore {

    private double x, y;
    private Shape drawShape;

    public CableStore(double x, double y){
        this.x = x;
        this.y = y;
        this.drawShape = new Circle(x, y, 10);
        drawShape.setFill(Color.GREEN);
    }

    public void draw(Pane drawpane){
        drawpane.getChildren().add(this.drawShape);
    }

    public void hide(Pane drawpane){
        drawpane.getChildren().remove(this.drawShape);
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public Position getPos() {
        return new Position(x, y);
    }
}
