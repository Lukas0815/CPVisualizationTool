package sample;

import Controllers.MainFrameController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainGUI extends Application {

    @Override
    public void init() throws Exception {
        //executes before start gets called
    }

    @Override
    public void stop() throws Exception {
        //Executes after window has been closed
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainFrame.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Conflict Visualization");
        stage.setResizable(true);
        stage.show();


    }
}
