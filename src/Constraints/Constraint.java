package Constraints;

import CableTree.Cavity;
import CableTree.Position;
import CableTree.Wire;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import sample.Arrow;

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
        return affected + " < " + source;
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

    public Shape drawOrderArrow(Pane drawPane) {
        Position sPos = source.getPos();
        Position aPos = affected.getPos();
        double sx = sPos.getX() + source.getWidth()/2;
        double ax = aPos.getX() + affected.getWidth()/2;
        double sy, ay;

        Arrow arrow;

        if (sPos.getY() < aPos.getY()){
            sy = sPos.getY() + source.getHeight();
            ay = aPos.getY();
        } else {
            sy = sPos.getY();
            ay = aPos.getY() + affected.getHeight();
        }

        arrow = new Arrow(sx, sy, ax, ay);

        return arrow;
    }
}
