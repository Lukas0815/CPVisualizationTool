package Constraints;

import CableTree.Cavity;
import CableTree.Palette;
import CableTree.Wire;
import Input.ColorScheme;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class DiagonallyCloseConstraint extends Constraint {

    private Shape drawingShape;

    public DiagonallyCloseConstraint(Cavity s, Cavity a, Wire w) {
        super(s, a, w);
        this.drawingShape = super.getSource().getDiagonallyCloseArea();
    }

    @Override
    public String toString() {
        return super.toString() + "\n Diagonally Close";
    }

    @Override
    public void draw(Pane pane, Palette p, ColorScheme cs) {
        //Draw area of source cavity that affects other cavity and leads to this constraint
        this.drawingShape.setOpacity(cs.getHeatOpacity());
        this.drawingShape.setFill(cs.getDiagonalColor());
        pane.getChildren().add(drawingShape);
    }

    @Override
    public void hide(Pane pane) {
        pane.getChildren().remove(this.drawingShape);
    }
}
