package org.virus.util;
import android.graphics.Point;

import org.virus.Advanced_Paths.Function;
import org.virus.Advanced_Paths.Node;

import java.util.HashMap;

public class ParametricFunction2D {

    private Pair<Function, Function> polarComponents;
    private Pair<Function, Function> rectangularComponents;

    /**
     * @Description
     * @param combined
     * @param isPolar
     */
    public ParametricFunction2D(Function combined, boolean isPolar) {

        if (isPolar) {
            polarComponents = parametrize(combined, isPolar);
            rectangularComponents = rectangularize(polarComponents.get1(), polarComponents.get2());
        } else {
            rectangularComponents = parametrize(combined, isPolar);
            polarComponents = polarize(rectangularComponents.get1(), rectangularComponents.get2());
        }
    }

    /**
     * @Description for cartesian: one is the x component and two is the y component; for polar: one is the function for magnitude and two is the function for theta
     * @param one
     * @param two
     * @param isPolar
     */

    public ParametricFunction2D(Function one, Function two, boolean isPolar) {

        if (isPolar) {

            polarComponents = new Pair<Function, Function>(one, two);
            rectangularComponents = rectangularize(one, two);
        } else {

            rectangularComponents = new Pair<Function, Function>(one, two);
            polarComponents = polarize(one, two);
        }
    }

    public ParametricFunction2D(ParametricFunction2D oldFunc) {

        rectangularComponents = oldFunc.getRectangularComponents();
        polarComponents = oldFunc.getPolarComponents();
    }

    //---------- Function Properties ----------//
    public double genMag(double tVal) {

        return Math.sqrt(Math.pow(rectangularComponents.get1().output(tVal), 2) + Math.pow(rectangularComponents.get2().output(tVal), 2));
    }

    public double genAngle(double tVal) {
        double xCompOutput = rectangularComponents.get1().output(tVal);
        double yCompOutput = rectangularComponents.get2().output(tVal);
        double theta = 0;

        if (xCompOutput == 0 && yCompOutput == 0) {

            theta = 0;
        } else {

            if (xCompOutput == 0) {

                theta = Math.PI / 2;

                if (yCompOutput < 0) {
                    theta *= -1;
                }
            } else {

                theta = Math.atan(yCompOutput / xCompOutput);

                if (xCompOutput < 0) {

                    if (yCompOutput >= 0) {

                        theta += Math.PI;
                    } else if (yCompOutput < 0) {

                        theta -= Math.PI;
                    } else {

                        theta = Math.PI;
                    }
                }
            }
        }

        return theta;
    }

    public Pair<Function, Function> getRectangularComponents() {
        return (new Pair<Function, Function>(Function.makeFunctionCopy(rectangularComponents.get1()), Function.makeFunctionCopy(rectangularComponents.get2())));
    }

    public Pair<Function, Function> getPolarComponents() {
        return (new Pair<Function, Function>(Function.makeFunctionCopy(polarComponents.get1()), Function.makeFunctionCopy(polarComponents.get2())));
    }

    //---------- Function Operations ----------//

    public static Pair<Function, Function> parametrize(Function pathComponent, boolean isPolar) {

        Node dependent = Function.replaceVariable(pathComponent.getRoot(), "t", pathComponent.getVariable(), pathComponent.getConstantList());
        Node independent = new Node(Node.paramType.Variable, "t");

        if (isPolar) {
            return new Pair<Function, Function>(Function.makeFunction(dependent, "t"), Function.makeFunction(independent, "t"));
        } else {
            return new Pair<Function, Function>(Function.makeFunction(independent, "t"), Function.makeFunction(dependent, "t"));
        }
    }

    public static Pair<Function, Function> polarize(Function x, Function y) {

        Node square1 = Function.operate(x.getRoot(), new Node(Node.paramType.Const, "2"), "^"); // x^2
        Node square2 = Function.operate(y.getRoot(), new Node(Node.paramType.Const, "2"), "^"); // y^2
        Node sum = Function.operate(square1, square2, "+"); // (x^2) + (y^2)
        Node r = Function.operate(sum, new Node(Node.paramType.Const, "0.5"), "^"); // sqrt((x^2) + (y^2))
        r = Function.operate(Function.composeTFUNC(x.getRoot(), "t", Node.T_FUNC_TYPES.sgn), r, "*");

        Node diff = Function.operate(y.getRoot(), x.getRoot(), "/"); // y/x
        Node theta = Function.composeTFUNC(diff, "t", Node.T_FUNC_TYPES.atan); // arctan(y/x)

        return (new Pair<Function, Function>(Function.makeFunction(r, "t"), Function.makeFunction(theta, "t")));
    }

    public static Pair<Function, Function> rectangularize(Function r, Function theta) {

        Node sin = Function.composeTFUNC(theta.getRoot(), "t", Node.T_FUNC_TYPES.sin); // sin(theta)
        Node cos = Function.composeTFUNC(theta.getRoot(), "t", Node.T_FUNC_TYPES.cos); // cos(theta)

        Node x = Function.operate(r.getRoot(), cos, "*");
        Node y = Function.operate(r.getRoot(), sin, "*");

        return (new Pair<Function, Function>(Function.makeFunction(x, "t"), Function.makeFunction(y, "t")));
    }

    //---------- Parametric Operations ----------//

    public void add(ParametricFunction2D term_two) {

        Pair<Function, Function> two_comp = term_two.getRectangularComponents();
        rectangularComponents.setT1(Function.operate(rectangularComponents.get1(), two_comp.get1(), Function.operation.addition));
        rectangularComponents.setT2(Function.operate(rectangularComponents.get2(), two_comp.get2(), Function.operation.addition));

        polarComponents = polarize(rectangularComponents.get1(), rectangularComponents.get2());
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
        rectangularComponents.setT1(Function.operate(rectangularComponents.get1(), Function.makeFunction(constantRoot, rectangularComponents.get1().getVariable()), Function.operation.multiplication));
        rectangularComponents.setT2(Function.operate(rectangularComponents.get2(), Function.makeFunction(constantRoot, rectangularComponents.get2().getVariable()), Function.operation.multiplication));

        polarComponents = polarize(rectangularComponents.get1(), rectangularComponents.get2());
    }

    public double dot(ParametricFunction2D term_two, double tVal) {

        Pair<Function, Function> two_comp = term_two.getRectangularComponents();

        return ((rectangularComponents.get1().output(tVal) * two_comp.get1().output(tVal)) + (rectangularComponents.get2().output(tVal) * two_comp.get2().output(tVal)));
    }

    public double angleBetween(ParametricFunction2D f2, double tVal) {

        double dotProduct = this.dot(f2, tVal);
        double magnitudeProducts = polarComponents.get1().output(tVal) * f2.getPolarComponents().get1().output(tVal);

        return Math.acos(dotProduct / magnitudeProducts);
    }

    public static double standardPosAngle(ParametricFunction2D v, double tVal) {

        Vector2D i = new Vector2D(1d, 0d);
        Vector2D j = new Vector2D(0d, 1d);

        Function const1 = Function.makeFunction(new Node(Node.paramType.Const, "1"), v.getRectangularComponents().get1().getVariable());
        Function const0 = Function.makeFunction(new Node(Node.paramType.Const, "0"), v.getRectangularComponents().get2().getVariable());
        ParametricFunction2D iVector = new ParametricFunction2D(const1, const0, false);
        ParametricFunction2D jVector = new ParametricFunction2D(const0, const1, false);

        double iAngle = v.angleBetween(iVector, tVal);

        if (v.angleBetween(jVector, tVal) > (Math.PI / 2.0)) {

            iAngle = (Math.PI * 2.0) - iAngle;
        }

        return iAngle;
    }

    /**
     * reverses the first rectangular component of the parametric function, or reflects over the Y axis
     */
    public void flipOverY() {
        rectangularComponents.setT1(Function.operate(rectangularComponents.get1(), Function.makeFunction(new Node(Node.paramType.Const, "-1"), rectangularComponents.get1().getVariable()), Function.operation.multiplication));
    }

    /**
     * reverses the second rectangular component of the parametric function, or reflects over the X axis
     */
    public void flipOverX() {
        rectangularComponents.setT2(Function.operate(rectangularComponents.get2(), Function.makeFunction(new Node(Node.paramType.Const, "-1"), rectangularComponents.get2().getVariable()), Function.operation.multiplication));
    }

    /**
     * This function rotates the function x radians counterclockwise
     * @param radians
     */
    public static ParametricFunction2D rotate(ParametricFunction2D originalFunc, double radians) {

        Function newScale = Function.makeFunctionCopy(originalFunc.getPolarComponents().get1());
        Function newTheta = Function.makeFunction(Function.operate(Function.makeTreeCopy(originalFunc.getPolarComponents().get2().getRoot()), new Node(Node.paramType.Const, Double.toString(radians)), "+"), "t");

        return (new ParametricFunction2D(newScale, newTheta, true));
    }

    public static ParametricFunction2D derivativeParametric(ParametricFunction2D originalFunc) {

        Function derivativeyt = Function.simplify(Function.derivative(originalFunc.getRectangularComponents().get2()));
        Function derivativext = Function.simplify(Function.derivative(originalFunc.getRectangularComponents().get1()));

        return (new ParametricFunction2D(derivativeyt, derivativext, false));
    }

    public static Function derivative(ParametricFunction2D originalFunc) {

        ParametricFunction2D derivative = derivativeParametric(originalFunc);

        return (Function.operate(derivative.getRectangularComponents().get2(), derivative.getRectangularComponents().get1(), Function.operation.division));
    }

    //---------- Function outputs ----------//

    public static Vector2D output(ParametricFunction2D originalFunc, double tValue) {

        double x = originalFunc.getRectangularComponents().get1().output(tValue);
        double y = originalFunc.getRectangularComponents().get2().output(tValue);

        return (new Vector2D(x, y));
    }

    public static Vector2D derivativeParametric(ParametricFunction2D originalFunc, double tValue) {
        ParametricFunction2D parametricDerivative = ParametricFunction2D.derivativeParametric(originalFunc);

        double x = parametricDerivative.getRectangularComponents().get1().output(tValue);
        double y = parametricDerivative.getRectangularComponents().get2().output(tValue);

        return (new Vector2D(x, y));
    }

    public static double derivative(ParametricFunction2D originalFunc, double tValue) {
        Function derivative = ParametricFunction2D.derivative(originalFunc);

        return derivative.output(tValue);
    }

    public String toString() {

        String vector = "<";
        vector += rectangularComponents.get1() + ", ";
        vector += rectangularComponents.get2() + ">";

        return vector;
    }

    public static void main(String[] args) {

        Function parabola = new Function("(x ^ 2)", "x", new HashMap<String, Double>());
        ParametricFunction2D parabolaParametric = new ParametricFunction2D(parabola, false);
    }
}
