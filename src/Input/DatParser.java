package Input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class DatParser {

    private File file;
    private List<String> atomics, softAtomics, disjunctives, directSuccs;
    private enum constraintType {Atomic, SoftAtomic, Disjunctive, DirectSucc, Undefined}
    private int k, b;

    public DatParser(File file) {
        this.file = file;
        this.atomics = new LinkedList<>();
        this.softAtomics = new LinkedList<>();
        this.directSuccs = new LinkedList<>();
        this.disjunctives = new LinkedList<>();
    }

    public void parse() throws FileNotFoundException {
        assert (file != null);
        constraintType ct = constraintType.Undefined;

        //Since the .dat file format is not a usual format, scan the document line by line
        Scanner scanner = new Scanner(this.file);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();

            if (s.contains("k = ")){
                String kString = s.substring(4).replace(";", "");
                this.k = Integer.parseInt(kString);
                System.out.println("k = " + this.k);
                continue;
            }

            if (s.contains("b = ")){
                String bString = s.substring(4).replace(";", "");
                this.b = Integer.parseInt(bString);
                System.out.println("b = " + this.b);
            }

            //Figure out what constraint type is on
            if (s.contains("AtomicConstraints") && !s.contains("Soft")){
                ct = constraintType.Atomic;
                continue;
            }

            if (s.contains("SoftAtomicConstraints")){
                ct = constraintType.SoftAtomic;
                continue;
            }

            if (s.contains("DisjunctiveConstraints")){
                ct = constraintType.Disjunctive;
                continue;
            }

            if (s.contains("DirectSuccessors")){
                ct = constraintType.DirectSucc;
                continue;
            }

            //Parse actual constraints
            if (!s.contains("<")) continue;

            switch (ct){
                case Atomic -> atomics.add(s);
                case DirectSucc -> directSuccs.add(s);
                case SoftAtomic -> softAtomics.add(s);
                case Disjunctive -> disjunctives.add(s);
                default -> System.out.println("Error parsing: unknown type s: " + s);
            }


        }

        System.out.println("Finished parsing .dat file! Atomics: " + atomics.size() + " direct: " + directSuccs.size());
        scanner.close();

    }
}
