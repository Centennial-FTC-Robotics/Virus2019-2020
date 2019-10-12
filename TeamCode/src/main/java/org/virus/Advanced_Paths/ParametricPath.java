package org.virus.Advanced_Paths;

import org.virus.util.FunctionFormatException;
import org.virus.util.Pair;
import org.virus.util.ParametricFunction2D;

import java.util.Arrays;
import java.util.HashMap;

public abstract class ParametricPath {

    //stuff used to parametrize
    private Function[] functions;
    private ParametricFunction2D[] rotatedFunctions;
    private Pair<Integer, Integer>[] tRanges;
    private double[] functionSpeeds;
    private double[] functionRotations;

    // parametrized stuff
    private Pair<Function[], Function[]> functionComponents;
    // note: make function vectors for this

    public ParametricPath(Function[] orderedFunctions, Pair<Integer, Integer>[] newRanges, double[] newSpeeds, double[] newAngles) throws FunctionFormatException {

        // tRange error checking, making sure ranges are consistent and continuous
        if (!checkTRanges(newRanges)) {
            throw new FunctionFormatException("tRanges in new Path incorrectly defined!", (new Exception()).getCause());
        }

        // parametrize the paths
        functions = Arrays.copyOf(orderedFunctions, orderedFunctions.length);
        tRanges = Arrays.copyOf(newRanges, newRanges.length);
        functionSpeeds = Arrays.copyOf(newSpeeds, newSpeeds.length);
        functionRotations = Arrays.copyOf(newAngles, newAngles.length);

        functionComponents = parametrize();
    }

    private boolean checkTRanges(Pair<Integer, Integer>[] tRanges) {
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

    private Pair<Function[], Function[]> parametrize() {
        Pair<Function[], Function[]> components = new Pair<Function[], Function[]>(null, null);



        return components;
    }
}
