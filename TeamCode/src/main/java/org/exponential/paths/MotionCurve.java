package org.exponential.paths;

public abstract class MotionCurve {
    float maxVal;
    float length;
    public abstract float getValue(float x);
    public float getLength(){
        return length;
    }

    public float getMaxVal(){
        return maxVal;
    }

}
