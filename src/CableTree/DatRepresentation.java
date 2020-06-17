package CableTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatRepresentation {

    private List<String> atomics, softAtomics, disjunctives, directSuccs;
    private List<String> allConstraints;
    private int k, b;
    private Map<String, Integer> cavFreqMap;

    public DatRepresentation(List<String> atomics, List<String> softAtomics, List<String> disjunctives, List<String> directSuccs, int k, int b) {
        this.atomics = atomics;
        this.softAtomics = softAtomics;
        this.disjunctives = disjunctives;
        this.directSuccs = directSuccs;
        this.k = k;
        this.b = b;

        this.allConstraints = new LinkedList<>();
        this.allConstraints.addAll(this.atomics);
        this.allConstraints.addAll(this.softAtomics);
        this.allConstraints.addAll(this.disjunctives);
        //TODO: convert directs first
        //this.allConstraints.addAll(this.directSuccs);

        makeFrequencyMap();
    }

    /*
    Create Map that holds amount of cavity representation and cavity representation.
    Helps the matching algorithm.
     */
    private void makeFrequencyMap(){
        this.cavFreqMap = new HashMap<>();

        //parse atomics and softatomics
        List<String> toCheck = new LinkedList<>();
        toCheck.addAll(this.atomics);
        toCheck.addAll(this.softAtomics);

        for (String s : toCheck){

            String firstHelp = s.split(",")[0];
            int firstStart = s.indexOf('<') +1;
            String first = firstHelp.substring(firstStart, firstHelp.length());

            String secondHelp = s.split(",")[1];
            int secondEnd = secondHelp.indexOf(">");
            String second = secondHelp.substring(0, secondEnd).trim();

           addToFreqMap(first);
           addToFreqMap(second);
        }

        //parse disjunctives
        for (String s : this.disjunctives){
            String cavs[] = s.split(",");

            String first = cavs[0].substring(s.indexOf("<") +1, cavs[0].length());
            String second = cavs[1].trim();
            String third = cavs[2].trim();
            String fourth = cavs[3].substring(0, cavs[3].indexOf(">")).trim();

            //System.out.println(first + " " + second + " " + third + " " + fourth);
            addToFreqMap(first);
            addToFreqMap(second);
            addToFreqMap(third);
            addToFreqMap(fourth);
        }

        //parse direct successors
        for (String s : this.directSuccs){
            String intString = s.trim();
            int right = Integer.parseInt(intString);
            int left = right > this.b ? right - this.b : right + b;

            addToFreqMap(Integer.toString(right));
            addToFreqMap(Integer.toString(left));
        }

        System.out.println(cavFreqMap);
    }

    private void addToFreqMap(String s){
        if (!cavFreqMap.containsKey(s)){
            cavFreqMap.put(s, 1);
        } else {
            cavFreqMap.put(s, cavFreqMap.get(s) +1);
        }
    }

    public Map<String, Integer> getCavFreqMap() {
        return cavFreqMap;
    }
}
