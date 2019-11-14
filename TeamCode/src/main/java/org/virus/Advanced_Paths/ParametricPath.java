package org.virus.Advanced_Paths;

import org.virus.util.FunctionFormatException;
import org.virus.util.Pair;
import org.virus.util.Vector2D;

import java.util.Arrays;

public class ParametricPath {

    //stuff used to parametrize
    private Function[] functions;
    private double[] functionRotations;

    // path definition
    private ParametricFunction2D[] rotatedFunctions;
    private Pair<Integer, Integer>[] tRanges;
    private Pair<Integer, Integer>[] definedFunctionRanges;
    private double maxSpeed;

    public ParametricPath(Function[] orderedFunctions, Pair<Integer, Integer>[] newRanges, Pair<Integer, Integer>[] newDefRanges, double[] newAngles, double newMax) throws FunctionFormatException {

        // tRange error checking, making sure ranges are consistent and continuous
        if (!checkTRanges(newRanges) && newRanges.length == newDefRanges.length) {
            throw new FunctionFormatException("Ranges in new Path incorrectly defined!", (new Exception()).getCause());
        }

        // information to parametrize
        functions = Arrays.copyOf(orderedFunctions, orderedFunctions.length);
        tRanges = Arrays.copyOf(newRanges, newRanges.length);
        definedFunctionRanges = Arrays.copyOf(newDefRanges, newDefRanges.length);
        functionRotations = Arrays.copyOf(newAngles, newAngles.length);

        // parametrize the paths
        rotatedFunctions = parametrize(functions, functionRotations);

        // stuff about the path movement
        maxSpeed = newMax;
    }

    private static boolean checkTRanges(Pair<Integer, Integer>[] tRanges) {
        for (int i = 0; i < tRanges.length; i++) {
            // check individual tRanges don't go backwards
            if (tRanges[i].get1() < tRanges[i].get2()) {
                return false;
            } else if (Math.abs(tRanges[i].get2() - tRanges[i].get1()) < 0.01) {
                // maybe remove "redundant" function paths
            }
            // check tRanges around current are properly connected
            if (i > 0 && i < (tRanges.length - 1)) {

                if ((!tRanges[i].get1().equals(tRanges[i - 1].get2())) || (!tRanges[i].get2().equals(tRanges[i + 1].get1()))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static ParametricFunction2D[] parametrize(Function[] functions, double[] functionRotations) {

        ParametricFunction2D[] pathComponents = new ParametricFunction2D[functions.length];

        for (int f = 0; f < functions.length; f++) {
            ParametricFunction2D function = new ParametricFunction2D(functions[f], false);
            function = ParametricFunction2D.rotate(function, functionRotations[f]);
            pathComponents[f] = function;
        }

        return pathComponents;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAngle(double tVal) {

        int pathComponentIndex = getPathComponentIndex(tVal);
        ParametricFunction2D function = rotatedFunctions[pathComponentIndex];
        double functionPoint = translatePoint(tVal);

        return function.genAngle(functionPoint);
    }

    public Vector2D getPoint(double tVal) {

        int pathComponentIndex = getPathComponentIndex(tVal);
        ParametricFunction2D function = rotatedFunctions[pathComponentIndex];
        Vector2D point = ParametricFunction2D.output(function, tVal);

        return point;
    }

    public double translatePoint(double pathPoint) {

        int pathComponentIndex = getPathComponentIndex(pathPoint);
        Pair<Integer, Integer> pathTRange = tRanges[pathComponentIndex];
        Pair<Integer, Integer> functionTRange = definedFunctionRanges[pathComponentIndex];

        double functionPoint = (((pathPoint - pathTRange.get1()) / (pathTRange.get2() - pathTRange.get1())) * (functionTRange.get2() - functionTRange.get1())) + functionTRange.get1();

        return functionPoint;
    }

    public ParametricFunction2D getPathComponent(double tVal) {

        return rotatedFunctions[getPathComponentIndex(tVal)];
    }

    public int getPathComponentIndex(double tVal) {

        // gets the corresponding range to get the function
        int tValRange = -1;
        for (int i = 0; i < tRanges.length; i++) {

            if (tVal > tRanges[i].get1() && tVal < tRanges[i].get2()) {
                tValRange = i;
            }
        }

        return tValRange;
    }

    public double getSpeed(double tVal) {

        int currentFuncIndex = getPathComponentIndex(tVal);
        Pair<Integer, Integer> tRange = tRanges[currentFuncIndex];
        Pair<Integer, Integer> funcRange = definedFunctionRanges[currentFuncIndex];

        return (((double) (funcRange.get2() - funcRange.get1())) / ((double) (tRange.get2() - tRange.get1())));
    }

    public double approximateDistance(double distanceTraveled, double resolution) {

        double distance = 0;
        double tVal = 0;

        while (distance < distanceTraveled) {

            Pair<Function, Function> parametricFunc = this.getPathComponent(tVal).getRectangularComponents();
            Function x = parametricFunc.get1();
            Function y = parametricFunc.get2();

            double addedDistance = Math.hypot(x.output(tVal + resolution) - x.output(tVal), y.output(tVal + resolution) - y.output(tVal));
           distance += addedDistance;
            tVal += resolution;
        }

        return tVal;
    }
}
