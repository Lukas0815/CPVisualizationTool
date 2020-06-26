package Controllers;

import CableTree.CableTree;
import Constraints.Conflict;
import Constraints.Constraint;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import CableTree.Position;

public class AnimationController {

    @FXML
    private Pane animationPane;
    @FXML
    private ChoiceBox conflictChooser, iterationChooser;
    @FXML
    private ListView conflictView, iterationView;
    @FXML
    private Label iterationAmount;
    @FXML
    private Button prevButton, nextButton, animateAllButton;
    @FXML
    private Slider speedSlider;

    private Rectangle roboArm;
    private double armDefaultX, armDefaultY;
    private final double animationTime = 1000;
    private List<Conflict> conflicts;
    private Conflict currentConflict;
    private LinkedList<Constraint> currentIteration;
    private int iterationCount;
    private Constraint toAnimate;
    private double width, height;
    private EventHandler<ActionEvent> adjustPosition;


    public static CableTree cableTree;

    public void AnimationController(){

    }

    public void initialize(){
        this.width = cableTree.getPalette().getWidth();
        this.height = cableTree.getPalette().getHeight();
        this.armDefaultX = width - 50;
        this.armDefaultY = height / 2;

        this.animationPane.setPrefSize(width, height);

        //fill conflict chooser
        ObservableList conflictList = FXCollections.observableArrayList();
        this.conflicts = cableTree.getConflicts();
        for (Conflict c : conflicts)
            conflictList.add(c.getName());

        conflictChooser.setItems(conflictList);

        //If a conflict exists, fill in all other elements
        Conflict first = this.conflicts.get(0);
        if(first != null){
            this.currentConflict = first;
            //set choosers default value to first conflict
            conflictChooser.setValue(conflictList.get(0));
            fillConflictView(first);
            setPermutationNumber(first.getConflictPermutationNumber());
            fillIterationChooser(first);
            fillIterationView(first, 0);
        }

        conflictChooser.show();


        cableTree.drawToPanel(animationPane, false);
    }


    public void animateAll(ActionEvent actionEvent) {
        this.roboArm = spawnArm(armDefaultX, armDefaultY, this.animationPane);
    }

    public void prevButtonPressed(ActionEvent actionEvent) {
        //TODO
    }

    public void nextButtonPressed(ActionEvent actionEvent) {
        //start from beginning again
        if (currentIteration.size() >= iterationCount){
            iterationCount = 0;
        }

        this.toAnimate = currentIteration.get(iterationCount);

        Position pos1 = toAnimate.getSource().getPos();
        Position pos2 = toAnimate.getAffected().getPos();

        SequentialTransition st = new SequentialTransition();
        moveArm(pos1.getX(), pos1.getY(), st);
        resetArm(st);
        moveArm(pos2.getX(), pos2.getY(), st);
        resetArm(st);

        st.play();

        iterationCount++;
    }

    private Rectangle spawnArm(double x, double y, Pane drawPane){
        Rectangle rec = new Rectangle();
        rec.setX(x);
        rec.setY(y);
        rec.setWidth(300);
        rec.setHeight(20);

        rec.setFill(Color.BLACK);

        drawPane.getChildren().add(rec);

        return rec;
    }

    /*
    Moves roboter arm back to its resting position specified by (armDefaultX, armDefaultY)
     */
    private void resetArm(SequentialTransition st){

        //Build up animation t1 - moving back on x axis
        KeyFrame horizontal = new KeyFrame(Duration.millis(animationTime),
                new KeyValue(this.roboArm.translateXProperty(), armDefaultX - roboArm.getX()));

        Timeline t1 = new Timeline();
        t1.getKeyFrames().add(horizontal);
        t1.setCycleCount(1);


        //Build up animation t2 - moving back on y axis
        KeyFrame vertical = new KeyFrame(Duration.millis(animationTime),
                new KeyValue(this.roboArm.translateYProperty(), armDefaultY - roboArm.getY()));

        Timeline t2 = new Timeline();
        t2.getKeyFrames().add(vertical);
        t2.setCycleCount(1);


        //add animation
        st.getChildren().addAll(t1,t2);
    }

    private void moveArm(double x, double y, SequentialTransition st){

        KeyFrame vertical = new KeyFrame(Duration.millis(animationTime),
                new KeyValue(this.roboArm.translateYProperty(), y - roboArm.getY()));

        KeyFrame horizontal = new KeyFrame(Duration.millis(animationTime),
                new KeyValue(this.roboArm.translateXProperty(), x - roboArm.getX()));

        Timeline verticalTime = new Timeline();
        verticalTime.getKeyFrames().add(vertical);

        Timeline horizontalTime = new Timeline();
        horizontalTime.getKeyFrames().add(horizontal);

        //add animation
        st.getChildren().addAll(verticalTime, horizontalTime);
    }

    private void fillConflictView(Conflict c){
        ObservableList constraints = FXCollections.observableArrayList(c.getCircle(c.getConstraints().get(0)));
        this.conflictView.setItems(constraints);
    }

    private void setPermutationNumber(int n){
        this.iterationAmount.setText(String.valueOf(n));
    }

    private void fillIterationChooser(Conflict first) {
        ObservableList itNumbers = FXCollections.observableArrayList();

        for(int i=1; i<=first.getConflictPermutationNumber(); i++){
            itNumbers.add("Iteration #" + i);
        }

        this.iterationChooser.setItems(itNumbers);

        if(itNumbers.size() != 0)
            iterationChooser.setValue(itNumbers.get(0));
    }

    private void fillIterationView(Conflict c, int n){
        ObservableList list = FXCollections.observableArrayList();
        int length = c.getConflictPermutationNumber();

        List<Conflict> circle = c.getCircle(c.getConstraints().get(0));

        assert (n <= length);
        for(int i=n; i<length; i++)
            list.add(circle.get(i));
        for(int i=0; i<n; i++)
            list.add(circle.get(i));

        this.currentIteration = new LinkedList(list);

        this.iterationView.setItems(list);
    }

    public void chooseIteration(ActionEvent actionEvent) {
        String value = this.iterationChooser.getValue().toString();
        value = value.split("#")[1];
        int n = Integer.valueOf(value);

        fillIterationView(currentConflict, n);
        this.iterationCount = 0;
    }

    public void chooseConflict(ActionEvent actionEvent) {
        String value = this.conflictChooser.getValue().toString();

        for (Conflict c: conflicts){
            if (c.getName().equals(value))
                this.currentConflict = c;
        }
    }


}
