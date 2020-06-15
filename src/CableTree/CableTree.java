package CableTree;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.util.LinkedList;
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
    private List<Shape> heats;

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

    public void computeHeatMap(){
        this.heats = new LinkedList<>();

        //Draw blocking cable constraints
        for (Cavity c : cavities){
            if (!c.getActive()) continue; //TODO: Eigentlich ist das ja unnötig, sollte nicht alles angezeigt werden?

            //heats.add(c.drawBlocking(palette));
            //heats.add(c.drawDiagonallyClose());
            heats.add(c.drawShortOneSided(palette));
        }
    }

    public void drawHeatMap(Pane drawPane) {
        computeHeatMap();
        drawPane.getChildren().addAll(this.heats);
    }

    public void hideHeatMap(Pane drawPane) {
        drawPane.getChildren().removeAll(this.heats);
    }


}
