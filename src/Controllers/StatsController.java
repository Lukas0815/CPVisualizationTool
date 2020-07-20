package Controllers;

import CableTree.CableTree;
import CableTree.Cavity;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

import java.util.*;

public class StatsController {

    @FXML
    private PieChart pieChart;
    @FXML
    private BarChart barChart;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Pane drawPane;
    @FXML
    private ListView rankingList;

    public static CableTree cableTree;

    @FXML
    public void initialize(){
        //set up chart data
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        XYChart.Series dataSeries = new XYChart.Series();

        Map<Cavity, Integer> freqMap = cableTree.getCavFreqMap();

        for (Cavity c : cableTree.getActiveCavs()){
            pieData.add(new PieChart.Data(c.toString(), freqMap.get(c)));
            //barChart.getData().add(new XYChart.Series<>(c.toString(), freqMap.get(c)));
            dataSeries.getData().add(new XYChart.Data(c.toString(), freqMap.get(c)));
        }


        //set up PieChart
        pieChart.setData(pieData);
        //pieChart.setTitle("I don't know");

        //set up BarChart
        barChart.getData().add(dataSeries);

        //set up pallete view where on cavity the number of occurence is displayed
        cableTree.drawToPanel(drawPane, true, false);

        //fill ranking into ListView
        ObservableList<Integer> cavAmounts = FXCollections.observableArrayList();
        cavAmounts.addAll(cableTree.getCavFreqMap().values());
        List<Integer> sortedAmount = new ArrayList<Integer>(cavAmounts);
        Collections.sort(sortedAmount);
        Collections.reverse(sortedAmount);


        ObservableList ranking = FXCollections.observableArrayList();
        Map<Integer, Cavity> cavFreqMap = invert(cableTree.getCavFreqMap());

        int rank = 1;
        for (int i : sortedAmount){
            ranking.add(rank + "\t " + cavFreqMap.get(i) + "\t#" + i);
            rank++;
        }

        rankingList.setItems(ranking);
    }


    private static <V, K> Map<V, K> invert(Map<K, V> map) {

        Map<V, K> inv = new HashMap<V, K>();

        for (Map.Entry<K, V> entry : map.entrySet())
            inv.put(entry.getValue(), entry.getKey());

        return inv;
    }
}
