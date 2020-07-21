package CableTree;

import Constraints.*;
import Input.ColorScheme;
import Input.Parameters;
import javafx.geometry.Point2D;
import javafx.scene.Group;
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
    private List<Cavity> cavities, activeCavities;
    private List<Wire> wires;
    private Palette palette;
    private List<Shape> heats;
    private ColorScheme colorScheme;
    private boolean[] heatmapPrintFlags;
    private List<Constraint> constraints;
    private Map<Cavity, Integer> cavFreqMap;
    private List<Conflict> conflicts;
    private CableStore cableStore;

    public CableTree(Palette palette, List<Housing> housings, List<Cavity> cavities, List<Wire> wires, List<Cavity> activeCavities){
        this.palette = palette;
        this.housings = housings;
        this.cavities = cavities;
        this.wires = wires;
        this.colorScheme = new ColorScheme();
        this.heatmapPrintFlags = new boolean[6];

        this.cavFreqMap = new HashMap();
        this.conflicts = new LinkedList<>();
        this.activeCavities = activeCavities;
        this.cableStore = new CableStore(Parameters.zetaCableStorePosition.getX(), Parameters.zetaCableStorePosition.getY());

        this.constraints = computeConstraints();
        makeFreqMap();

    }

    public void drawToPanel(Pane drawPane, boolean statMode, boolean drawWires) {

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

        if (drawWires){
            for (Wire w : wires){
                w.draw(drawPane);
            }
        }

        this.cableStore.draw(drawPane);
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
        if (this.heatmapPrintFlags[5])  //Direct successor
            this.heats.addAll(computeDirectSuccAreas());

        drawPane.getChildren().addAll(this.heats);
    }

    private List<Shape> computeDiagonallyClose() {
        List<Shape> areas = new LinkedList<>();

        for (Cavity c : activeCavities){
            Rectangle rec = c.getDiagonallyCloseArea();

            rec.setOpacity(colorScheme.getHeatOpacity());
            rec.setFill(colorScheme.getDiagonalColor());

            areas.add(rec);
        }

        return areas;
    }

    public void hideHeatMap(Pane drawPane) {
        if (this.heats != null && !this.heats.isEmpty())
            drawPane.getChildren().removeAll(this.heats);
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public List<Shape> computeDirectSuccAreas(){
        List<Shape> areas = new LinkedList<>();

        for (Wire w : wires){
            Cavity a = w.getCavities()[0];
            Cavity b = w.getCavities()[1];

            double distanceA = pythagorasDistance(a.getPos(), cableStore.getPos());
            double distanceB = pythagorasDistance(b.getPos(), cableStore.getPos());
            DirectSuccessorConstraint ds;

            if (w.getLength() < distanceA){
                ds = new DirectSuccessorConstraint(b, a, w, cableStore.getPos());

                Shape moveArea = ds.getMoveArea();
                moveArea.setOpacity(colorScheme.getHeatOpacity());
                moveArea.setFill(colorScheme.getDirectColor());

                Collection<Shape> arrows = ds.getArrows();
                for (Shape s : arrows)
                    s.setFill(colorScheme.getDirectColor());

                areas.addAll(arrows);
                areas.add(moveArea);
            }
            if (w.getLength() < distanceB){
                ds = new DirectSuccessorConstraint(a, b, w, cableStore.getPos());

                Shape moveArea = ds.getMoveArea();
                moveArea.setOpacity(colorScheme.getHeatOpacity());
                moveArea.setFill(colorScheme.getDirectColor());

                Collection<Shape> arrows = ds.getArrows();
                for (Shape s : arrows)
                    s.setFill(colorScheme.getDirectColor());

                areas.addAll(arrows);
                areas.add(moveArea);
            }

        }
        return areas;
    }

    public List<Shape> computeBlockingAreas(Palette p){
        List<Shape> areas = new LinkedList<>();

        for (Cavity c : activeCavities){
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

        for (Cavity c: activeCavities){
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
        for (Cavity source : activeCavities){

            for (Cavity c : activeCavities){

                if (source.equals(c)) continue; //Do not compare with itself!
                //if (c.getPos().getY() > source.getPos().getY()) continue; //Optimization, since only cavities below are blocked

                //at the moment checks for the cavity point (middle point) --> TODO: check if a whole rectangle needs to be checked instead of point
                boolean isAffected = source.getBlockingArea(palette).intersects(c.getPos().getX(), c.getPos().getY(), 1, 1); //.contains(c.getMiddlePoint());

                if (isAffected){
                    constraints.add(new BlockingConstraint(source, c, findWire(source, c)));
                }
            }
        }

        //diagonally close constraints
        for (Cavity source : activeCavities){
            for (Cavity c : activeCavities){
                if (source.equals(c)) continue; //Do not compare with itself!

                boolean isAffected = source.getDiagonallyCloseArea().intersects(c.getPos().getX(), c.getPos().getY(), 1, 1);

                if (isAffected){
                    constraints.add(new DiagonallyCloseConstraint(source, c, findWire(source, c)));
                }
            }
        }

        //short one-sided constraints
        for (Wire w : wires){

            if (w.getLength() > Parameters.shortL) continue; //only respect short cables
            Cavity source = w.getCavities()[0];

            for (Cavity c : activeCavities){
                boolean isAffected = source.getShortOneSidedArea(w).intersects(c.getPos().getX(), c.getPos().getY(), 1, 1);

                if (isAffected){
                    constraints.add(new ShortOneSidedConstraint(source, c, w));
                }
            }

        }


        //Chamber Triplet constraints
        for (Cavity source : activeCavities){
            Shape affectedArea = source.getChamberTripletArea();
            int count = 0;
            List<Cavity> affectedCavs = new LinkedList<Cavity>();

            for (Cavity c : activeCavities){
                if (source.equals(c)) continue; //Do not compare with oneself

                //if (affectedArea.intersects(c.getPos().getX(), c.getPos().getY(), 1, 1)){
                if (affectedArea.intersects(c.getMiddlePoint().getX(), c.getMiddlePoint().getY(), 1, 1)){
                    count++;
                    affectedCavs.add(c);
                }

                if (count >= 2){
                    constraints.add(new ChamberTripletConstraint(source, affectedCavs.get(0), affectedCavs.get(1)));
                    //Constraint found, start search for next cavity
                    break;
                }
            }
        }

        //Critical Distance
        /*
        Implemented according to example in TechTool.pdf
        TODO: compare with actual constraints in TechTool
        TODO: do NOT use Blocking and Diagonally Close but make two forms of CriticalDistanceCostraint
         */
        /*
        for (Wire w : wires){
            Cavity a = w.getCavities()[0];
            Cavity b = w.getCavities()[1];

            Shape as = w.getWireShape(a.getPos(), cableStore.getPos());
            Shape ab = w.getWireShape(a.getPos(), w.getCavities()[1].getPos());
            Shape bs = w.getWireShape(b.getPos(), cableStore.getPos());

            boolean coversAS, coversAB, coversBS;

            for (Cavity c : activeCavities){
                coversAS = as.intersects(c.getMiddlePoint().getX(), c.getMiddlePoint().getY(), c.getWidth(), c.getHeight());
                coversAB = ab.intersects(c.getMiddlePoint().getX(), c.getMiddlePoint().getY(), c.getWidth(), c.getHeight());
                coversBS = bs.intersects(c.getMiddlePoint().getX(), c.getMiddlePoint().getY(), c.getWidth(), c.getHeight());

               if (coversAS && coversAB && coversBS){
                   constraints.add(new BlockingConstraint(a, c, w));
                   constraints.add(new BlockingConstraint(b, c, w));
               } else if (coversAB && coversAS){
                   constraints.add(new BlockingConstraint(a, c, w));
               } else if (coversAB && coversBS){
                   constraints.add(new BlockingConstraint(b, c, w));
               } else if (coversAS && coversBS){
                   constraints.add(new CriticalDistanceConstraint(b, c, c, a, w));
                   constraints.add(new CriticalDistanceConstraint(a, c, c, b, w));
               } else if (coversAB) {
                   constraints.add(new CriticalDistanceConstraint(a, c, b, c, w));
               } else if (coversAS){
                   constraints.add(new CriticalDistanceConstraint(a,c,c,b, w));
               } else if (coversBS){
                   constraints.add(new CriticalDistanceConstraint(b,c,c,a,w));
               }

            }
        }

         */

        //Direct Successor
        //TODO: adjust position of cable store so that it matches the constraints*
        for (Wire w : wires){
            Cavity a = w.getCavities()[0];
            Cavity b = w.getCavities()[1];

            double distanceA = pythagorasDistance(a.getPos(), cableStore.getPos());
            double distanceB = pythagorasDistance(b.getPos(), cableStore.getPos());
            DirectSuccessorConstraint ds;

            if (w.getLength() < distanceA){
                ds = new DirectSuccessorConstraint(b, a, w, cableStore.getPos());
                constraints.add(ds);
            }
            if (w.getLength() < distanceB){
                ds = new DirectSuccessorConstraint(a, b, w, cableStore.getPos());
                constraints.add(ds);
            }

        }

        return constraints;
    }

    //Gives distance betweent two points using pythagoras
    private double pythagorasDistance(Position pos_a, Position pos_b){
        double ax = pos_a.getX();
        double ay = pos_a.getY();
        double bx = pos_b.getX();
        double by = pos_b.getY();


        double a = ax < bx ? bx - ax : ax - bx;
        double b = ay < by ? by - ay : ay - by;

        return Math.sqrt((a*a) + (b*b));
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

    public Palette getPalette(){
        return palette;
    }

    public List<Cavity> getActiveCavs(){
        return this.activeCavities;
    }

    public List<Shape> getWireShapes(){
        List<Shape> wireList = new LinkedList<>();

        for (Wire w : wires)
            wireList.add(w.getWireShape());

        return wireList;
    }

    public Wire findWire(Cavity a, Cavity b){

        for (Wire w : wires){
            Cavity first = w.getCavities()[0];
            Cavity second = w.getCavities()[1];

            if (first.equals(a) && second.equals(b))
                return w;
            if (second.equals(a) && first.equals(b))
                return w;
        }

        return null;
    }

    public CableStore getCableStore() {
        return this.cableStore;
    }
}
