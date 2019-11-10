package org.virus.util;
import org.virus.Advanced_Paths.Function;
import org.virus.Advanced_Paths.Node;

import java.util.Arrays;
import java.util.HashMap;

public class ParametricFunction2D {

    private String name = "Vector";
    private Function scale;
    private Function theta;
    private Function[] components;
    private boolean isPolar;



    public ParametricFunction2D(Function combined, boolean polarity) {

        isPolar = polarity;

        Pair<Function, Function> newComponents = parametrize(combined);

        if (isPolar) {
            theta = newComponents.get1();
            scale = newComponents.get2();

            Pair<Function, Function> newRectangular = rectangularize(scale, theta);
            components = new Function[] {newRectangular.get1(), newRectangular.get2()};
        } else {

            components = new Function[] {newComponents.get1(), newComponents.get2()};
            Pair<Function, Function> newPolar = polarize(newComponents.get1(), newComponents.get2());
            scale = newPolar.get1();
            theta = newPolar.get2();
        }
    }

    /**
     * @Description for cartesian: one is the x component and two is the y component; for polar: one is the function for theta and two is the function for the magnitude
     * @param one
     * @param two
     * @param polarity
     */

    public ParametricFunction2D(Function one, Function two, boolean polarity) {

        isPolar = polarity;

        if (isPolar) {
            theta = one;
            scale = two;
        } else {
            components = new Function[] {one, two};
        }
    }

    public ParametricFunction2D(ParametricFunction2D prevV) {

        components = prevV.getComponents();
        scale = prevV.getMag();
        theta = prevV.getTheta();
    }

    //---------- Function Properties ----------//

    public Function getMag() {

        return scale;
    }
    public double genMag(double tVal) {

        return Math.sqrt(Math.pow(components[0].output(tVal), 2) + Math.pow(components[1].output(tVal), 2));
    }

    public Function getTheta() {

        return theta;
    }

    public double genAngle(double tVal) {
        double xCompOutput = components[0].output(tVal);
        double yCompOutput = components[1].output(tVal);
        double theta = Math.atan(yCompOutput / xCompOutput);

        if (xCompOutput < 0) {

            if (yCompOutput > 0) {

                theta += Math.PI;
            } else if (yCompOutput < 0) {

                theta -= Math.PI;
            }
        }

        return theta;
    }

    public void setName(String newName) {

        name = newName;
    }

    public Function[] getComponents() {

        return Arrays.copyOf(components, components.length);
    }

    public String getName() {

        return name;
    }

    public boolean isPolar() {

        return isPolar;
    }

    //---------- Function Operations ----------//

    public static Pair<Function, Function> parametrize(Function pathComponent) {

        Node dependent = Function.replaceVariable(pathComponent.getRoot(), pathComponent.getVariable(), "t", pathComponent.getConstantList());
        Node independent = new Node(Node.paramType.Variable, "t");

        return new Pair<Function, Function>(Function.makeFunction(independent, "t"), Function.makeFunction(dependent, "t"));
    }

    private static Pair<Function, Function> polarize(Function x, Function y) {

        Node square1 = Function.operate(x.getRoot(), new Node(Node.paramType.Const, "2"), "^"); // x^2
        Node square2 = Function.operate(y.getRoot(), new Node(Node.paramType.Const, "2"), "^"); // y^2
        Node sum = Function.operate(square1, square2, "+"); // (x^2) + (y^2)
        Node r = Function.operate(sum, new Node(Node.paramType.Const, "0.5"), "^"); // sqrt((x^2) + (y^2))

        Node diff = Function.operate(x.getRoot(), y.getRoot(), "/"); // x/y
        Node theta = Function.compose(new Node(Node.paramType.T_FUNC, Node.T_FUNC_TYPES.atan.toString()), diff); // arctan(x/y)

        return (new Pair<Function, Function>(Function.makeFunction(r, "t"), Function.makeFunction(theta, "t")));
    }

    private static Pair<Function, Function> rectangularize(Function r, Function theta) {

        Node sin = Function.compose(new Node(Node.paramType.T_FUNC, Node.T_FUNC_TYPES.sin.toString()), theta.getRoot()); // sin(theta)
        Node cos = Function.compose(new Node(Node.paramType.T_FUNC, Node.T_FUNC_TYPES.cos.toString()), theta.getRoot()); // cos(theta)

        Node x = Function.operate(r.getRoot(), cos, "*");
        Node y = Function.operate(r.getRoot(), sin, "*");

        return (new Pair<Function, Function>(Function.makeFunction(x, "t"), Function.makeFunction(y, "t")));
    }

    //---------- Parametric Operations ----------//

    public void add(ParametricFunction2D term_two) {

        Function[] two_comp = term_two.getComponents();

        components[0] = Function.operate(components[0], two_comp[0], Function.operation.addition);
        components[1] = Function.operate(components[1], two_comp[1], Function.operation.addition);
    }

    public void sub(ParametricFunction2D term_two) {

        add(invert(term_two));
    }

    public ParametricFunction2D invert(ParametricFunction2D term_two) {

        ParametricFunction2D iTwo = new ParametricFunction2D(term_two);
        iTwo.scale(-1);

        return iTwo;
    }

    public void scale(double scalar) {

        Node constantRoot = new Node(Node.paramType.Const, String.valueOf(scalar));

        components[0] = Function.operate(components[0], Function.makeFunction(constantRoot, components[0].getVariable()), Function.operation.multiplication);
        components[1] = Function.operate(components[1], Function.makeFunction(constantRoot, components[1].getVariable()), Function.operation.multiplication);
    }

    public double dot(ParametricFunction2D term_two, double tVal) {

        Function[] two_comp = term_two.getComponents();

        return ((components[0].output(tVal) * two_comp[0].output(tVal)) + (components[1].output(tVal) * two_comp[1].output(tVal)));
    }

    public double angleBetween(ParametricFunction2D f2, double tVal) {

        double dotProduct = this.dot(f2, tVal);
        double magnitudeProducts = scale.output(tVal) * f2.getMag().output(tVal);

        return Math.acos(dotProduct / magnitudeProducts);
    }

    public static double standardPosAngle(ParametricFunction2D v, double tVal) {

        Vector2D i = new Vector2D(1d, 0d);
        Vector2D j = new Vector2D(0d, 1d);

        Function const1 = Function.makeFunction(new Node(Node.paramType.Const, "1"), v.getComponents()[0].getVariable());
        Function const0 = Function.makeFunction(new Node(Node.paramType.Const, "0"), v.getComponents()[1].getVariable());
        ParametricFunction2D iVector = new ParametricFunction2D(const1, const0, false);
        ParametricFunction2D jVector = new ParametricFunction2D(const0, const1, false);

        double iAngle = v.angleBetween(iVector, tVal);

        if (v.angleBetween(jVector, tVal) > (Math.PI / 2.0)) {

            iAngle = (Math.PI * 2.0) - iAngle;
        }

        return iAngle;
    }

    /**
     * reverses specified component of the vector
     * @param dimension
     */
    public void flipDimension(int dimension) {

        components[dimension] = Function.operate(components[dimension], Function.makeFunction(new Node(Node.paramType.Const, "-1"), components[dimension].getVariable()), Function.operation.multiplication);
    }

    /**
     * This function rotates the function x radians counterclockwise
     * @param radians
     */
    public static ParametricFunction2D rotate(ParametricFunction2D originalFunc, double radians) {

        Function newTheta = null;
        Function newScale = null;

        Function thetaOffset = new Function( "t + " + radians, "t", new HashMap<String, Double>());

        newTheta = Function.makeFunction(Function.compose(Function.makeFunctionCopy(originalFunc.getTheta()), thetaOffset), "t");
        newScale = Function.makeFunctionCopy(originalFunc.getMag());

        if (originalFunc.isPolar()) {

            return (new ParametricFunction2D(newTheta, newScale, true));
        } else {

            Pair<Function, Function> rectangularComponents = ParametricFunction2D.rectangularize(newScale, newTheta);
            return (new ParametricFunction2D(rectangularComponents.get1(), rectangularComponents.get2(), false));
        }
    }

    public String toString() {

        String vector = name + ": <";
        vector += components[0] + ", ";
        vector += components[1] + ">";

        return vector;
    }

    public static void main(String[] args) {

        Function parabola = new Function("(x ^ 2)", "x", new HashMap<String, Double>());
        ParametricFunction2D parabolaParametric = new ParametricFunction2D(parabola, false);
    }
}
