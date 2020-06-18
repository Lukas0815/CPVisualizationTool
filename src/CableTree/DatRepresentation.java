package CableTree;

import Constraints.Constraint;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

    public boolean matchWithCableTree(CableTree cableTree){

        //inverse map since I would like to search representation given an amount
        Map<Integer, List<String>> datCavs = createReprMap(this.cavFreqMap);
        Map<Cavity, Integer> cavFreqCT = cableTree.getCavFreqMap();

        //maps to store information that could not be matched!
        List<List<String>> mDats = new LinkedList<>();
        List<Cavity> mCavs = new LinkedList<Cavity>();

        System.out.println("datCavs: " + datCavs);

        //Match via frequency
        for (Cavity c : cableTree.getCavities()){
            int amount = cavFreqCT.get(c);
            List<String> datRepr = datCavs.get(amount);

            if (datRepr == null) {
                System.out.println("Error number 5");
                continue;
                //return false;
            }

            if (datRepr.size() == 1){
                c.setReprStr(datRepr.get(0));
            } else {
                //multiple cavities have are used this often in the constraints --> perform extra matching
                //Save these cases for further investigation afterwards
                mDats.add(datRepr);
                mCavs.add(c);
            }
        }

        //Check if everything has been matched
        if (mCavs.isEmpty()) return true;

        List<String> result = new LinkedList<>();

        //Match constraints that could not be matched by taking constraints into account
        for (Constraint c : cableTree.getConstraints()){
            //only consider constraints where cavity is unclear
            if (c.getSource().getReprStr() != null && c.getAffected().getReprStr() != null)
                continue;

            if (c.getSource().getReprStr() == null){
                //Loop through dat-Strings of constraints and match <*, secCav.getReprStr()>
                List<String> atomicType = new LinkedList<String>(this.atomics);
                atomicType.addAll(this.atomics);
                atomicType.addAll(this.softAtomics);

                for (String s : atomicType){
                    String cavs[] = s.split(",");
                    if (cavs[1].contains(c.getAffected().getReprStr()))
                        result.add(cavs[0]); //TODO: Maybe format string so that "< " is not included?
                }

                for (String s: disjunctives){
                    String cavs[] = s.split(",");

                    if (cavs[1].contains(c.getAffected().getReprStr()))
                        result.add(cavs[0]); //TODO: format string
                    if (cavs[3].contains(c.getAffected().getReprStr()))
                        result.add(cavs[2]);
                }

                for (String s : directSuccs){
                    if (s.contains(c.getAffected().getReprStr()))
                            result.add(directSuccPre(s));
                }


                //now count the first cavs frequency
                Map<String, Integer> firstCount = new HashMap();
                for (String s : result){
                    if (firstCount.containsKey(s))
                        firstCount.put(s, firstCount.get(s) +1);
                    else
                        firstCount.put(s, 1);
                }

                Map<Integer, String> firstCountInv = invert(firstCount);

                //set the value if amount matches now
                for (Cavity c : mCavs){
                    if (!firstCountInv.containsKey(cavFreqCT.get(c)))
                        continue;

                    c.setReprStr(firstCountInv.get(cavFreqCT.get(c)));
                }

                
            } else if (c.getAffected().getReprStr() == null){
                //TODO: loop through dat-Strings and match <firstCav.getReprStr(), *>
            }
        }



        return false;
    }

    private static Map<Integer, List<String>> createReprMap(Map<String, Integer> map) {

        Map<Integer, List<String>> inv = new HashMap<Integer, List<String>>();

        for (Entry<String, Integer> entry : map.entrySet())
            if (inv.containsKey(entry.getValue()))
                inv.get(entry.getValue()).add(entry.getKey());
            else
                inv.put(entry.getValue(), List.of(entry.getKey()));

        return inv;
    }

    private static <V, K> Map<V, K> invert(Map<K, V> map) {

        Map<V, K> inv = new HashMap<V, K>();

        for (Entry<K, V> entry : map.entrySet())
            inv.put(entry.getValue(), entry.getKey());

        return inv;
    }

    /*
    Given just the direct successor string as found in dat ("x,"), extract the other part of the constraint
     */
    private String directSuccPre(String s){
        int sInt = Integer.parseInt(s);

        return sInt > this.b ? String.valueOf(sInt - this.b) : String.valueOf(sInt + this.b);
    }
}
