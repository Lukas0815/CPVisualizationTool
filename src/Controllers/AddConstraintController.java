package Controllers;

import CableTree.CableTree;
import CableTree.Cavity;
import Constraints.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sample.MainGUI;

import java.util.List;


public class AddConstraintController {

    @FXML
    private Button constraintAddSafe, constraintAddAbort;
    @FXML
    private ChoiceBox typeChooser, houseChoose1_opt1, houseChoose2_opt1, houseChoose1_opt2, houseChoose2_opt2;
    @FXML
    private HBox hboxConstraint2;

    public static CableTree cableTree;

    public static List<Cavity> cavities;

    private final String constraintTypes[] = {
            "Blocking Constraint",
            "Chamber Triplet Constraint",
            "Critical Distance Constraint",
            "Diagonally Close Constraint",
            "Direct Successors Constraint",
            "Short One-Sided Constraint"
    };

    @FXML
    public void initialize(){

        //set up ChoiceBox for choosing a constraint type
        ObservableList names = FXCollections.observableArrayList(constraintTypes);

        typeChooser.setItems(names);

        //initially hide second constraint input option
        hboxConstraint2.setVisible(false);

        ObservableList cavityList = FXCollections.observableArrayList(cavities);
        houseChoose1_opt1.setItems(cavityList);
        houseChoose1_opt2.setItems(cavityList);
        houseChoose2_opt1.setItems(cavityList);
        houseChoose2_opt2.setItems(cavityList);

    }

    public void chooseConflictType(ActionEvent actionEvent) {
       /*
        switch (typeChooser.getValue().toString()){
            case "Blocking Constraint":
                System.out.println("Blocking Constraint");
                break;

            default:
        }
        */
    }


    public void abortClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) constraintAddAbort.getScene().getWindow();
        stage.close();
    }

    public void safeClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Missing Information");

        if (typeChooser.getValue() == null) {
            alert.setContentText("Please choose a type");
            alert.showAndWait();
            return;
        }
        if (houseChoose1_opt1.getValue() == null || houseChoose2_opt1.getValue() == null){
            alert.setContentText("Please choose all cavities");
            alert.showAndWait();
            return;
        }

        Cavity leftCav = (Cavity) houseChoose1_opt1.getValue();
        Cavity rightCav = (Cavity) houseChoose2_opt1.getValue();
        Constraint constraint;

        switch (typeChooser.getValue().toString()){
            case "Blocking Constraint":
                constraint = new BlockingConstraint(leftCav, rightCav, null);
                System.out.println("Adding a constraint");
                break;
            case "Chamber Triplet Constraint":
                constraint = new ChamberTripletConstraint(leftCav, rightCav, null);
                break;
            case "Critical Distance Constraint":
                constraint = new CriticalDistanceConstraint(leftCav, rightCav, null);
                break;
            case "Diagonally Close Constraint":
                constraint = new DiagonallyCloseConstraint(leftCav, rightCav, null);
                break;
            case "Direct Successors Constraint":
                constraint = new DirectSuccessorConstraint(leftCav, rightCav, null);
                break;
            case "Short One-Sided Constraint":
                constraint = new ShortOneSidedConstraint(leftCav, rightCav, null);
                break;
            default:
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Constraint could not be created");
                alert.setContentText("Something went wrong... " + typeChooser.getValue().toString());
                alert.showAndWait();
                return;
        }


        cableTree.addConstraint(constraint);

        //Make new constraint visible in ListView
        MainGUI.controller.updateConstraintView();

        //Close window
        Stage stage = (Stage) this.constraintAddSafe.getScene().getWindow();
        stage.close();
    }
}
