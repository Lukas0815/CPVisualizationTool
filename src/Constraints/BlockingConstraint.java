package Constraints;

import CableTree.Cavity;
import CableTree.Palette;
import CableTree.Wire;
import Input.ColorScheme;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.util.LinkedList;
import java.util.List;

public class BlockingConstraint extends Constraint {

    private List<Shape> shapes;

    public BlockingConstraint(Cavity s, Cavity a, Wire w) {
        super(s, a, w);
        this.shapes = new LinkedList<>();
    }

    @Override
    public String toString() {
        return super.toString() + "\n Blocking";
    }

    @Override
    public void draw(Pane pane, Palette p, ColorScheme cs) {
        Shape blockingSource = super.getSource().getBlockingArea(p);
        Shape blockingAffected = super.getAffected().getBlockingArea(p);
        //Shape wireShape = super.getW().getWireShape();

        blockingSource.setOpacity(cs.getHeatOpacity());
        blockingSource.setFill(cs.getBlockingColor());

        blockingAffected.setOpacity(cs.getHeatOpacity());
        blockingAffected.setFill(cs.getBlockingColor());

        shapes.add(blockingSource);
        shapes.add(blockingAffected);
        //shapes.add(wireShape);

        pane.getChildren().addAll(blockingSource, blockingAffected/*, wireShape*/);
    }

    @Override
    public void hide(Pane pane) {
        pane.getChildren().removeAll(this.shapes);
    }

}
