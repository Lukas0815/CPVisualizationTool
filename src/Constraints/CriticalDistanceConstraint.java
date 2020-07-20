package Constraints;

import CableTree.Cavity;
import CableTree.Palette;
import CableTree.Wire;
import Input.ColorScheme;
import javafx.scene.layout.Pane;

public class CriticalDistanceConstraint extends Constraint{
    public CriticalDistanceConstraint(Cavity a, Cavity b, Cavity c, Cavity d, Wire w) {
        //represents NOT(A<B AND C<D)
        super(a,b,w); //TODO: useless, create real constructor
    }

    @Override
    public String toString() {
        return super.toString() + "\n Critical Distance";
    }

    @Override
    public void draw(Pane pane, Palette p, ColorScheme cs) {
        //TODO
    }

    @Override
    public void hide(Pane pane) {
        //TODO
    }
}
