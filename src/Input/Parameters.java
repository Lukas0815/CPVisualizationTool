package Input;

import CableTree.Position;

public class Parameters {
    //Blocking
    public static int blockingAngle = 25;
    public static int blockingH = 32;
    //Diagonally close
    public static int diagV = 32;
    public static int diagH = 32;
    //Short one-sided cable
    public static int shortAngle = 10;
    public static int shortL = 7000;
    //Chamber Triplet
    public static int chamberH = 25;

    private static int factor = 2;

    //CableStorePosition
    public static Position zetaCableStorePosition = new Position(390 * factor, 200 * factor);
    public static Position omegaCableStorePosition = new Position(470, 200);
}
