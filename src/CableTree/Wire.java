package CableTree;

public class Wire {

    private int length;
    private String type;
    private Cavity[] cavities;  //Start and end cavity

    public Wire(int length, String type){
        //TODO: what arguments to insert to make Cavity objects?
        this.length = length;
        this.type = type;
        this.cavities = new Cavity[2];
    }
}
