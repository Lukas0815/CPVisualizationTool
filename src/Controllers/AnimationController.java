package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

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


    public void initialize(){

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
    private void resetArm(){

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

        //specify that animation t2 is played only after t1 has finished
        t1.setOnFinished(new EventHandler<ActionEvent>()  {
            @Override
            public void handle(ActionEvent actionEvent) {
                t2.play();
            }
        });

        //execute animation
        t1.play();
    }

    public void animateAll(ActionEvent actionEvent) {
        this.armDefaultX = this.animationPane.getWidth() - 50;
        this.armDefaultY = this.animationPane.getHeight() /2;

        this.roboArm = spawnArm(armDefaultX, armDefaultY, this.animationPane);

        //testing
        roboArm.setX(400);
        roboArm.setY(400);

    }

    public void prevButtonPressed(ActionEvent actionEvent) {
        //TODO
    }

    public void nextButtonPressed(ActionEvent actionEvent) {
        resetArm();
        //TODO
    }
}
