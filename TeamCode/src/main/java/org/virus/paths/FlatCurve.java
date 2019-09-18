package org.virus.paths;

public class FlatCurve extends MotionCurve {
    FlatCurve(float curveLength, float max){
        maxVal=max;
        length=curveLength;
    }
    @Override
    public float getValue(float x) {
        return maxVal;
    }
}
