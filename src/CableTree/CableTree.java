package CableTree;

import Constraints.*;
import Input.ColorScheme;
import Input.Parameters;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.*;

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
    private List<Constraint> constraints;
    private Map<Cavity, Integer> cavFreqMap;
    private List<Conflict> conflicts;

    public CableTree(Palette palette, List<Housing> housings, List<Cavity> cavities, List<Wire> wires){
        this.palette = palette;
        this.housings = housings;
        this.cavities = cavities;
        this.wires = wires;
        this.colorScheme = new ColorScheme();
        this.heatmapPrintFlags = new boolean[5];
        this.constraints = computeConstraints();

        this.cavFreqMap = new HashMap();
        this.conflicts = new LinkedList<>();
        makeFreqMap();
    }

    public void drawToPanel(Pane drawPane, boolean statMode) {
        //Draw the pallet first
        palette.draw(drawPane);

        //Draw housings
        for (Housing h : housings){
            h.draw(drawPane);
        }

        for (Cavity c : cavities){

            if (statMode){
                c.drawStats(drawPane, cavFreqMap.get(c));
            } else {
                c.draw(drawPane);
            }

        }

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

    private List<Shape> computeDiagonallyClose() {
        List<Shape> areas = new LinkedList<>();

        for (Cavity c : cavities){
            Rectangle rec = c.getDiagonallyCloseArea();

            rec.setOpacity(colorScheme.getHeatOpacity());
            rec.setFill(colorScheme.getDiagonalColor());

            areas.add(rec);
        }

        return areas;
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
            Shape blockArea = c.getBlockingArea(p);

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

            Polygon area = w.getCavities()[0].getShortOneSidedArea(w);

            area.setFill(colorScheme.getShortColor());
            area.setOpacity(colorScheme.getHeatOpacity());

            areas.add(area);

        }

        return areas;
    }

    public List<Shape> computeChamberTripletAreas(){
        List<Shape> areas = new LinkedList<>();

        for (Cavity c: cavities){
            Rectangle rec = c.getChamberTripletArea();

            rec.setFill(colorScheme.getChamberColor());
            rec.setOpacity(colorScheme.getHeatOpacity());

            areas.add(rec);
        }


        return areas;
    }

    public void setHeatmapPrintFlag(int i, boolean b) {
        assert(i >= 0 && i < heatmapPrintFlags.length);

        this.heatmapPrintFlags[i] = b;
    }

    /*
    Will add Constraint objects to a list based on the Shapes computed for the
    constraints in e.g. the heatmap

    Note: Implemented very inefficiently but simple to understand!
     */
    private List<Constraint> computeConstraints(){
        List<Constraint> constraints = new LinkedList<>();

        //blocking constraints
        for (Cavity source : cavities){
            //Only check for used cavities
           /* if (!source.getActive())
                continue;
             */

            for (Cavity c : cavities){
                //if (!c.getActive()) continue;
                if (source.equals(c)) continue; //Do not compare with itself!
                if (c.getPos().getY() > source.getPos().getY()) continue; //Optimization, since only cavities below are blocked

                //at the moment checks for the cavity point (middle point) --> TODO: check if a whole rectangle needs to be checked instead of point
                boolean isAffected = source.getBlockingArea(palette).contains(c.getMiddlePoint());

                if (isAffected){
                    constraints.add(new BlockingConstraint(source, c, null));
                }
            }
        }

        //diagonally close constraints
        for (Cavity source : cavities){
            for (Cavity c : cavities){
                boolean isAffected = source.getDiagonallyCloseArea().contains(c.getMiddlePoint());

                if (isAffected){
                    constraints.add(new DiagonallyCloseConstraint(source, c, null));
                }
            }
        }

        //short one-sided constraints
        for (Wire w : wires){

            if (w.getLength() > Parameters.shortL) continue; //only respect short cables
            Cavity source = w.getCavities()[0];

            for (Cavity c : cavities){
                boolean isAffected = source.getShortOneSidedArea(w).contains(c.getMiddlePoint());

                if (isAffected){
                    constraints.add(new ShortOneSidedConstraint(source, c, w));
                }
            }

        }
/*
        //Critical Distance
        for (Cavity source : cavities){
            for (Cavity c : cavities){
                boolean isAffected = source.getDiagonallyCloseArea().contains(c.getMiddlePoint());

                if (isAffected){
                    constraints.add(new CriticalDistanceConstraint(source, c, null));
                }
            }
        }


 */

        return constraints;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public List<Housing> getHousings() {
        return housings;
    }

    public List<Cavity> getCavities() {
        return cavities;
    }

    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    private void makeFreqMap(){
        for (Constraint c : constraints){
            Cavity cav1 = c.getSource();
            Cavity cav2 = c.getAffected();

            if (!this.cavFreqMap.containsKey(cav1)){
                this.cavFreqMap.put(cav1, 1);
            } else {
                this.cavFreqMap.put(cav1, this.cavFreqMap.get(cav1) +1);
            }

            if (!this.cavFreqMap.containsKey(cav2)){
                this.cavFreqMap.put(cav2, 1);
            } else {
                this.cavFreqMap.put(cav2, this.cavFreqMap.get(cav2) +1);
            }

        }

        System.out.println(this.cavFreqMap);
    }

    public Map<Cavity, Integer> getCavFreqMap() {
        return cavFreqMap;
    }

    public void addConflict(Conflict c){
        this.conflicts.add(c);
    }

    public List<Conflict> getConflicts(){
        return this.conflicts;
    }
}
