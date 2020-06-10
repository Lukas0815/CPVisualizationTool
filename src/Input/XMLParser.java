package Input;

import CableTree.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import com.sun.prism.shader.Solid_TextureFirstPassLCD_Loader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/* Class responsible to create CableTree object from given xml-File. Ti uses the dom xml parser of Java. */
/* Tutorial followed for dom parser: https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/  */
public class XMLParser {

    private String xmlpath;
    private File fXmlFile;
    private  DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;

    public XMLParser(String path){
        this.xmlpath = path;
        this.fXmlFile = new File(path);
        this.dbFactory = DocumentBuilderFactory.newInstance();
        try{
            this.dBuilder = dbFactory.newDocumentBuilder();
            this.doc = dBuilder.parse(fXmlFile);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public CableTree parseXMLFile(){
        //Optionally normalize, but must not be done
        doc.getDocumentElement().normalize();

        //For testing purpose
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        Palette palette = parsePalette();
        List<Housing> housings = parseHousings();
        List<Cavity> cavities = parseCavities(doc.getDocumentElement());
        //TODO: error handling if null is returned!

        return new CableTree(palette, housings, cavities);
    }

    /*
    Returns the pallet object to the xml file specified in the XMLParser constructor.
    Returns null if palette cannot be parsed!
     */
    private Palette parsePalette(){
        Node nNode = doc.getElementsByTagName("PalletType").item(0);
        if (nNode.getNodeType() == Node.ELEMENT_NODE){
            Element eElement = (Element) nNode;

            String name = eElement.getAttribute("name");
            double x = Double.parseDouble(eElement.getElementsByTagName("X").item(0).getTextContent());
            double y = Double.parseDouble(eElement.getElementsByTagName("Z").item(0).getTextContent());
            double width = Double.parseDouble(eElement.getElementsByTagName("Width").item(0).getTextContent());
            double height = Double.parseDouble(eElement.getElementsByTagName("Height").item(0).getTextContent());

            return new Palette(name, x, y, width, height);
        }

        return null;
    }

    private List<Housing> parseHousings(){
        List<Housing> housings = new LinkedList<Housing>();
        List<Cavity> cavities = new LinkedList<Cavity>();

        //Get all <Housing> nodes that describe a housing. If done just with getElementByTagName("Housing") it would also
        //get every housing attribute outside of <Housings></Housings> tag.
        NodeList nHousings = doc.getElementsByTagName("Housings").item(0).getChildNodes();

        //Loop through the housings (oldfashion way)
        for (int i=0; i<nHousings.getLength(); i++){

            Node nNode = nHousings.item(i);
            //handle correctly parsing Element
            if (nNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;

                String name = eElement.getAttribute("name");
                String type = eElement.getElementsByTagName("Type").item(0).getTextContent();
                double x = Double.parseDouble(eElement.getElementsByTagName("X").item(0).getTextContent());
                double y = Double.parseDouble(eElement.getElementsByTagName("Z").item(0).getTextContent());
                double width = Double.parseDouble(eElement.getElementsByTagName("Width").item(0).getTextContent());
                double height = Double.parseDouble(eElement.getElementsByTagName("Height").item(0).getTextContent());
                //int angle = Integer.parseInt(eElement.getElementsByTagName("Angle").item(0).getTextContent());

                //get cavities of housing
                cavities = parseCavities(eElement);

                housings.add(new Housing(name, type, x, y, width, height, cavities));
            } else {
                //Could not pass correctly, so break (Will return null)
                System.out.println("Could not parse Node as Element in Housings at position " +  i);
                return null;
            }
        }

        return housings;
    }

    private List<Cavity> parseCavities(Element root){
        List<Cavity> cavities = new LinkedList<Cavity>();
        NodeList nCavities = root.getElementsByTagName("CavityInstance");

        for (int i=0; i<nCavities.getLength(); i++){
            Node nNode = nCavities.item(i);

            if(nNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;

                String name = eElement.getAttribute("name");
                //String type = eElement.getElementsByTagName("Type").item(0).getTextContent();
                double x = Double.parseDouble(eElement.getElementsByTagName("X").item(0).getTextContent());
                double y = Double.parseDouble(eElement.getElementsByTagName("Z").item(0).getTextContent());
                double width = Double.parseDouble(eElement.getElementsByTagName("Width").item(0).getTextContent());
                double height = Double.parseDouble(eElement.getElementsByTagName("Height").item(0).getTextContent());
                int angle = Integer.parseInt(eElement.getElementsByTagName("Angle").item(0).getTextContent());

                cavities.add(new Cavity(name, x, y, width, height, angle));
            }else{
                return null;
            }
        }

        return cavities;
    }
}
