package Constraints;

import CableTree.Cavity;
import CableTree.Wire;

public class DiagonallyCloseConstraint extends Constraint {


    public DiagonallyCloseConstraint(Cavity s, Cavity a, Wire w) {
        super(s, a, w);
    }

    @Override
    public String toString() {
        return super.toString() + "\n Diagonally Close";
    }
}
