package Controllers;

import CableTree.CableTree;
import Input.XMLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.MainGUI;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


public class MainFrameController {

    @FXML
    public ScrollPane scrollPane;
    @FXML
    public AnchorPane drawPane;
    @FXML
    private Button addButton;
    @FXML
    private MenuItem loadXML;

    private CableTree cableTree;

    public MainFrameController(){
    }

    //Gets called at the beginning. Could be used to load in some graphics on the canvas.
    @FXML
    public void initialize(){

    }

    public void addClicked(MouseEvent mouseEvent) {
        Rectangle rec = new Rectangle(800, 600, Color.RED);
        rec.setFill(Color.RED);
        rec.setOpacity(0.5);

        drawPane.getChildren().add(rec);
    }

    public void FileLoadXML(ActionEvent actionEvent) {
        //Let user choose xml file
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML cable tree file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        fileChooser.setInitialDirectory(new File("C:\\Users\\Lukas Schaller\\Documents\\TechTool-master\\Layouts"));
        File file = fileChooser.showOpenDialog(MainGUI.MainStage);

        XMLParser parser = new XMLParser(file.getAbsolutePath());
        cableTree = parser.parseXMLFile();

        //Clear previous drawings
        drawPane.getChildren().clear();
        //Now draw the tree to the panel
        cableTree.drawToPanel(drawPane);
    }

    public void loadPropertyStage(ActionEvent actionEvent) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../sample/properties.fxml"));
            Scene pScene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(pScene);

            //user can only interact with properties window if opened
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainGUI.MainStage);

            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void reloadCanvas(ActionEvent actionEvent) {
        //Clear previous drawings
        drawPane.getChildren().clear();
        //Now draw the tree to the panel
        cableTree.drawToPanel(drawPane);
    }

    public void showStatisticsStage(ActionEvent actionEvent) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../sample/StatisticsFrame.fxml"));
            Scene pScene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Statistics");
            stage.setScene(pScene);

            //user can only interact with properties window if opened
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainGUI.MainStage);

            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void showAnimationStage(ActionEvent actionEvent) {
        //TODO
    }
}
