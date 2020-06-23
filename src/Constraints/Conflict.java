package Constraints;

import CableTree.Cavity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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


    /*
    Will return a circle (List in order) found in the conflict or an empty list. If there are more than one circle,
    this will only return one circle.
     */
    public List getCircle(Constraint startConstraint){
        List circle = new LinkedList<Constraint>();
        findCirlce(startConstraint.getAffected(), startConstraint.getAffected(), circle);
        Collections.reverse(circle);
        return circle;
    }

    /*
    Will return a circle found in the conflict. If there are more than one circle,
    this will only return one circle.
     */
    private Constraint findCirlce(Cavity right, Cavity goal, List circleList){

        List<Constraint> succs = findSuccConstraint(right);

        //no successor found? The circle search ends here
        if (succs == null || succs.isEmpty())
            return null;

        //for each successor a circle might exists, so split the search
        for (Constraint s : succs){
            if (s.getAffected() == goal){
                circleList.add(s);
                return s;
            }

            Constraint found = findCirlce(s.getAffected(), goal, circleList);
            if (found != null) {
                circleList.add(s);
                return s;
            }

        }

        return null;
    }

    /*
    Given a cavity that belongs to a constraint in this conflict, this will return
    a list of all constraints in this conflict that start with this cavity
    (or an empty list if none could be found)
     */
    private List<Constraint> findSuccConstraint(Cavity right) {
        List<Constraint> succs = new LinkedList<Constraint>();

        for (Constraint c : constraints){
            if (c.getSource() == right){
                succs.add(c);
            }
        }

        return succs;
    }
}
