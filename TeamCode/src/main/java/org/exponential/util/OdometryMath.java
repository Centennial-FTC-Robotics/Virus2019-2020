package org.exponential.util;

import org.exponential.blueshift.TankDrivetrain;

public class OdometryMath {
    public static final float COUNTSPERREV = TankDrivetrain.COUNTSPERREV;

    public static float encoderToInch(int encoder){
        float revs = (24f/22f) * (float)(encoder)/COUNTSPERREV;
        float distance = (float) (revs * 4f * Math.PI);
        return distance;
    }
    public static int inchToEncoder(float inches){
        float revs = (float)(inches /  (4f * Math.PI));
        float encoder = revs * COUNTSPERREV * (22f/24f);
        return Math.round(encoder);

    }
}
