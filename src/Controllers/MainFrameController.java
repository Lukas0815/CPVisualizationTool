package Controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class MainFrameController {

    @FXML
    private AnchorPane drawPane;
    private Button addButton;


    public void addClicked(MouseEvent mouseEvent) {
        Rectangle rec = new Rectangle(100, 50, Color.RED);
        rec.setFill(Color.RED);

        drawPane.getChildren().add(rec);
    }
}
