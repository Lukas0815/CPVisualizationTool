package Constraints;

import java.util.List;

public class Conflict {

    private List<Constraint> constraints;

    public Conflict(List<Constraint> constraints){
        this.constraints = constraints;
    }


    public void addConstraint(Constraint c){
        this.constraints.add(c);
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }
}
