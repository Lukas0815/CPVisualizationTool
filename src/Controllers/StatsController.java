package Controllers;

import CableTree.CableTree;
import CableTree.Cavity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.Map;

public class StatsController {

    @FXML
    private PieChart pieChart;
    @FXML
    private BarChart barChart;

    public static CableTree cableTree;

    public void initialize(){
        //set up chart data
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        XYChart.Series dataSeries = new XYChart.Series();

        Map<Cavity, Integer> freqMap = cableTree.getCavFreqMap();

        for (Cavity c : cableTree.getCavities()){
            pieData.add(new PieChart.Data(c.toString(), freqMap.get(c)));
            //barChart.getData().add(new XYChart.Series<>(c.toString(), freqMap.get(c)));
            dataSeries.getData().add(new XYChart.Data(c.toString(), freqMap.get(c)));
        }

        //set up PieChart
        pieChart.setData(pieData);
        //pieChart.setTitle("I don't know");

        //set up BarChart
        barChart.getData().add(dataSeries);
    }

}
