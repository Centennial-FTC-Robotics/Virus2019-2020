package org.virus.Advanced_Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.virus.util.Pair;

public class Function {

    /*
    * RULES:
    *  - all constants will not have parentheses around them
    *  - there will be parentheses around every single operation ((ex. (2 * x) / (10.2)))
    *  - between operations and constants there will always be spaces (ex. 1 + x, not 1+x)
    * */

    public enum operation {addition, subtraction, multiplication, division, exponent};
    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, ArrayList<Integer>>>> operationsTree; // ArrayList<Pair<Bounds<Start, End>, Relatives<Parent, Children<Positions>>>>

    Function(String function, char variable, HashMap<Character, Double> constants) {

        parse(function, variable);
    }

    private ArrayList<Pair<operation, Pair<Function, Function>>> parse(String function, char variable) {

        function = function.substring(1, function.length() - 1); // remove garuanteed start and end parentheses
        ArrayList<Pair<Integer, Integer>> subFunctions = new ArrayList<Pair<Integer, Integer>>();
        Pair<Integer, Integer> currentPair = new Pair<>(0, 0);
        int parenthesesDepth = 0;

        for (int c = 0; c < function.length(); c++) {

            if (function.substring(c, c + 1).equals("(")) {

                if (parenthesesDepth == 0) {
                    currentPair.setT1(c);
                }

                parenthesesDepth++;
            }

            if (function.substring(c, c + 1).equals(")")) {

                parenthesesDepth--;
            }

            if (parenthesesDepth == 0) {
                currentPair.setT2(c);
                subFunctions.add(currentPair);
                currentPair = new Pair<>(0, 0);
            }
        }

        if (subFunctions.size() == 0) {

            if (function.contains(String.valueOf(variable))) {


            } else {

            }
        } else {

            for (int sf = 0; sf < subFunctions.size(); sf++) {


            }
        }

        return null;
    }


    public double output(int input) {

        double output = 0;



        return output;
    }

    public static void main(String[] args) {

        String polynomial = "(x ^ {3})";
        String multipleInputPoint = "((ln(x)) / (4)) + (12.34 * (x + p))";

        HashMap<Character, Double> MIPconsts = new HashMap<Character, Double>();
        MIPconsts.put('p', 500.2);

        Function MIP = new Function(multipleInputPoint, 'x', MIPconsts);
    }
}
