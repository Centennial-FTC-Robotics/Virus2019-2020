package org.virus.Advanced_Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.virus.util.Pair;
import org.virus.util.UrBad;

public class Function {

    /*
    * RULES:
    *  - no single constants or variables will have parentheses around them
    *  - there will be parentheses around every single operation ((ex. (2 * x) / (10.2)))
    *  - between operations and constants there will always be spaces (ex. 1 + x, not 1+x)
    *  - don't put in any constants that don't correspond to a constant in the hashmap, a format exception will be thrown
    * */

    public enum operation {addition, subtraction, multiplication, division, exponent};
    private HashMap<Character, Double> constantList;
    private ArrayList<Node> operationsTree; // ArrayList<Pair<Bounds<Start, End>, Relatives<Parent, Children<Positions>>>>

    Function(String function, char variable, HashMap<Character, Double> newConstants) {

        constantList = newConstants;

        operationsTree = parse(function, variable);
    }

    private ArrayList<Node> parse(String function, char variable) {

        ArrayList<Node> functionStructure = new ArrayList<Node>();

        // checking for smaller functions inside
        System.out.println("New Function: " + function);
        function = function.substring(1, function.length() - 1); // remove garuanteed start and end parentheses
        System.out.println("Stripped function: " + function + "\n");
        ArrayList<Pair<Integer, Integer>> subFunctions = new ArrayList<Pair<Integer, Integer>>();
        Pair<Integer, Integer> currentPair = null;
        int parenthesesDepth = 0;

        for (int c = 0; c < function.length(); c++) {

            if (function.substring(c, c + 1).equals("(")) {

                if (parenthesesDepth == 0) {

                    currentPair = new Pair<>(0, 0);
                    currentPair.setT1(c);
                }

                parenthesesDepth++;
            }

            if (function.substring(c, c + 1).equals(")")) {

                parenthesesDepth--;
            }

            if (parenthesesDepth == 0 && currentPair != null) {

                currentPair.setT2(c + 1);
                subFunctions.add(currentPair);
                currentPair = null;
            }
        }

        System.out.print("Generated SubFunctions: ");

        for (Pair<Integer, Integer> a: subFunctions) {
            System.out.print(function.substring(a.get1(), a.get2()) + " : ");
        }

        if (subFunctions.size() == 0) {

            System.out.print("None");
        }
        System.out.println();

        if (subFunctions.size() == 0) { // this is the base case of having a combination of a total of two variables and constants w/ one connecting operation

            String[] components = function.split(" ");
            //System.out.println(Arrays.toString(components));

            Node child1 = new Node(((components[0].equals(String.valueOf(variable))) ? Node.paramType.Variable : Node.paramType.Const), components[0]);
            Node child2 = new Node(((components[2].equals(String.valueOf(variable))) ? Node.paramType.Variable : Node.paramType.Const), components[2]);

            Node OpRelation = new Node(Node.paramType.Operation, components[1]);
            OpRelation.setChild1(child1);
            OpRelation.setChild2(child2);

            child1.setParent(OpRelation);
            child2.setParent(OpRelation);

            functionStructure.add(OpRelation);
            functionStructure.add(child1);
            functionStructure.add(child2);
        } else { // unfortunately you've some smaller functions to break down

            ArrayList<Pair<ArrayList<Node>, Pair<Integer, Integer>>> subFunctionTrees = new ArrayList<Pair<ArrayList<Node>, Pair<Integer, Integer>>>();
            ArrayList<String> links = new ArrayList<String>();

            for (int sf = 0; sf < subFunctions.size(); sf++) {
                //System.out.println(function.substring(subFunctions.get(sf).get1(), subFunctions.get(sf).get2()));
                ArrayList<Node> subFunctionStructures = parse(function.substring(subFunctions.get(sf).get1(), subFunctions.get(sf).get2()), variable);

                Pair<Integer, Integer> opRef = new Pair<Integer, Integer>(null, null);
                if (sf == 0) {

                    String baseBefore = function.substring(0, subFunctions.get(sf).get1()).trim();

                    if (!baseBefore.equals("")) {

                        links.addAll(Arrays.asList(baseBefore.split(" ")));
                        opRef.setT1(new Integer(links.size() - 1));

                        System.out.println(baseBefore);
                    }
                } else {

                    String baseBetween = function.substring(subFunctions.get(sf - 1).get2(), subFunctions.get(sf).get1()).trim();

                    if (!baseBetween.equals("")) {

                        links.addAll(Arrays.asList(baseBetween.split(" ")));
                        opRef.setT1(new Integer(links.size() - 1));

                        System.out.println(baseBetween);
                    }
                }

                if (sf == subFunctions.size() - 1) {

                    String baseAfter = function.substring(subFunctions.get(sf).get2()).trim();

                    if (!baseAfter.equals("")) {

                        opRef.setT2(links.size());
                        links.addAll(Arrays.asList(baseAfter.split(" ")));
                        System.out.println(baseAfter);
                    }
                } else {

                    String baseAfter = function.substring(subFunctions.get(sf).get2(), (subFunctions.get(sf + 1).get1())).trim();

                    if (!baseAfter.equals("")) {

                        opRef.setT2(links.size());
                    }
                }

                subFunctionTrees.add(new Pair<ArrayList<Node>, Pair<Integer, Integer>>(subFunctionStructures, opRef));
            }

            // printing out subfunctions and their links
            for (int subFunc = 0; subFunc < subFunctionTrees.size(); subFunc++) {

                System.out.println("Parsed SubFunction: " + function.substring(subFunctions.get(subFunc).get1(), subFunctions.get(subFunc).get2()));
                System.out.print("SubFunction relations: " + ((subFunctionTrees.get(subFunc).get2().get1() == null) ? "NaN":links.get(subFunctionTrees.get(subFunc).get2().get1())));
                System.out.println(" : " + ((subFunctionTrees.get(subFunc).get2().get2() == null) ? "NaN":links.get(subFunctionTrees.get(subFunc).get2().get2())));
            }

            // now to put the subfunctions and the consts/variables together
            System.out.println(links);

            try {
                functionStructure = linker(variable, links, subFunctionTrees);
            } catch (UrBad e) {
                e.printStackTrace();
            }
        }

        return functionStructure;
    }

    private ArrayList<Node> linker(char variable, ArrayList<String> subFunctionLinks, ArrayList<Pair<ArrayList<Node>, Pair<Integer, Integer>>> subFunctionTrees) throws UrBad {

        ArrayList<Node> linkedSubFunctions = new ArrayList<Node>();

        int subFuncPointer = 0;

        for (int node = 0; node < subFunctionLinks.size(); node++) {

            String subject = subFunctionLinks.get(node);

            if ("+-*/^".contains(String.valueOf(subject))) {

                ArrayList<Pair<Integer, Boolean>> references = findLinkReferences(subFunctionTrees, node);
                Node link = new Node(Node.paramType.Operation, subject);

                if (references.size() == 0) {
                    if (node == 0) {

                    } else if (node == subFunctionLinks.size() - 1) {

                    } else {

                    }
                } else {

                }


            } else {

                if (subject.equals(String.valueOf(subject))) {

                } else {

                    boolean isNumber = true;

                    try {

                        Double.valueOf(subject);

                    } catch (NumberFormatException e) {

                        isNumber = false;
                    }

                    if (isNumber) {

                        Node constant = new Node(Node.paramType.Const, subject);
                    } else {

                        if (constantList.containsKey(subject)) {

                            Double val = constantList.get(subject);


                        } else {

                            throw new UrBad(new Exception().getCause());
                        }
                    }
                }
            }
        }

        return linkedSubFunctions;
    }

    private ArrayList<Pair<Integer, Boolean>> findLinkReferences(ArrayList<Pair<ArrayList<Node>, Pair<Integer, Integer>>> ObjLinkRef, int linkID) {

        ArrayList<Pair<Integer, Boolean>> refIndexes = new ArrayList<Pair<Integer, Boolean>>();

        for (int o = 0; o < ObjLinkRef.size(); o++) {

            Pair<Integer, Integer> references = ObjLinkRef.get(o).get2();

            if (references.get1() == linkID) {

                refIndexes.add(new Pair<Integer, Boolean>(o, false));
            } else if (references.get2() == linkID) {

                refIndexes.add(new Pair<Integer, Boolean>(o, true));
            }
        }

        return refIndexes;
    }

    private Node getRoot(ArrayList<Node> tree) {

        Node root = null;

        for (Node n: tree) {
            if (n.getLevel() == 0) {
                root = n;
            }
        }

        return root;
    }

    public double output(int input) {

        double output = 0;



        return output;
    }

    public static void main(String[] args) {

//        String polynomial = "(x ^ 3)";
//        String multipleInputPoint = "((ln(x)) / (4)) + (12.34 * (x + p))";
//
//        HashMap<Character, Double> MIPconsts = new HashMap<Character, Double>();
//        MIPconsts.put('p', 500.2);
//
//        Function MIP = new Function(multipleInputPoint, 'x', MIPconsts);

        String[] components = "x ^ 2".split(" ");
        char variable = 'x';
//        System.out.println(Arrays.toString(components));
//        System.out.println(((components[0].equals(String.valueOf(variable))) ? Node.paramType.Variable : Node.paramType.Const));
        Function simpleFunction = new Function("(x * (x ^ 3) + 3 * x / (3 * 9) * (x / 4) ^ x)", variable, new HashMap<Character, Double>());
        
//        try {
//            throw new UrBad((new Exception()).getCause());
//        } catch (UrBad e) {
//
//            e.printStackTrace();
//        }
    }
}
