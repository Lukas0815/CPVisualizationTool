package Constraints;

import CableTree.Cavity;
import CableTree.Wire;

public class ChamberTripletConstraint extends Constraint{
    public ChamberTripletConstraint(Cavity s, Cavity a, Wire w) {
        super(s, a, w);
    }

    @Override
    public String toString() {
        return super.toString() +"\n Chamber Triplet";
    }
}
