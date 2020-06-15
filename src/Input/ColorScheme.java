package Input;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ColorScheme {

    private Color blockingColor, diagonalColor, shortColor, criticalColor, directColor, chamberColor;
    private double heatOpacity;

    public ColorScheme(){

        try {
            loadScheme(new File("src/Input/default-colors.xml"));
        } catch (Exception e) {
            System.out.println("Could not load scheme, loading default!");
            e.printStackTrace();

            this.blockingColor = Color.GRAY;
            this.diagonalColor = Color.YELLOW;
            this.shortColor = Color.GREEN;

            this.heatOpacity = 0.3;
        }
    }

    public void loadScheme(File file) throws Exception{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc;

        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(file);

        //Optionally normalize document
        doc.getDocumentElement().normalize();

        //Read colors
        Node nNode = doc.getElementsByTagName("ConstraintColors").item(0);

        if (nNode.getNodeType() == Node.ELEMENT_NODE){
            Element eElement = (Element) nNode;

            this.blockingColor = Color.web(eElement.getElementsByTagName("BlockingCable").item(0).getTextContent());
            this.diagonalColor = Color.web(eElement.getElementsByTagName("DiagonallyClose").item(0).getTextContent());
            this.shortColor = Color.web(eElement.getElementsByTagName("ShortOneSided").item(0).getTextContent());
            this.criticalColor = Color.web(eElement.getElementsByTagName("CriticalDistance").item(0).getTextContent());
            this.directColor = Color.web(eElement.getElementsByTagName("DirectSuccessor").item(0).getTextContent());
            this.chamberColor = Color.web(eElement.getElementsByTagName("ChamberTriplet").item(0).getTextContent());
        } else {
            System.out.println("Could not read colors from scheme!");
        }

        //Read opacity
        Element eOpacity = (Element) doc.getElementsByTagName("heatOpacity").item(0);
        this.heatOpacity = Double.parseDouble(eOpacity.getTextContent());
    }

    public Color getBlockingColor() {
        return blockingColor;
    }

    public void setBlockingColor(Color blockingColor) {
        this.blockingColor = blockingColor;
    }

    public Color getDiagonalColor() {
        return diagonalColor;
    }

    public void setDiagonalColor(Color diagonalColor) {
        this.diagonalColor = diagonalColor;
    }

    public Color getShortColor() {
        return shortColor;
    }

    public void setShortColor(Color shortColor) {
        this.shortColor = shortColor;
    }

    public Color getCriticalColor() {
        return criticalColor;
    }

    public void setCriticalColor(Color criticalColor) {
        this.criticalColor = criticalColor;
    }

    public Color getDirectColor() {
        return directColor;
    }

    public void setDirectColor(Color directColor) {
        this.directColor = directColor;
    }

    public Color getChamberColor() {
        return chamberColor;
    }

    public void setChamberColor(Color chamberColor) {
        this.chamberColor = chamberColor;
    }

    public double getHeatOpacity() {
        return heatOpacity;
    }

    public void setHeatOpacity(double heatOpacity) {
        this.heatOpacity = heatOpacity;
    }
}
