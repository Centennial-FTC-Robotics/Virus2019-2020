package org.virus.Advanced_Paths;

import org.virus.util.FunctionFormatException;
import org.virus.util.Pair;
import org.virus.util.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;

public class ParametricPath {

    //stuff used to parametrize
    private Function[] functions;
    private double[] functionRotations;

    // path definition
    private ParametricFunction2D[] rotatedFunctions;
    private Vector2D[] translations;
    private double[][] definedFunctionRanges;
    private double[][] tRanges;
    private double maxSpeed;

    public ParametricPath(Function[] orderedFunctions, Vector2D[] newTranslations, double[] newRanges, double[][] newDefRanges, double[] newAngles, double newMax) throws FunctionFormatException {

        // information to parametrize
        functions = Function.copy(orderedFunctions);
        definedFunctionRanges = copyDouble2D(newDefRanges);
        tRanges = generateTRanges(Arrays.copyOf(newRanges, newRanges.length));
        functionRotations = Arrays.copyOf(newAngles, newAngles.length);
        translations = Vector2D.copy(newTranslations);

        // parametrize the paths
        rotatedFunctions = parametrize(functions, functionRotations);

        // stuff about the path movement
        maxSpeed = newMax;

        // tRange error checking, making sure ranges are consistent and continuous
        boolean matching = (newRanges.length == orderedFunctions.length)
                && (newDefRanges.length == orderedFunctions.length)
                && (newAngles.length == orderedFunctions.length)
                && (newTranslations.length == orderedFunctions.length);

        if (!checkDefinedRanges(newDefRanges) || !allPositive(newRanges) || !matching || !checkTranslations()) {
            throw new FunctionFormatException("Functions in new PurePursuitPath incorrectly defined!", (new Exception()).getCause());
        }
    }

    public double[][] generateTRanges(double[] orderedRanges) {

        double[][] newRanges = new double[orderedRanges.length][];
        double start = 0;

        for (int r = 0; r < orderedRanges.length; r++) {

            newRanges[r] = new double[] {start, start + orderedRanges[r]};
            start += orderedRanges[r];
        }

        return newRanges;
    }

    private static boolean checkDefinedRanges(double[][] tRanges) {

        for (int i = 0; i < tRanges.length; i++) {
            // check individual tRanges don't go backwards
            if (tRanges[i][0] < tRanges[i][1]) {
                return false;
            } else if (Math.abs(tRanges[i][1] - tRanges[i][0]) < 0.01) {
                // maybe remove "redundant" function paths
            }

            // don't need this if I'm going to make this for defined function ranges, they don't need to be connected
//            // check tRanges around current are properly connected
//            if (i > 0 && i < (tRanges.length - 1)) {
//
//                if ((!tRanges[i].get1().equals(tRanges[i - 1].get2())) || (!tRanges[i].get2().equals(tRanges[i + 1].get1()))) {
//                    return false;
//                }
//            }
        }

        return true;
    }

    private boolean checkTranslations() {

        for (int t = 1; t < (translations.length - 1); t++) {

            Vector2D startTranslation = translations[t];
            Vector2D endTranslation = Vector2D.add(ParametricFunction2D.output(rotatedFunctions[t], definedFunctionRanges[t][1]), startTranslation);

            Vector2D previousEnd = Vector2D.add(ParametricFunction2D.output(rotatedFunctions[t - 1], definedFunctionRanges[t - 1][1]), translations[t - 1]);
            Vector2D nextStart = translations[t + 1];

            if (!startTranslation.equals(previousEnd) || !endTranslation.equals(nextStart)) {

                return false;
            }
        }

        return true;
    }

    public boolean allPositive(double[] list) {

        for (int i = 0; i < list.length; i++) {

            if (list[i] <= 0) {

                return false;
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
        double functionPoint = translatePoint(tVal);
        ParametricFunction2D function = rotatedFunctions[pathComponentIndex];
        Vector2D point = Vector2D.add(ParametricFunction2D.output(function, functionPoint), translations[pathComponentIndex]);

        return point;
    }

    public double translatePoint(double pathPoint) {

        int pathComponentIndex = getPathComponentIndex(pathPoint);
        double[] pathTRange = tRanges[pathComponentIndex];
        double[] functionTRange = definedFunctionRanges[pathComponentIndex];

        double functionPoint = (((pathPoint - pathTRange[0]) / (pathTRange[1] - pathTRange[0])) * (functionTRange[1] - functionTRange[0])) + functionTRange[0];

        return functionPoint;
    }

    public ParametricFunction2D getPathComponent(double tVal) {

        return rotatedFunctions[getPathComponentIndex(tVal)];
    }

    public int getPathComponentIndex(double tVal) {

        // gets the corresponding range to get the function
        int tValRange = -1;
        for (int i = 0; i < tRanges.length; i++) {

            if (tVal >= tRanges[i][0] && tVal < tRanges[i][1]) {
                tValRange = i;
            }
        }

        return tValRange;
    }

    public double getSpeed(double tVal) {

        int currentFuncIndex = getPathComponentIndex(tVal);
        double[] tRange = tRanges[currentFuncIndex];
        double[] funcRange = definedFunctionRanges[currentFuncIndex];

        return (((double) (funcRange[1] - funcRange[0])) / ((double) (tRange[1] - tRange[0])));
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

    public static double[][] copyDouble2D(double[][] original) {

        double[][] newList = new double[original.length][];

        for (int d = 0; d < original.length; d++) {

            newList[d] = Arrays.copyOf(original[d], original[d].length);
        }

        return newList;
    }
}
