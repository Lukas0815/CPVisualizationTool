package Constraints;

import CableTree.Cavity;
import CableTree.Wire;

public class BlockingConstraint extends Constraint {

    public BlockingConstraint(Cavity s, Cavity a, Wire w) {
        super(s, a, w);
    }

    @Override
    public String toString() {
        return super.getAffected() + " <| " + super.getSource();
    }
}
