package CableTree;

import java.util.List;

/*
This class is a model of the cable tree given via an xml file. A CableTree consists of a board on which
several housings are placed. Each housing has cavities that can be connected via wires.
 */
public class CableTree {

    private String xmlPath;

    private List<Housing> housings;
    private List<Cavity> cavities;
    private Palette palette;

    public CableTree(Palette palette, List<Housing> housings, List<Cavity> cavities){
        this.palette = palette;
        this.housings = housings;
        this.cavities = cavities;
    }
}
