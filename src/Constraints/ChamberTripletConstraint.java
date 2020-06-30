package Constraints;

import CableTree.Cavity;
import CableTree.Palette;
import CableTree.Wire;
import Input.ColorScheme;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class ChamberTripletConstraint extends Constraint{

    private Cavity source, a1, a2;
    private Shape drawingShape;

    public ChamberTripletConstraint(Cavity source, Cavity a1, Cavity a2){
        super(source, a1, null);
        this.source = source;
        this.a1 = a1;
        this.a2 = a2;
        this.drawingShape = source.getChamberTripletArea();
    }

    @Override
    public String toString() {
        return a1 + " < " + source + "\t" + a2 + " < " + source +"\n Chamber Triplet";
    }

    @Override
    public void draw(Pane pane, Palette p, ColorScheme cs) {
        //Draw the blocking area that affects the two neighbouring cavities leading to this constraint
        drawingShape.setFill(cs.getChamberColor());
        drawingShape.setOpacity(cs.getHeatOpacity());
        pane.getChildren().add(this.drawingShape);
    }

    @Override
    public void hide(Pane pane) {
        pane.getChildren().remove(this.drawingShape);
    }
}
