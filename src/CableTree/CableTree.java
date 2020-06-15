package CableTree;

import Input.ColorScheme;
import Input.Parameters;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.tan;

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
    private ColorScheme colorScheme;
    private boolean[] heatmapPrintFlags;

    public CableTree(Palette palette, List<Housing> housings, List<Cavity> cavities, List<Wire> wires){
        this.palette = palette;
        this.housings = housings;
        this.cavities = cavities;
        this.wires = wires;
        this.colorScheme = new ColorScheme();
        this.heatmapPrintFlags = new boolean[5];
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

            //heats.add(c.drawBlocking(palette, colorScheme));
            //heats.add(c.drawDiagonallyClose(palette, colorScheme));
            //heats.add(c.drawShortOneSided(palette, colorScheme));
        }

        //heats.addAll(computeshortOneSidedAreas());
        heats.addAll(computeChamberTripletAreas());
    }

    public void drawHeatMap(Pane drawPane) {
        if (this.heats != null) drawPane.getChildren().removeAll(this.heats);

        this.heats = new LinkedList<>();

        if (this.heatmapPrintFlags[0])
            this.heats.addAll(computeBlockingAreas(palette));
        if (this.heatmapPrintFlags[1])
            this.heats.addAll(computeDiagonallyClose());
        if (this.heatmapPrintFlags[2])
            this.heats.addAll(computeshortOneSidedAreas());
        if (this.heatmapPrintFlags[3])
            this.heats.addAll(computeChamberTripletAreas());
        //if (this.heatmapPrintFlags[4]) //Critical Distance
        //if (this.heatmapPrintFlags[5]  //Direct successor

        drawPane.getChildren().addAll(this.heats);
    }

    private Collection<Shape> computeDiagonallyClose() {
        return null;
    }

    public void hideHeatMap(Pane drawPane) {
        drawPane.getChildren().removeAll(this.heats);
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public List<Shape> computeBlockingAreas(Palette p){
        List<Shape> areas = new LinkedList<>();

        for (Cavity c : cavities){
            //We will use a polygon to show the affected area by the blocking constraint
            Polygon blockArea = new Polygon();
            double x = c.getPos().getX() + c.getWidth() /2;
            double y = c.getPos().getY() + c.getHeight()/2;
            double h = p.getHeight();

            double tanAlpha = tan(Parameters.blockingAngle);
            double h_alpha = x * tanAlpha;

            blockArea.getPoints().addAll(new Double[]{
                    x, y,
                    x+ Parameters.blockingH, y,
                    x+ Parameters.blockingH, h,
                    0.0, h,
                    h_alpha, h, //not true, needs calculation with angle
            });

            blockArea.setOpacity(colorScheme.getHeatOpacity());
            blockArea.setFill(colorScheme.getBlockingColor());

            areas.add(blockArea);
        }


        return areas;
    }

    public List<Shape> computeshortOneSidedAreas(){

        List<Shape> areas = new LinkedList<>();

        for (Wire w : wires){
            //only look at short cables
            if (w.getLength() > Parameters.shortL) continue;

            Cavity c = w.getCavities()[0];

            //We will use a polygon to show the affected area
            Polygon area = new Polygon();
            double x = c.getPos().getX() + c.getWidth() /2;
            double y = c.getPos().getY() + c.getHeight()/2;
            double tanBeta = Math.tan(Parameters.shortAngle);
            double h_beta_left = tanBeta * w.getLength();
            double h_beta_right = tanBeta * w.getLength();

            area.getPoints().addAll(
                    x, y,
                    x - w.getLength(), (y + h_beta_left),
                    x + (double) w.getLength(), (y + h_beta_right)
            );


            System.out.println(area.getPoints() + " length of cable: " + w.getLength());

            area.setFill(colorScheme.getShortColor());
            area.setOpacity(colorScheme.getHeatOpacity());

            areas.add(area);

        }

        return areas;
    }

    public List<Shape> computeChamberTripletAreas(){
        List<Shape> areas = new LinkedList<>();

        for (Cavity c: cavities){
            Rectangle rec = new Rectangle();
            rec.setFill(colorScheme.getChamberColor());
            rec.setOpacity(colorScheme.getHeatOpacity());

            //compute the points
            rec.setX(c.getPos().getX() + c.getWidth()/2 - Parameters.blockingH);
            rec.setY(c.getPos().getY() + c.getHeight()/2);
            rec.setWidth(Parameters.blockingH * 2);
            rec.setHeight(Parameters.blockingH);

            areas.add(rec);
        }


        return areas;
    }

    public void setHeatmapPrintFlag(int i, boolean b) {
        assert(i >= 0 && i < heatmapPrintFlags.length);

        this.heatmapPrintFlags[i] = b;
    }
}
