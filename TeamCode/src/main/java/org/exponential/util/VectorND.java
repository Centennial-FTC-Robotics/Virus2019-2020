package org.exponential.util;
import java.util.Arrays;

public class VectorND {

    private enum types {Vector, Cartesian, Polar};
    private types type;
    private String name = "Vector";
    private double[] components;
    private double[] angles; // in radians
    private double magnitude;

    VectorND(VectorND oldV) {

        type = types.Vector;
        components = oldV.getComponents();
        angles = oldV.getAngles();
        magnitude = oldV.getMagnitude();
    }

    VectorND(double[] newComponents) {

        type = types.Cartesian;
        components = newComponents;
        reCalcMagnitude();
        reCalcAngles();
    }

    VectorND(double[] newAngles, double newMagnitude) {

        type = types.Polar;
        magnitude = newMagnitude;
        angles = newAngles;
        reCalcComponents();
    }

    //---------- Vector Refresh ----------//

    private void reCalcAngles() {

        angles = new double[components.length];

        for (int a = 0; a < angles.length; a++) {

            double dotProduct = components[a];

            angles[a] = Math.acos(dotProduct / magnitude);
        }
    }

    private void reCalcMagnitude() {

        magnitude = length(components);
    }

    private void reCalcComponents() {

        components = new double[angles.length];

        for (int a = 0; a < angles.length; a++) {

            components[a] = Math.round(Math.cos(angles[a]) * magnitude * 100000.0) / 100000.0;
        }
    }

    public void reCalc() {

        switch (type) {
            case Vector:
                reCalcMagnitude();
                break;
            case Cartesian:
                reCalcMagnitude();
                reCalcAngles();
                break;
            case Polar:
                reCalcComponents();
                break;
            default:
                break;
        }
    }

    //---------- Get Methods ----------//

    private double length(double[] legs) {

        double hypotenuse = 0;

        for (int c = 0; c < legs.length; c++)  {

            hypotenuse += Math.pow(legs[c], 2);
        }

        return Math.sqrt(hypotenuse);
    }

    private double getAngle(double component1, double component2) {

        return Math.atan(component2 / component1);
    }

    public double[] getAngles() {

        return Arrays.copyOf(angles, angles.length);
    }

    public double[] getComponents() {

        return Arrays.copyOf(components, components.length);
    }

    public double getMagnitude() {

        return magnitude;
    }

    public int dimension() {

        return components.length;
    }

    public double getComponent(int componentIndex) {

        Double component;

        if (componentIndex < components.length) {

            component = components[componentIndex];
        } else {

            component = null;
        }

        return component;
    }

    public void add(VectorND second) {

        double[] ΔVectorND = second.getComponents();

        if (this.dimension() < second.dimension()) {

            components = Arrays.copyOf(components, second.dimension());
        }

        for (int c = 0; c < second.dimension(); c++) {

            components[c] += ΔVectorND[c];
        }

        reCalcMagnitude();
        reCalcAngles();
    }

    public void sub(VectorND second) {

        add(invert(second));
    }

    public void scale(double scalar) {

        magnitude *= scalar;

        for (int c = 0; c < this.dimension(); c++) {

            components[c] *= scalar;
        }

        reCalcMagnitude();
        reCalcAngles();
    }

    public void scale(int component, double scalar) {

        if (component < components.length) {

            components[component] *= scalar;
        }

        reCalcMagnitude();
        reCalcAngles();
    }

    public void collapse(int dimension) {

        if (dimension < this.dimension()) {

            components = Arrays.copyOf(components, dimension);

            reCalcMagnitude();
            reCalcAngles();
        }
    }

    public double dot(VectorND second) {

        double dotProduct = 0;

        double[] secondComponents = second.getComponents();

        if (this.dimension() == second.dimension()) {

            for (int c = 0; c < components.length; c++) {

                dotProduct += components[c] * secondComponents[c];
            }
        } else {

            dotProduct = -1;
        }

        return dotProduct;
    }

    public double dot(double secondMagnitude, double angleBetween) {

        double dotProduct = magnitude * secondMagnitude * Math.cos(angleBetween);

        return dotProduct;
    }

    public double cross(double secondMagnitude, double angleBetween) {

        double crossProduct = magnitude * secondMagnitude * Math.sin(angleBetween);

        return crossProduct;
    }

    public double angleBetween(VectorND v2) {

        double dotProduct = this.dot(v2);
        double magnitudeProducts = magnitude * v2.getMagnitude();

        double angle = Math.toDegrees(Math.acos(dotProduct / magnitudeProducts));
        return Math.round(angle * 1000.0) / 1000.0;
    }

    public void zero() {

        components = new double[components.length];
    }

    public static VectorND invert(VectorND v) {

        VectorND i = new VectorND(v);
        i.scale(-1);

        return i;
    }

    public static double standardPosAngle(VectorND v) {

        v.collapse(2);

        VectorND i = new VectorND(new double[] {1, 0});
        VectorND j = new VectorND(new double[] {0, 1});

        double iAngle = v.angleBetween(i);

        if (v.angleBetween(j) > 90) {

            iAngle = 360 - iAngle;
        }

        return iAngle;
    }

    /**
     * This function rotates the vector in the x-y plane
     * @param degrees
     */
    public void rotate(double degrees) {
        // make this store the rest of the components and also be able to rotate in more dimensions
        this.collapse(2);

        double currentAngle = VectorND.standardPosAngle(this);

//        double[] newComponents = new double[components.length];
//
//        for (int c = 0; c < newComponents.length; c++) {
//
//            newComponents[c] =
//        }
    }

    public void flipDimension(int dimension) {

        if (dimension < components.length) {

            components[dimension] *= -1;
        }

        reCalcMagnitude();
        reCalcAngles();
    }

    public String toString() {

        String vector = name + ": <";

        for (int c = 0; c < this.dimension() - 1; c++) {

            vector += components[c] + ", ";
        }

        vector += components[this.dimension() - 1] + ">";

        return vector;
    }

    private static void VectorNDTester() {

        double[] components = {4, 5, 3, 7, 6, 6.54, 654.82667, 10};

        VectorND first = new VectorND(components);

        VectorND second = new VectorND(first.getAngles(), first.getMagnitude());
        System.out.println(second.toString());

        VectorND third = new VectorND(new double[] {0, 0, 5});
        third.add(first);

        System.out.println(third.toString());

        third.add(VectorND.invert(first));
        third.collapse(2);
        System.out.println(third);

        VectorND v = new VectorND(new double[] {3, 1});

        VectorND a = new VectorND(new double[] {1, 1});

        for (int i = 0; i < 100; i++) {

            v.add(a);
        }

        System.out.println(v);

        VectorND v1 = new VectorND(new double[] {0, 1});
        VectorND v2 = new VectorND(new double[] {2, 2});

        System.out.println(v1.angleBetween(v2));

        v2 = new VectorND(new double[] {Math.PI, Math.E});

        System.out.println(v1.angleBetween(v2));

        v2 = new VectorND(new double[] {0.9, 0.01});

        System.out.println(v1.angleBetween(v2));
    }

    public static void main(String[] args) {

        VectorNDTester();
    }
}