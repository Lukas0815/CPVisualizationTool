package Constraints;

import CableTree.Cavity;
import CableTree.Wire;

public class ChamberTripletConstraint extends Constraint{

    private Cavity source, a1, a2;

    public ChamberTripletConstraint(Cavity source, Cavity a1, Cavity a2){
        super(source, a1, null);
        this.source = source;
        this.a1 = a1;
        this.a2 = a2;
    }

    @Override
    public String toString() {
        return a1 + " < " + source + "\t" + a2 + " < " + source +"\n Chamber Triplet";
    }
}
