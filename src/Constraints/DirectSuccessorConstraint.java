package Constraints;

import CableTree.Cavity;
import CableTree.Wire;

public class DirectSuccessorConstraint extends Constraint{
    public DirectSuccessorConstraint(Cavity s, Cavity a, Wire w) {
        super(s, a, w);
    }

    @Override
    public String toString() {
        return super.toString() + "\n Direct Successor";
    }
}
