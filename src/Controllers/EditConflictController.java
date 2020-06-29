package Controllers;

import CableTree.CableTree;
import Constraints.Conflict;
import Constraints.Constraint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.MainGUI;

import java.util.List;

public class EditConflictController {
    @FXML
    private Button insertButton, removeButton, saveButton;
    @FXML
    private ListView constraintView, conflictView;
    @FXML
    private ChoiceBox conflictChooser;

    public static CableTree cableTree;
    private Conflict current, old;

    @FXML
    private void initialize(){
        ObservableList constraintList = FXCollections.observableArrayList();
        constraintList.addAll(cableTree.getConstraints());
        constraintView.setItems(constraintList);

        this.conflictChooser.setItems(FXCollections.observableList(cableTree.getConflicts()));
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
        cableTree.getConflicts().remove(old);
        cableTree.getConflicts().add(current);

        //Close window
        Stage stage = (Stage) this.saveButton.getScene().getWindow();
        stage.close();

        MainGUI.controller.updateConflictChooser();
    }

    public void chooseConflict(ActionEvent actionEvent) {
        old = (Conflict) conflictChooser.getValue();
        current = new Conflict(old.getConstraints(), old.getName());
        ObservableList conflictList = FXCollections.observableList(current.getConstraints());

        conflictView.setItems(conflictList);
    }
}
