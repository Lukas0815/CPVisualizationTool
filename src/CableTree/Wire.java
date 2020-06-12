package CableTree;

public class Wire {

    private int length;
    private String type;
    private Cavity[] cavities;  //Start and end cavity

    public Wire(int length, String type, Cavity start, Cavity end){
        this.length = length;
        this.type = type;
        this.cavities = new Cavity[2];
        this.cavities[0] = start;
        this.cavities[1] = end;
    }

    @Override
    public String toString() {
        return "Wire: <" + cavities[0] + "," + cavities[1] + ">";
    }
}
