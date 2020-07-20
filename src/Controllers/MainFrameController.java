package Controllers;

import CableTree.CableTree;
import Constraints.*;
import Input.ColorScheme;
import Input.DatParser;
import Input.Parameters;
import Input.XMLParser;
import com.sun.javafx.scene.SceneEventDispatcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.MainGUI;
import CableTree.DatRepresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import CableTree.Cavity;
import CableTree.Wire;


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
    private Slider heatOpacitySlider, zoomSlider;
    @FXML
    private CheckBox blockingDrawCheck, diagonalDrawCheck, shortDrawCheck, criticalDrawCheck,
            directSuccDrawCheck, chamberDrawCheck, wireCheckBox, pureConflictCheckBox;
    @FXML
    private ListView constraintList, conflictView;
    @FXML
    private ChoiceBox conflictChooser;
    @FXML
    private CheckBox cycleCheckBox;
    @FXML
    private ToggleButton toggleDrawOnlySelected;

    private CableTree cableTree;
    private Map<Conflict, List<Shape>> cycleMap;
    private List<Shape> wireList;

    public MainFrameController(){
        this.cycleMap = new HashMap<>();
        this.wireList = new LinkedList<>();
    }

    //Gets called at the beginning. Could be used to load in some graphics on the canvas.
    @FXML
    public void initialize(){
        this.conflictView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

        //Debugging constraint animation
        if (file.getName().contains("R071")){
            List<Cavity> cavs = cableTree.getActiveCavs();
            HashMap<String, Cavity> cavMap = new HashMap<String, Cavity>();
            List<String> names = Arrays.asList("C5PW10A(A):12", "C5PW10A(A):3", "C5PW10A(A):5", "C5PM26-A(A):2", "C5PM26-A(A):1");

            for (Cavity c : cavs){
                if (names.contains(c.toString()))
                    cavMap.put(c.toString(), c);
            }

            BlockingConstraint b1 = new BlockingConstraint(cavMap.get("C5PW10A(A):12"), cavMap.get("C5PM26-A(A):2"), cableTree.findWire(cavMap.get("C5PW10A(A):12"), cavMap.get("C5PM26-A(A):2")) );
            BlockingConstraint b2 = new BlockingConstraint(cavMap.get("C5PW10A(A):3"), cavMap.get("C5PM26-A(A):1"), cableTree.findWire(cavMap.get("C5PW10A(A):3"), cavMap.get("C5PM26-A(A):1")));
            DiagonallyCloseConstraint b3 = new DiagonallyCloseConstraint(cavMap.get("C5PW10A(A):5"), cavMap.get("C5PW10A(A):12"), cableTree.findWire(cavMap.get("C5PW10A(A):5"), cavMap.get("C5PW10A(A):12")));
            DirectSuccessorConstraint d1 = new DirectSuccessorConstraint(cavMap.get("C5PM26-A(A):2"), cavMap.get("C5PW10A(A):3"), cableTree.findWire(cavMap.get("C5PM26-A(A):2"), cavMap.get("C5PW10A(A):3")));
            DirectSuccessorConstraint d2 = new DirectSuccessorConstraint(cavMap.get("C5PM26-A(A):1"), cavMap.get("C5PW10A(A):5"), cableTree.findWire(cavMap.get("C5PM26-A(A):1"), cavMap.get("C5PW10A(A):5")));

            Conflict conflict = new Conflict(Arrays.asList(b1, b2, b3, d1, d2), "circleTest");
            cableTree.addConflict(conflict);
        }

        //Clear previous drawings
        drawPane.getChildren().clear();
        //Now draw the tree to the panel
        cableTree.drawToPanel(drawPane, false, false);

        adaptGUIColorScheme();

        //Fill ListView with constraint representations
        updateConstraintView();

        //fill conflict chooser
        ObservableList conflictList = FXCollections.observableList(List.copyOf(cableTree.getConflicts()));
        this.conflictChooser.setItems(conflictList);
        if (conflictList.size() != 0)
            this.conflictChooser.setValue(conflictList.get(0));
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
        cableTree.drawToPanel(drawPane, false, false);

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

        //Generating an artificial conflict for testing purpose
        //TODO: remove this section after testing or add housing for further testing?
        List<Constraint> conflicts = new LinkedList<Constraint>();
        Cavity a = new Cavity("a", 132, 420, 0,0,0);
        Cavity b = new Cavity("b", 128, 347, 0,0,0);
        Cavity c = new Cavity("c", 426, 597, 0,0,0);
        Cavity d = new Cavity("d", 216, 460, 0,0,0);
        Cavity f = new Cavity("f", 28, 601, 0,0,0);

        conflicts.add(new BlockingConstraint(a, b, new Wire(1000, "std", a, b)));
        conflicts.add(new BlockingConstraint(b, f, new Wire(1000, "std", b, f)));
        conflicts.add(new BlockingConstraint(b, c, new Wire(1000, "std", b, c)));
        conflicts.add(new BlockingConstraint(c, a, new Wire(1000, "std", c, a)));
        conflicts.add(new BlockingConstraint(f, d, new Wire(1000, "std", f, d)));


        //Conflict conflict = new Conflict(conflicts, "conflict 1");

        //this.cableTree.addConflict(conflict);

        try {
            AnimationController.cableTree = this.cableTree;

            Parent root = FXMLLoader.load(getClass().getResource("../sample/AnimationStage.fxml"));
            Scene pScene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Animation");
            stage.setScene(pScene);

            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }


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

        //Find Conflict
        //TODO


    }

    public void addConflictClicked(ActionEvent actionEvent) {
        try{
            ConflictController.cableTree = this.cableTree;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../sample/addConflict.fxml"));


            Scene pScene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Conflict menu");
            stage.setScene(pScene);

            //user can only interact with properties window if opened
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainGUI.MainStage);

            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void updateConflictChooser() {
        //List<Conflict> list = cableTree.getConflicts();
        this.conflictChooser.setItems(FXCollections.emptyObservableList());
        this.conflictChooser.setItems(FXCollections.observableList(this.cableTree.getConflicts()));

    }

    public void editConflictButton(ActionEvent actionEvent) {
        try{
            EditConflictController.cableTree = this.cableTree;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("../sample/editConflict.fxml"));


            Scene pScene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Conflict editor");
            stage.setScene(pScene);

            //user can only interact with properties window if opened
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainGUI.MainStage);

            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void deleteConflictClicked(ActionEvent actionEvent) {
        Conflict c = (Conflict) this.conflictChooser.getValue();
        if (c != null)
            this.cableTree.getConflicts().remove(c);

        this.updateConflictChooser();
    }

    public void toggleCycle(ActionEvent actionEvent) {
        Conflict c = (Conflict) this.conflictChooser.getValue();
        List<Shape> arrows = null;

        if (this.cycleCheckBox.isSelected() && c != null){
            arrows = c.drawCycle(this.drawPane);
            cycleMap.put(c, arrows);
            this.drawPane.getChildren().addAll(arrows);
        } else if (!this.cycleCheckBox.isSelected()){

            List<Shape> conflictArrows = cycleMap.get(c);
            if (conflictArrows != null && !conflictArrows.isEmpty()){
                this.drawPane.getChildren().removeAll(conflictArrows);
            }

        }

    }

    public void toggleWires(ActionEvent actionEvent) {
        //Always clear out previously drawn wires, so nothing gets drawn twice
        this.drawPane.getChildren().removeAll(wireList);

        //If wires should be drawn, redraw all of them
        if (this.wireCheckBox.isSelected()){
            wireList = cableTree.getWireShapes();
            drawPane.getChildren().addAll(wireList);
        }

    }

    public void togglePureConflict(ActionEvent actionEvent) {

        //Get conflict to draw
        Conflict conflict = (Conflict) this.conflictChooser.getValue();
        if (conflict == null)
            return;

        //disable heatmap first for clarity
        this.cableTree.hideHeatMap(drawPane);
        this.heatmapOption.setSelected(false);

        //begin drawing process
        for (Constraint c : conflict.getConstraints()){
            if (this.pureConflictCheckBox.isSelected())
                c.draw(drawPane, this.cableTree.getPalette(), this.cableTree.getColorScheme());
            else
                c.hide(drawPane);
        }

    }

    public void toggleDrawOnlySelected(ActionEvent actionEvent) {
        if (!this.toggleDrawOnlySelected.isSelected()){
            Conflict conflict = (Conflict) this.conflictChooser.getValue();

            //wipe drawing of conflict completely
            for (Constraint c : conflict.getConstraints())
                c.hide(drawPane);
        }
    }

    public void chooseConflict(ActionEvent actionEvent) {
        Conflict c = (Conflict) this.conflictChooser.getValue();

        ObservableList constraintList = FXCollections.observableList(c.getConstraints());
        this.conflictView.setItems(constraintList);
    }

    public void updateConflictSelectionDrawing(MouseEvent mouseEvent) {
        if (!this.toggleDrawOnlySelected.isSelected())
            return;

        System.out.println("Now starting to draw!");

        Conflict conflict = (Conflict) this.conflictChooser.getValue();

        //wipe drawing of conflict completely
        for (Constraint c : conflict.getConstraints())
            c.hide(drawPane);

        //Get selected constraints
        ObservableList<Integer> selectedIndices = this.conflictView.getSelectionModel().getSelectedIndices();
        //Draw the constraints
        for (Integer i : selectedIndices){
            Constraint c = (Constraint) conflictView.getItems().get(i);
            c.draw(drawPane, cableTree.getPalette(), cableTree.getColorScheme());
        }

    }

    public void chooseZoom(MouseEvent mouseEvent) {
        //drawPane.scaleXProperty().bind(zoomSlider.valueProperty());
        //drawPane.scaleYProperty().bind(zoomSlider.valueProperty());

        drawPane.getTransforms().clear();

        Scale scaleTransform = new Scale(zoomSlider.getValue(), zoomSlider.getValue(), 0, 0);
        drawPane.getTransforms().add(scaleTransform);
    }
}
