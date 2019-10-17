package org.virus.Advanced_Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.virus.util.FunctionFormatException;
import org.virus.util.Pair;

public class Function {

    /*
    * RULES:
    *  - no single constants or variables will have parentheses around them
    *  - there will be parentheses around every single operation ((ex. (2 * x) / (10.2)))
    *  - between operations and constants there will always be spaces (ex. 1 + x, not 1+x)
    *  - don't put in any constants that don't correspond to a constant in the hashmap, a format exception will be thrown
    * */

    public enum operation {addition, subtraction, multiplication, division, exponent};
    private char funcVariable;
    private HashMap<Character, Double> constantList;
    private ArrayList<Node> operationsTree; // ArrayList<Pair<Bounds<Start, End>, Relatives<Parent, Children<Positions>>>>

    Function(String function, char variable, HashMap<Character, Double> newConstants) {

        funcVariable = variable;
        constantList = newConstants;
        operationsTree = parse(function, funcVariable);
        System.out.println("Tree: " + operationsTree);
    }

    Function(ArrayList<Node> oldFunctionTree) {

        constantList = new HashMap<Character, Double>();
        operationsTree = makeTreeCopy(getRoot(oldFunctionTree));
    }

    public ArrayList<Node> getOperationsTree() {
        return makeTreeCopy(getRoot(operationsTree));
    }

    public Node getRoot() {
        return getRoot(operationsTree).linkedClone();
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

        // finds the basic subfunctions through the parentheses
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
            ArrayList<ArrayList<Node>> subFunctionTrees = new ArrayList<ArrayList<Node>>();
            ArrayList<Pair<Integer, Integer>> subFunctionIndexes = new ArrayList<Pair<Integer, Integer>>();
            ArrayList<String> links = new ArrayList<String>();

            for (int sf = 0; sf < subFunctions.size(); sf++) {
                //System.out.println(function.substring(subFunctions.get(sf).get1(), subFunctions.get(sf).get2()));
                ArrayList<Node> subFunctionStructures = parse(function.substring(subFunctions.get(sf).get1(), subFunctions.get(sf).get2()), variable);
                Pair<Integer, Integer> opRef = new Pair<Integer, Integer>(null, null);

                if (sf == 0) {
                    String baseBefore = function.substring(0, subFunctions.get(sf).get1()).trim();

                    if (!baseBefore.equals("")) {
                        links.addAll(Arrays.asList(baseBefore.split(" ")));
                        opRef.setT1(links.size() - 1);

                        System.out.println("Base Before: " + baseBefore);
                    }
                } else {
                    String baseBetween = function.substring(subFunctions.get(sf - 1).get2(), subFunctions.get(sf).get1()).trim();

                    if (!baseBetween.equals("")) {
                        links.addAll(Arrays.asList(baseBetween.split(" ")));
                        opRef.setT1(links.size() - 1);

                        System.out.println("Base between: " + baseBetween);
                    }
                }

                if (sf == subFunctions.size() - 1) {
                    String baseAfter = function.substring(subFunctions.get(sf).get2()).trim();

                    if (!baseAfter.equals("")) {
                        opRef.setT2(links.size());
                        links.addAll(Arrays.asList(baseAfter.split(" ")));
                        System.out.println("Base after: " + baseAfter);
                    }
                } else {
                    String baseAfter = function.substring(subFunctions.get(sf).get2(), (subFunctions.get(sf + 1).get1())).trim();

                    if (!baseAfter.equals("")) {
                        opRef.setT2(links.size());
                    }
                }

                subFunctionTrees.add(subFunctionStructures);
                subFunctionIndexes.add(opRef);
            }

            // printing out subfunctions and their links
            for (int subFunc = 0; subFunc < subFunctionIndexes.size(); subFunc++) {
                System.out.println("Parsed SubFunction: " + function.substring(subFunctions.get(subFunc).get1(), subFunctions.get(subFunc).get2()));
                System.out.print("SubFunction relations: " + ((subFunctionIndexes.get(subFunc).get1() == null) ? "NaN":links.get(subFunctionIndexes.get(subFunc).get1())));
                System.out.println(" : " + ((subFunctionIndexes.get(subFunc).get2() == null) ? "NaN":links.get(subFunctionIndexes.get(subFunc).get2())));
            }

            // now to put the subfunctions and the consts/variables together
            System.out.println(links);

            try {
                functionStructure = linker(variable, links, subFunctionTrees, subFunctionIndexes);
            } catch (FunctionFormatException e) {
                e.printStackTrace();
            }
        }

        return functionStructure;
    }

    private ArrayList<Node> linker(char variable, ArrayList<String> subFunctionLinks, ArrayList<ArrayList<Node>> subFunctionTrees, ArrayList<Pair<Integer, Integer>> subFunctionIndexes) throws FunctionFormatException {
        ArrayList<Node> linkedSubFunctions = new ArrayList<Node>();

        for (int node = 0; node < subFunctionLinks.size(); node++) {
            String subject = subFunctionLinks.get(node);

            if ("+-*/^".contains(String.valueOf(subject))) {
                Pair<Integer, Integer> references = findLinkReferences(subFunctionIndexes, node);
                Node link = new Node(Node.paramType.Operation, subject);
                Node funcBefore = null;
                Node funcAfter = null;

                if (node < subFunctionLinks.size() - 1) {
                    if (node == 0) {
                        funcBefore = getRoot(subFunctionTrees.get(references.get1()));
                    }

                    if (references.get2() == null) {
                        if (subFunctionLinks.size() > (node + 1) && !"+-*/^".contains(subFunctionLinks.get(node + 1))) {
                            funcAfter = new Node(Node.paramType.Const, subFunctionLinks.get(node + 1));
                        } else {
                            throw new FunctionFormatException("No constant connected after " + subject + " operator at " + node , (new Exception()).getCause());
                        }
                    } else {
                        funcAfter = getRoot(subFunctionTrees.get(references.get2()));
                    }
                }

                if (node > 0) {
                    if (node == subFunctionLinks.size() - 1) {
                        funcAfter = getRoot(subFunctionTrees.get(references.get2()));
                    }

                    if (references.get1() == null) {
                        if ( (node - 1) >= 0 && !"+-*/^".contains(subFunctionLinks.get(node - 1))) {
                            // add in the case in which the constant is not explicitly defined
                            funcBefore = new Node(Node.paramType.Const, subFunctionLinks.get(node - 1));
                        } else {
                            throw new FunctionFormatException("No constant connected before " + subject + " operator at " + node , (new Exception()).getCause());
                        }
                    } else {
                        funcBefore = getRoot(subFunctionTrees.get(references.get1()));
                    }
                }

                // link the subfunctions using the operator
                link.setChild1(funcBefore);
                link.setChild2(funcAfter);

                // add them to the linked subfunctions list
                linkedSubFunctions.add(funcBefore);
                linkedSubFunctions.add(funcAfter);
                linkedSubFunctions.add(link);
            }
        }

        return linkedSubFunctions;
    }

    private static Pair<Integer, Integer> findLinkReferences(ArrayList<Pair<Integer, Integer>> ObjLinkRef, int linkID) {
        Pair<Integer, Integer> refIndexes = new Pair<Integer, Integer>(null, null);
        // the subfunction doesnt always have an operator before or after
        //System.out.println(ObjLinkRef);
        for (int o = 0; o < ObjLinkRef.size(); o++) {
            Pair<Integer, Integer> references = ObjLinkRef.get(o);

            if (references.get1() != null && (references.get1() == linkID && refIndexes.get2() == null)) {
                refIndexes.setT2(o);
            }
            if (references.get2() != null && (references.get2() == linkID && refIndexes.get1() == null)) {
                refIndexes.setT1(o);
            }
        }
        //System.out.println(linkID + ": " + refIndexes);

        return refIndexes;
    }

    private static Node getRoot(ArrayList<Node> tree) {
        Node root = null;

        for (Node n: tree) {
            if (n.getLevel() == 0) {
                root = n;
            }
        }

        return root;
    }

    public double output(double input) {
        double output = 0;



        return output;
    }

    public Node genNode(String nodeVal) throws FunctionFormatException{

        Node newNode = null;

        if ("+-*/^".contains(nodeVal)) {
            newNode = new Node(Node.paramType.Operation, nodeVal);
        }else if (nodeVal.equals(String.valueOf(funcVariable))) {
            newNode = new Node(Node.paramType.Variable, nodeVal);
        } else {
            boolean isNumber = true;

            try {
                Double.valueOf(nodeVal);
            } catch (NumberFormatException e) {
                isNumber = false;
            }

            if (isNumber) {
                newNode = new Node(Node.paramType.Const, nodeVal);
            } else {
                if (constantList.containsKey(nodeVal)) {
                    Double val = constantList.get(nodeVal);
                    newNode = new Node(Node.paramType.Const, String.valueOf(val));
                } else {
                    throw new FunctionFormatException("Constant " + nodeVal + " does not exist in constant hashmap",(new Exception()).getCause());
                }
            }
        }

        return newNode;
    }

    //---------- Public Function Operations ----------//
    public static Function makeFunction(Node root) {

        return new Function(makeTreeCopy(root));
    }

    public static Function rebuildFunction(Node root) {

        return new Function(rebuildTree(root));
    }

    public static ArrayList<Node> makeTreeCopy(Node root) {

        ArrayList<Node> newTree = new ArrayList<Node>();
        Node rootClone = root.loneClone();
        newTree.add(rootClone);

        if (root.getChild1() != null) {
            ArrayList<Node> child1Tree = makeTreeCopy(root.getChild1());
            Node child1Root = getRoot(child1Tree);
            rootClone.setChild1(child1Root);
            newTree.addAll(child1Tree);
        }
        if (root.getChild2() != null) {
            ArrayList<Node> child2Tree = makeTreeCopy(root.getChild2());
            Node child2Root = getRoot(child2Tree);
            rootClone.setChild2(child2Root);
            newTree.addAll(child2Tree);
        }

        return newTree;
    }

    public static ArrayList<Node> rebuildTree(Node root) {

        ArrayList<Node> newTree = new ArrayList<Node>();
        newTree.add(root);

        if (root.getChild1() != null) {
            newTree.addAll(rebuildTree(root.getChild1()));
        }
        if (root.getChild2() != null) {
            newTree.addAll(rebuildTree(root.getChild2()));
        }

        return newTree;
    }

    public static Function operate(Function funcOne, Function funcTwo, String operation) {

        if ("+-*/^".contains(operation)) {
            Node plusRoot = new Node(Node.paramType.Operation, operation);
            Node child1 = getRoot(makeTreeCopy(funcOne.getRoot()));
            Node child2 = getRoot(makeTreeCopy(funcTwo.getRoot()));
            plusRoot.setChild1(child1);
            plusRoot.setChild2(child2);

            return rebuildFunction(plusRoot);
        }
        return null;
    }

    public static Function derivative(Function originalFunc) {

        ArrayList<Node> funcTree = makeTreeCopy(getRoot(originalFunc.getOperationsTree()));



        return makeFunction(getRoot(funcTree));
    }

    public static Function inverse(Function originalFunc) {

        Function inverse = null;


        return inverse;
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
        Function simpleFunction = new Function("(3 * (x + 5))", variable, new HashMap<Character, Double>());
        //Function longFunction = new Function("(((((3 * 5) * 45) + (x * (x ^ 3))) + (3 * (x / (3 * 9)))) * ((x / 4) ^ x))", variable, new HashMap<Character, Double>());
    }
}
