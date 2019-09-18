package org.virus.paths;

public abstract class Rotation {

    private MotionCurve angleCurve;

    public abstract float getAngle(float percent);
}
