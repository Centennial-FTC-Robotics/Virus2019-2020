package org.exponential.paths;

import android.graphics.Point;

public abstract class Movement {
    public MotionCurve LFrontCurve;
    public MotionCurve RFrontCurve;
    public MotionCurve LBackCurve;
    public MotionCurve RBackCurve;
    int tolerance = 10;
    Point start;
    Point end;


    //thicc curve pls i need a 5
    abstract void generateCurves();

    public Point getEnd() {
        return end;
    }

    public Point getStart() {
        return start;
    }
}
