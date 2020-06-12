package CableTree;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.List;

/*
This class is a model of the cable tree given via an xml file. A CableTree consists of a board on which
several housings are placed. Each housing has cavities that can be connected via wires.
 */
public class CableTree {

    private String xmlPath;

    private List<Housing> housings;
    private List<Cavity> cavities;
    private List<Wire> wires;
    private Palette palette;

    public CableTree(Palette palette, List<Housing> housings, List<Cavity> cavities, List<Wire> wires){
        this.palette = palette;
        this.housings = housings;
        this.cavities = cavities;
        this.wires = wires;
    }

    public void drawToPanel(Pane drawPane) {
        //Draw the pallet first
        palette.draw(drawPane);

        //Draw housings
        for (Housing h : housings){
            h.draw(drawPane);
        }

        for (Cavity c : cavities){
            c.draw(drawPane);
        }
    }
}
