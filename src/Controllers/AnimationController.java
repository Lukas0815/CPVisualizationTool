package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

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


    public void initialize(){
        System.out.println("Initializing animation stage!");
    }

}
