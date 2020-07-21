package Constraints;

import CableTree.Cavity;
import CableTree.Palette;
import CableTree.Wire;
import Input.ColorScheme;
import javafx.scene.layout.Pane;

public class CriticalDistanceConstraint extends Constraint{

    private Cavity a, b, c, d;
    private Wire w;
    private String type;

    public CriticalDistanceConstraint(Cavity a, Cavity b, Cavity c, Cavity d, Wire w) {
        //represents NOT(A<B AND C<D)
        super(a,b,w);
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.w = w;
        this.type = "C1";
    }

    public CriticalDistanceConstraint(Cavity a, Cavity b, Wire w){
        super(a,b,w);
        this.a = a;
        this.b = b;
        this.w = w;
        this.type = "C2";
    }

    @Override
    public String toString() {
        String preString;

        if (type.equals("C2"))
            preString = super.toString();
        else
            preString = "NOT("+ this.a + "<" + this.b + " AND " + this.c + "<" + this.d + ")";

        return preString + "\n Critical Distance \t Type " + type;
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
