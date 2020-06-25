package Constraints;

import CableTree.Cavity;
import CableTree.Wire;

public abstract class Constraint {

    private Cavity source;
    private Cavity affected;
    private Wire w;

    public Constraint(Cavity s, Cavity a, Wire w){
        this.source = s;
        this.affected = a;
        this.w = w;
    }

    public String toString(){
        return source + " < " + affected;
    }

    public Cavity getSource() {
        return source;
    }

    public Cavity getAffected() {
        return affected;
    }

    public Wire getW() {
        return w;
    }
}
