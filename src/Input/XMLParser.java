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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/* Class responsible to create CableTree object from given xml-File. Ti uses the dom xml parser of Java. */
/* Tutorial followed for dom parser: https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/  */
public class XMLParser {

    public static int scale = 6;

    private String xmlpath;
    private File fXmlFile;
    private  DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;
    private HashMap<String, Housing> housingMap;
    private List<Cavity> activeCavities;

    public XMLParser(String path){
        this.activeCavities = new LinkedList<Cavity>();
        this.housingMap = new HashMap<String, Housing>();
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
        List<Cavity> cavities = new LinkedList<Cavity>();

        for (Housing h : housings){
            cavities.addAll(h.getCavities());
        }
        List<Wire> wires = parseCables();
        //TODO: error handling if null is returned!

        return new CableTree(palette, housings, cavities, wires, this.activeCavities);
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

            return new Palette(name, x * scale, y *scale, width*scale, height*scale);
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
                y = y < 0 ? -y : y;
                double width = Double.parseDouble(eElement.getElementsByTagName("Width").item(0).getTextContent());
                double height = Double.parseDouble(eElement.getElementsByTagName("Height").item(0).getTextContent());

                //Extract correct height and width from Housing type since in Housings both are set to 0
                NodeList typeList = doc.getElementsByTagName("HousingType");
                for(int j=0; j<typeList.getLength(); j++){
                    Node nHousingType = typeList.item(j);

                    if(nHousingType.getNodeType() == Node.ELEMENT_NODE){
                        Element eHousingType = (Element) nHousingType;

                        String typeName = eHousingType.getAttribute("name");
                        if (typeName.equals(type)){
                            width = Double.parseDouble(eHousingType.getElementsByTagName("Width").item(0).getTextContent());
                            height = Double.parseDouble(eHousingType.getElementsByTagName("Height").item(0).getTextContent());
                            break;
                        }
                    }
                }


                int angle = Integer.parseInt(eElement.getElementsByTagName("Angle").item(0).getTextContent());

                //get cavities of housing
                Housing h = new Housing(name, type, x*scale, y*scale, width*scale, height*scale, null, angle);
                cavities = parseCavities(eElement, h);
                h.setCavities(cavities);


                housings.add(h);
                housingMap.put(name, h);
            } else {
                //Could not pass correctly, so break (Will return null)
                System.out.println("Could not parse Node as Element in Housings at position " +  i);
                return null;
            }
        }

        return housings;
    }

    private List<Cavity> parseCavities(Element root, Housing h){
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
                y = y < 0 ? -y : y;
                double width = Double.parseDouble(eElement.getElementsByTagName("Width").item(0).getTextContent());
                double height = Double.parseDouble(eElement.getElementsByTagName("Height").item(0).getTextContent());
                int angle = Integer.parseInt(eElement.getElementsByTagName("Angle").item(0).getTextContent());

                cavities.add(new Cavity(name, x*scale, y*scale, width*scale, height*scale, angle, h));
            }else{
                return null;
            }
        }

        return cavities;
    }

    private List<Wire> parseCables(){
        List<Wire> wireList = new LinkedList<Wire>();
        NodeList nLeadSets = doc.getElementsByTagName("LeadSet");

        for (int i=0; i<nLeadSets.getLength(); i++){
           try{
               Node nNode = nLeadSets.item(i);

               if(nNode.getNodeType() == Node.ELEMENT_NODE){
                   Element eElement = (Element) nNode;

                   Element beginning = (Element) eElement.getChildNodes().item(0);
                   String startHousingName = beginning.getElementsByTagName("Housing").item(0).getTextContent();
                   String startCavityName = beginning.getElementsByTagName("Cavity").item(0).getTextContent();
                   Cavity startCavity = this.housingMap.get(startHousingName).getCavity(startCavityName);

                   Element ending = (Element) eElement.getChildNodes().item(1);
                   String endHousingName = ending.getElementsByTagName("Housing").item(0).getTextContent();
                   String endCavityName = ending.getElementsByTagName("Cavity").item(0).getTextContent();
                   Cavity endCavity = this.housingMap.get(endHousingName).getCavity(endCavityName);

                   int length = Integer.parseInt(eElement.getElementsByTagName("Length").item(0).getTextContent());
                   String type = eElement.getElementsByTagName("Type").item(0).getTextContent();

                   if(startCavity == null || endCavity == null){
                       System.out.println("ERROR: could not get cavity for wire!");
                       continue;
                   }
                    startCavity.setActive(true);
                    endCavity.setActive(true);
                    Wire w = new Wire(length, type, startCavity, endCavity);
                    wireList.add(w);

                    this.activeCavities.add(startCavity);
                    this.activeCavities.add(endCavity);
                }


           } catch (Exception e){
               System.out.println("ERROR: could not create wire!");
               continue;
           }
        }

        return wireList;
    }
}
