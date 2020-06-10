package sample;

import Input.XMLParser;
import javafx.application.Application;


public class Main {

    public static void main(String[] args) {
        XMLParser parser = new XMLParser("C:\\Users\\Lukas Schaller\\Documents\\TechTool-master\\Layouts\\R071.xml");
        parser.parseXMLFile();

        //launch main gui
        Application.launch(MainGUI.class, null);
    }
}
