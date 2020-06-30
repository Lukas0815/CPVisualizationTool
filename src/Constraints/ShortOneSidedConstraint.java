package Constraints;

import CableTree.Cavity;
import CableTree.Palette;
import CableTree.Wire;
import Input.ColorScheme;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class ShortOneSidedConstraint extends Constraint{
    private Shape drawingShape;

    public ShortOneSidedConstraint(Cavity s, Cavity a, Wire w) {
        super(s, a, w);
        this.drawingShape = super.getSource().getShortOneSidedArea(w);
    }

    @Override
    public String toString() {
        return super.toString() + "\n Short One-Sided" ;
    }

    @Override
    public void draw(Pane pane, Palette p, ColorScheme cs) {
        this.drawingShape.setOpacity(cs.getHeatOpacity());
        this.drawingShape.setFill(cs.getShortColor());
        pane.getChildren().add(this.drawingShape);
    }

    @Override
    public void hide(Pane pane) {
        pane.getChildren().remove(this.drawingShape);
    }
}
