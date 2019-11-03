package org.virus.Advanced_Paths;


import org.virus.util.FunctionFormatException;
import org.virus.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Function {

    /*
     * RULES:
     *  - no single constants or variables will have parentheses around them
     *  - there will be parentheses around every single operation ((ex. (2 * x) / (10.2)))
     *  - between operations and constants there will always be spaces (ex. 1 + x, not 1+x)
     *  - don't put in any constants that don't correspond to a constant in the hashmap, a format exception will be thrown
     * */

    public enum operation {addition, subtraction, multiplication, division, exponent};
    private String funcVariable;
    private HashMap<String, Double> constantList;
    private ArrayList<Node> operationsTree; // ArrayList<Pair<Bounds<Start, End>, Relatives<Parent, Children<Positions>>>>

    Function(String function, String variable, HashMap<String, Double> newConstants) {

        funcVariable = variable;
        constantList = newConstants;
        operationsTree = parse(function, funcVariable);
    }

    Function(ArrayList<Node> oldFunctionTree) {

        constantList = new HashMap<String, Double>();
        operationsTree = makeTreeCopy(getRoot(oldFunctionTree));
    }

    public ArrayList<Node> getOperationsTree() {
        return makeTreeCopy(getRoot(operationsTree));
    }

    public Node getRoot() {
        return getRoot(operationsTree).linkedClone();
    }

    private ArrayList<Node> parse(String function, String variable) {
        ArrayList<Node> functionStructure = new ArrayList<Node>();

        // checking for smaller functions inside
        System.out.println("\n+ New Function: " + function);
        function = function.substring(1, function.length() - 1); // remove garuanteed start and end parentheses
        System.out.println("Stripped function: " + function + "\n");
        ArrayList<Pair<Integer, Integer>> subFunctions = getSubFuncIndex(function);

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

            if (components.length == 1) {

                Node lone = null;

                try {
                    lone = genNode(components[0]);

                    if (lone.getType() == Node.paramType.Operation) {

                        throw new FunctionFormatException("Syntax error, function/subfunction found to literally just be an operator", (new Exception()).getCause());
                    }
                } catch (FunctionFormatException e) {
                    e.printStackTrace();
                }

                functionStructure.add(lone);
            } else {
                Node child1 = null;
                Node child2 = null;

                try {
                    child1 = genNode(components[0]);
                    child2 = genNode(components[2]);
                } catch (FunctionFormatException e) {
                    e.printStackTrace();
                }
                Node OpRelation = new Node(Node.paramType.Operation, components[1]);
                OpRelation.setChild1(child1);
                OpRelation.setChild2(child2);

                functionStructure.add(OpRelation);
                functionStructure.add(child1);
                functionStructure.add(child2);
            }
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

        System.out.println("Tree: " + functionStructure);

        return functionStructure;
    }

    private ArrayList<Pair<Integer, Integer>> getSubFuncIndex(String function) {

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

        return subFunctions;
    }

    private ArrayList<Node> linker(String variable, ArrayList<String> subFunctionLinks, ArrayList<ArrayList<Node>> subFunctionTrees, ArrayList<Pair<Integer, Integer>> subFunctionIndexes) throws FunctionFormatException {
        ArrayList<Node> linkedSubFunctions = new ArrayList<Node>();
        for (int subFunc = 0; subFunc < subFunctionTrees.size(); subFunc++) {
            ArrayList<Node> subFunction = subFunctionTrees.get(subFunc);
            System.out.println(subFunc + ": " + subFunction);
        }
        System.out.println("Links: " + subFunctionLinks);
        System.out.println("Indexes: " + subFunctionIndexes);

        for (int node = 0; node < subFunctionLinks.size(); node++) {
            String subject = subFunctionLinks.get(node);

            if ("+-*/^".contains(String.valueOf(subject))) {
                Pair<Integer, Integer> references = findLinkReferences(subFunctionIndexes, node);
                System.out.println("Link References: " + references);
                Node link = genNode(subject);
                Node funcBefore = null;
                Node funcAfter = null;

                if (node < subFunctionLinks.size()) {
                    if (node == 0) {
                        ArrayList<Node> refSubFunc = subFunctionTrees.get(references.get1());
                        funcBefore = changeNode(getRoot(refSubFunc), getRoot(refSubFunc).getVal());
                    }

                    if (references.get2() == null) {
                        if (subFunctionLinks.size() > (node + 1) && !"+-*/^".contains(subFunctionLinks.get(node + 1))) {
                            funcAfter = genNode(subFunctionLinks.get(node + 1));
                        } else {
                            throw new FunctionFormatException("No constant connected after " + subject + " operator at " + node , (new Exception()).getCause());
                        }
                    } else {
                        funcAfter = changeNode(getRoot(subFunctionTrees.get(references.get2())), getRoot(subFunctionTrees.get(references.get2())).getVal());
                    }
                }

                if (node > 0) {
                    if (node == subFunctionLinks.size() - 1) {
                        ArrayList<Node> refSubFunc = subFunctionTrees.get(references.get2());
                        funcAfter = changeNode(getRoot(refSubFunc), getRoot(refSubFunc).getVal());
                    }

                    if (references.get1() == null) {
                        if (!"+-*/^".contains(subFunctionLinks.get(node - 1))) {
                            // add in the case in which the constant is not explicitly defined
                            funcBefore = genNode(subFunctionLinks.get(node - 1));
                        } else {
                            throw new FunctionFormatException("No constant connected before " + subject + " operator at " + node , (new Exception()).getCause());
                        }
                    } else {
                        funcBefore = changeNode(getRoot(subFunctionTrees.get(references.get1())), getRoot(subFunctionTrees.get(references.get1())).getVal());
                    }
                }

                // link the subfunctions using the operator
                link.setChild1(funcBefore);
                link.setChild2(funcAfter);

                // add them to the linked subfunctions list
                System.out.println("\nFunction Before: " + funcBefore);
                System.out.println("Link: " + link);
                System.out.println("Function After: " + funcAfter + "\n");

                linkedSubFunctions.add(link);
                linkedSubFunctions.add(funcBefore);
                linkedSubFunctions.add(funcAfter);
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

        if (root == null) {
            System.out.println("Bad tree: " + tree);
        }
        return root;
    }

    public double output(double input) {

        return output(this, input);
    }

    public static double output(Function inputFunction, double input) {

        return output(inputFunction.getOperationsTree(), input);
    }

    public static double output(ArrayList<Node> operationTree, double input) {

        double output = 0;
        Node root = getRoot(operationTree);

        //child 1
        double child1Val = 0;
        if (root.getChild1() != null) {
            switch (root.getChild1().getType()) {
                case Const:
                    child1Val = Double.parseDouble(root.getChild1().getVal());
                    break;
                case Variable:
                    child1Val = input;
                    break;
                case Operation:
                    ArrayList<Node> subTree = makeTreeCopy(root.getChild1());
                    child1Val = output(subTree, input);
                    break;
            }
        }

        //child2
        double child2Val = 0;
        if (root.getChild1() != null) {
            switch (root.getChild2().getType()) {
                case Const:
                    child2Val = Double.parseDouble(root.getChild2().getVal());
                    break;
                case Variable:
                    child2Val = input;
                    break;
                case Operation:
                    ArrayList<Node> subTree = makeTreeCopy(root.getChild2());
                    child2Val = output(subTree, input);
                    break;
            }
        }

        switch(root.getType()) {
            case Const:

                output = Double.parseDouble(root.getVal());
                break;
            case Variable:

                output = input;
                break;
            case Operation:
                switch (root.getVal()) {
                    case "+":
                        output = child1Val + child2Val;
                        break;
                    case "-":
                        output = child1Val - child2Val;
                        break;
                    case "*":
                        output = child1Val * child2Val;
                        break;
                    case "/":
                        output = child1Val / child2Val;
                        break;
                    case "^":
                        output = Math.pow(child1Val, child2Val);
                        break;
                }
                break;
        }

        return output;
    }

    public Node changeNode(Node root, String newVal) throws FunctionFormatException{

        Node newRoot = genNode(newVal);

        if (root.getChild1() != null) {

            Node child1 = root.getChild1();
            root.getChild1().severParent(root);
            newRoot.setChild1(child1);
        }

        if (root.getChild2() != null) {

            Node child2 = root.getChild2();
            root.getChild2().severParent(root);
            newRoot.setChild2(child2);
        }

        return newRoot;
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
                System.out.println("List of Constants: " + constantList);
                if (constantList.containsKey(nodeVal)) {
                    Double val = constantList.get(nodeVal);
                    newNode = new Node(Node.paramType.Const, String.valueOf(val));
                    System.out.println(nodeVal + " = " + newNode.getVal());
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

        return makeFunction(getRoot(derivative(originalFunc.getOperationsTree())));
    }

    public static ArrayList<Node> derivative(ArrayList<Node> originalFunc) {

        ArrayList<Node> funcTree = makeTreeCopy(getRoot(originalFunc));
        Node dRoot = getRoot(funcTree);

        Node subRoot1 = null;
        if (dRoot.getChild1() != null) {
            switch(dRoot.getChild1().getType()) {
                case Const:
                    break;
                case Variable:
                    break;
                case Operation:
                    ArrayList<Node> subDerivative = derivative(makeTreeCopy(dRoot.getChild1()));
                    subRoot1 = getRoot(subDerivative);
                    break;
            }
        }

        Node subRoot2 = null;
        if (dRoot.getChild2() != null) {
            switch(dRoot.getChild2().getType()) {
                case Const:
                    break;
                case Variable:
                    break;
                case Operation:
                    ArrayList<Node> subDerivative = derivative(makeTreeCopy(dRoot.getChild2()));
                    subRoot2 = getRoot(subDerivative);
                    break;
            }
        }

        switch(dRoot.getType()) {
            case Const:
                break;
            case Variable:
                break;
            case Operation:
                break;
        }

        return funcTree;
    }

    public static Function inverse(Function originalFunc) {

        Function inverse = null;


        return inverse;
    }

    public static boolean FunctionTypeTester(double input) {

        boolean passed = true;

        String variable = "x";

        String polynomial = "(x ^ 3)";
        Function polynomialFunction = new Function(polynomial, variable, new HashMap<String, Double>());
        passed = (polynomialFunction.output(input) == Math.pow(input, 3));
        if (!passed) {
            return false;
        }

        String multipleInputPoint = "((x / 4) + (12.34 * (x + p)))";
        HashMap<String, Double> MIPconsts = new HashMap<String, Double>();
        MIPconsts.put("p", 500.2);
        Function MIPFunction = new Function(multipleInputPoint, variable, MIPconsts);
        passed = (MIPFunction.output(input) == ((input / 4.0) + (12.34 * (input + 500.2))));
        if (!passed) {
            return false;
        }

        String moreComplex = "(C * ((x * 4) / (((x ^ x) + 8) - C)))";
        HashMap<String, Double> mCVariables = new HashMap<String, Double>();
        mCVariables.put("C", 340.2);
        Function moreComplexFunction = new Function(moreComplex, variable, mCVariables);
        passed = (moreComplexFunction.output(input) == (340.2 * ((input * 4) / (((Math.pow(input, input)) + 8) - 340.2))));
        if (!passed) {
            return false;
        }

        String simple = "(3 * (x + 5))";
        Function simpleFunction = new Function(simple, variable, new HashMap<String, Double>());
        passed = (simpleFunction.output(input) == (3 * (input + 5)));
        if (!passed) {
            return false;
        }

        String wayMoreComplex = "((((x ^ 2) - ((3 * x) ^ 5)) + (((3 * x) ^ 0) + 2)) + ((3 ^ x) ^ 0)))";
        HashMap<String, Double> WMCVariables = new HashMap<String, Double>();
        WMCVariables.put("z", 123d);
        Function wayMoreComplexFunction = new Function(wayMoreComplex, variable, WMCVariables);
        passed = (wayMoreComplexFunction.output(input) == ((((Math.pow(input, 2)) - (Math.pow((3 * input), 5))) + ((Math.pow((3 * input), 0)) + 2)) + (Math.pow((Math.pow(3, input)), 0))));
        if (!passed) {
            return false;
        }

        String constant = "(8)";
        Function constantFunction = new Function(constant, variable, new HashMap<String, Double>());
        passed = (constantFunction.output(input) == 8);
        if (!passed) {
            return false;
        }

        String longest = "(((((3 * 5) * 45) + (x * (x ^ 3))) + (3 * (x / (3 * 9)))) * ((x / 4) ^ x))";
        Function longestFunction = new Function(longest, variable, new HashMap<String, Double>());
        passed = (longestFunction.output(input) == (((((3 * 5) * 45) + (input * (Math.pow(input, 3)))) + (3 * (input / (3.0 * 9.0)))) * (Math.pow((input / 4), input))));

        return passed;
    }

    public static void main(String[] args) {

        boolean passed = true;
        for (double x = 0.0; x <= 100; x+=0.5) {

            if (!FunctionTypeTester(x)) {
                passed = false;
                break;
            }
        }

        System.out.println(passed);
    }
}