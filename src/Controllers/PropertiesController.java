package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class PropertiesController {

    @FXML
    private Spinner<Integer> bLockingAlphaSpin, blockingHBSpin, diagonalVSpin, diagonalHDSpin, ShortAlphaSpin, DiagonalLSpin, CriticalDSpin, ChamberHCSpin;

    @FXML
    public void initialize(){
        bLockingAlphaSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 360, 25));
        blockingHBSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 40));
        diagonalVSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 32));
        diagonalHDSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 32));
        ShortAlphaSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 360, 0));
        DiagonalLSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 7000));
        CriticalDSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 17));
        ChamberHCSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 50));
    }
}
