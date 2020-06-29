package Controllers;

import CableTree.CableTree;
import Constraints.Conflict;
import Constraints.Constraint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.MainGUI;

import java.util.List;

public class ConflictController {

    @FXML
    private Button insertButton, removeButton, saveButton;
    @FXML
    private ListView constraintView, conflictView;
    @FXML
    private TextField nameTextField;

    public static CableTree cableTree;

    @FXML
    private void initialize(){
        ObservableList constraintList = FXCollections.observableArrayList();
        constraintList.addAll(cableTree.getConstraints());
        constraintView.setItems(constraintList);
    }


    public void insertButtonClicked(ActionEvent actionEvent) {
        Constraint c = (Constraint) constraintView.getSelectionModel().getSelectedItem();
        if (!conflictView.getItems().contains(c))
            conflictView.getItems().add(c);
    }

    public void removeButtonClicked(ActionEvent actionEvent) {
        Constraint c = (Constraint) conflictView.getSelectionModel().getSelectedItem();
        conflictView.getItems().remove(c);
    }

    public void saveButtonClicked(ActionEvent actionEvent) {
        String name = nameTextField.getText();
        List<Constraint> conflictList = conflictView.getItems();

        if (conflictList.isEmpty())
            return;
        if (name == null || name.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing information");
            alert.setContentText("Name conflict first");
            alert.showAndWait();
            return;
        }

        cableTree.addConflict(new Conflict(conflictList, name));
        MainGUI.controller.updateConflictChooser();

        //Close window
        Stage stage = (Stage) this.saveButton.getScene().getWindow();
        stage.close();

    }

}
