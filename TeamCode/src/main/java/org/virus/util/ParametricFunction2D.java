package org.virus.util;
import org.virus.Advanced_Paths.Function;
import org.virus.Advanced_Paths.Node;

import java.util.Arrays;

public class ParametricFunction2D {

    private String name = "Vector";
    private Function scale;
    private Function theta;
    private Function[] components;

    /**
     * @Description for cartesian: one is the x component and two is the y component; for polar: one is the function for theta and two is the function for the magnitude
     * @param one
     * @param two
     * @param isPolar
     */
    ParametricFunction2D(Function one, Function two, boolean isPolar) {

        if (isPolar) {
            theta = one;
            scale = two;
        } else {
            components = new Function[] {one, two};
        }
    }

    ParametricFunction2D(ParametricFunction2D prevV) {

        components = prevV.getComponents();
        scale = prevV.getMag();
        theta = prevV.getTheta();
    }

    //---------- Vector Refresh ----------//
    // add in functions for adding parameterized functions

    //---------- Vector Properties ----------//

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

    //---------- Vector Operations ----------//

    public void add(ParametricFunction2D term_two) {

        Function[] two_comp = term_two.getComponents();

        components[0] = two_comp[0];
        components[1] = two_comp[1];
    }

    public void sub(ParametricFunction2D term_two) {

        add(invert(term_two));
    }

    public ParametricFunction2D invert(ParametricFunction2D term_two) {

        ParametricFunction2D iTwo = new ParametricFunction2D(term_two);
        iTwo.scale(-1);

        return iTwo;
    }

    public void zero() {

        this.scale(0);
    }

    public void scale(double scalar) {

        Node constantRoot = new Node(Node.paramType.Const, String.valueOf(scalar));

        components[0] = Function.operate(components[0], Function.makeFunction(constantRoot), "*");
        components[1] = Function.operate(components[1], Function.makeFunction(constantRoot), "*");
    }

    public double dot(ParametricFunction2D term_two) {

        double[] two_comp = term_two.getComponents();

        return ((components[0] * two_comp[0]) + (components[1] * two_comp[1]));
    }

    public double angleBetween(ParametricFunction2D v2) {

        double dotProduct = this.dot(v2);
        double magnitudeProducts = scale * v2.getMag();

        return Math.acos(dotProduct / magnitudeProducts);
    }

    public static double standardPosAngle(ParametricFunction2D v) {

        ParametricFunction2D i = new ParametricFunction2D(1d, 0d);
        ParametricFunction2D j = new ParametricFunction2D(0d, 1d);

        double iAngle = v.angleBetween(i);

        if (v.angleBetween(j) > (Math.PI / 2.0)) {

            iAngle = (Math.PI * 2.0) - iAngle;
        }

        return iAngle;
    }

    /**
     * reverses specified component of the vector
     * @param dimension
     */
    public void flipDimension(int dimension) {

        components[dimension] = Function.operate(components[dimension], Function.makeFunction(new Node(Node.paramType.Const, "-1")), "*");
    }

    /**
     * This function rotates the vector x radians counterclockwise
     * @param radians
     */
    public void rotate(double radians) {

        theta += radians % (2 * Math.PI);
        theta = theta % (2 * Math.PI);
        genComp();
    }

    public String toString() {

        String vector = name + ": <";
        vector += components[0] + ", ";
        vector += components[1] + ">";

        return vector;
    }

    public static void main(String[] args) {

        for (int angle = 0; angle < 360; angle++) {

            float theta = (float) Math.toRadians(angle);
            //ParametricFunction2D v = new ParametricFunction2D(null, null, false);
            //System.out.println("Angle: " + angle + " genAngle: " + Math.toDegrees(v.getTheta()) + " standardPosAngle: " + Math.toDegrees(standardPosAngle(v)));
        }
    }
}
