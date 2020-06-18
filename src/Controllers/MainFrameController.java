package Controllers;

import CableTree.CableTree;
import Constraints.Constraint;
import Input.ColorScheme;
import Input.DatParser;
import Input.XMLParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.MainGUI;
import CableTree.DatRepresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MainFrameController {

    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Pane drawPane;
    @FXML
    private Button addButton;
    @FXML
    private MenuItem loadXML, loadColorSchemes;
    @FXML
    private CheckBox heatmapOption;
    @FXML
    private ColorPicker blockingColorPicker, diagonalColorPicker, shortColorPicker, criticalColorPicker, directColorPicker, chamberColorPicker;
    @FXML
    private Slider heatOpacitySlider;
    @FXML
    private CheckBox blockingDrawCheck, diagonalDrawCheck, shortDrawCheck, criticalDrawCheck, directSuccDrawCheck, chamberDrawCheck;
    @FXML
    private ListView constraintList;

    private CableTree cableTree;

    public MainFrameController(){

    }

    //Gets called at the beginning. Could be used to load in some graphics on the canvas.
    @FXML
    public void initialize(){


    }

    public void addClicked(MouseEvent mouseEvent) {
        if(cableTree == null) return;

        try{
            AddConstraintController.cavities = cableTree.getCavities();
            AddConstraintController.cableTree = cableTree;
            Parent root = FXMLLoader.load(getClass().getResource("../sample/addConstraint.fxml"));
            Scene pScene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Add Constraint");
            stage.setScene(pScene);

            //user can only interact with properties window if opened
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainGUI.MainStage);


            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void FileLoadXML(ActionEvent actionEvent) {
        //Let user choose xml file
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML cable tree file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            fileChooser.setInitialDirectory(new File("C:\\Users\\Lukas Schaller\\Documents\\TechTool-master\\Layouts"));
        File file = fileChooser.showOpenDialog(MainGUI.MainStage);

        XMLParser parser = new XMLParser(file.getAbsolutePath());
        cableTree = parser.parseXMLFile();

        //Clear previous drawings
        drawPane.getChildren().clear();
        //Now draw the tree to the panel
        cableTree.drawToPanel(drawPane, false);

        adaptGUIColorScheme();

        //Fill ListView with constraint representations
        updateConstraintView();
    }

    public void updateConstraintView(){
        ObservableList constraints = FXCollections.observableArrayList(cableTree.getConstraints());
        constraintList.setItems(constraints);
    }

    private void adaptGUIColorScheme(){
        //Match default displayed visualization options to parameters given (e.g. color scheme)
        heatOpacitySlider.setValue(cableTree.getColorScheme().getHeatOpacity() * 100);
        ColorScheme c = cableTree.getColorScheme();
        this.blockingColorPicker.setValue(c.getBlockingColor());
        this.diagonalColorPicker.setValue(c.getDiagonalColor());
        this.shortColorPicker.setValue(c.getShortColor());
        this.criticalColorPicker.setValue(c.getCriticalColor());
        this.directColorPicker.setValue(c.getDirectColor());
        this.chamberColorPicker.setValue(c.getChamberColor());
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
        if (drawPane == null || cableTree == null) return;

        //Clear previous drawings
        drawPane.getChildren().clear();
        //Now draw the tree to the panel
        cableTree.drawToPanel(drawPane, false);

        //draw anything else based on chosen options
        if (heatmapOption.isSelected()) cableTree.drawHeatMap(drawPane);
    }

    public void showStatisticsStage(ActionEvent actionEvent) {
        try{
            StatsController.cableTree = this.cableTree;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../sample/StatisticsFrame.fxml"));


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

    public void toggleHeatMap(ActionEvent actionEvent) {
        if (heatmapOption.isSelected()){
            cableTree.drawHeatMap(drawPane);
        } else {
            cableTree.hideHeatMap(drawPane);
        }
    }

    
    /* Handle heatmap options */
    
    public void chooseBlockingColor(ActionEvent actionEvent) {
        //Retrieve the chosen color value
        Color color = this.blockingColorPicker.getValue();
        cableTree.getColorScheme().setBlockingColor(color);
        reloadCanvas(actionEvent);
    }

    public void chooseDiagonalColor(ActionEvent actionEvent) {
        Color color = this.diagonalColorPicker.getValue();
        cableTree.getColorScheme().setDiagonalColor(color);
        reloadCanvas(actionEvent);
    }

    public void chooseShortColor(ActionEvent actionEvent) {
        Color color = this.shortColorPicker.getValue();
        cableTree.getColorScheme().setShortColor(color);
        reloadCanvas(actionEvent);
    }

    public void chooseCriticalColor(ActionEvent actionEvent) {
        Color color = this.criticalColorPicker.getValue();
        cableTree.getColorScheme().setCriticalColor(color);
        reloadCanvas(actionEvent);
    }

    public void chooseDirectColor(ActionEvent actionEvent) {
        Color color = this.directColorPicker.getValue();
        cableTree.getColorScheme().setDirectColor(color);
        reloadCanvas(actionEvent);
    }

    public void chooseChamberColor(ActionEvent actionEvent) {
        Color color = this.chamberColorPicker.getValue();
        cableTree.getColorScheme().setChamberColor(color);
        reloadCanvas(actionEvent);
    }

    public void chooseHeatOpacity(MouseEvent mouseEvent) {
        double opacity = heatOpacitySlider.getValue() / 100;
        cableTree.getColorScheme().setHeatOpacity(opacity);
        reloadCanvas(null);
    }

    public void toggleDrawBlocking(ActionEvent actionEvent) {
        if (blockingDrawCheck.isSelected()){
            cableTree.setHeatmapPrintFlag(0, true);
        } else {
            cableTree.setHeatmapPrintFlag(0, false);
        }

        cableTree.drawHeatMap(drawPane);
    }

    public void toggleDrawChamber(ActionEvent actionEvent) {
        if (chamberDrawCheck.isSelected()){
           this.cableTree.setHeatmapPrintFlag(3, true);
        } else {
            this.cableTree.setHeatmapPrintFlag(3, false);
        }
        cableTree.drawHeatMap(drawPane);

    }

    public void toggleDrawDirectSucc(ActionEvent actionEvent) {
        if (directSuccDrawCheck.isSelected()){
            this.cableTree.setHeatmapPrintFlag(5, true);
        } else {
            this.cableTree.setHeatmapPrintFlag(5, false);
        }

        cableTree.drawHeatMap(drawPane);
    }

    public void toggleDrawCritical(ActionEvent actionEvent) {
        if (criticalDrawCheck.isSelected()){
            this.cableTree.setHeatmapPrintFlag(4, true);
        } else {
            this.cableTree.setHeatmapPrintFlag(4, false);
        }

        cableTree.drawHeatMap(drawPane);
    }

    public void toggleDrawShort(ActionEvent actionEvent) {
        if (shortDrawCheck.isSelected()){
           this.cableTree.setHeatmapPrintFlag(2, true);
        } else {
            this.cableTree.setHeatmapPrintFlag(2, false);
        }

        cableTree.drawHeatMap(drawPane);
    }

    public void toggleDrawDiagonal(ActionEvent actionEvent) {
        if (diagonalDrawCheck.isSelected()){
            this.cableTree.setHeatmapPrintFlag(1, true);
        } else {
            this.cableTree.setHeatmapPrintFlag(1, false);
        }

        cableTree.drawHeatMap(drawPane);
    }

    /*
    Opens File browser to let user select a predefined color scheme
    TODO: open new stage where one could choose or even make own color scheme easily
    */
    public void loadColorScheme(ActionEvent actionEvent) {
        final FileChooser fc = new FileChooser();
        fc.setTitle("Open a settings file");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Color schemes (*.cpcs)", "*.cpcs", "*.xml"));
        File file = fc.showOpenDialog(MainGUI.MainStage);

        try {
            cableTree.getColorScheme().loadScheme(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        reloadCanvas(actionEvent);
        adaptGUIColorScheme();
    }

    public void FileLoadDat(ActionEvent actionEvent) {
        //Let user select the file
        final FileChooser fc = new FileChooser();
        fc.setTitle("Open the corresponding .dat file");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Dat files (*.dat)", "*.dat"));
        File file = fc.showOpenDialog(MainGUI.MainStage);

        //create internal representation of dat file
        DatParser datParser = new DatParser(file);
        try {
            DatRepresentation datRepr = datParser.parse();
            datRepr.matchWithCableTree(this.cableTree);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //See if the .dat file matches the current cableTree
        //TODO
    }
}
