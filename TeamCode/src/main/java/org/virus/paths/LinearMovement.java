package org.virus.paths;

public class LinearMovement extends Movement {
    public static final int TRAPEZOID =0;
    public static final int ROOT = 1;
    public static final int COMBO = 2;

    public LinearMovement(float maxPower, float maxPowerPercent, float distance){
        LBackCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, .05f);
        RBackCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, .05f);
        LFrontCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, .05f);
        RFrontCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, .05f);

    }

    /*LinearMovement(float maxPower, float maxPowerPercent, float distance, float bias){
        LBackCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, bias);
        RBackCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, bias);
        LFrontCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, bias);
        RFrontCurve = new TrapezoidalCurve(distance,  distance*maxPowerPercent, maxPower, bias);

    }*/

    public LinearMovement(float maxAccel, float maxDeaccel, int distance, float max, float bias , int type){
        if(type ==0){
            LBackCurve = new TrapezoidalCurve(maxAccel,  maxDeaccel, distance, max, bias);
            RBackCurve = new TrapezoidalCurve(maxAccel,  maxDeaccel, distance, max, bias);
            LFrontCurve = new TrapezoidalCurve(maxAccel,  maxDeaccel, distance, max, bias);
            RFrontCurve = new TrapezoidalCurve(maxAccel,  maxDeaccel, distance, max, bias);

        }
        else if (type ==1){
            LBackCurve = new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
            RBackCurve = new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
            LFrontCurve =new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
            RFrontCurve = new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
        }
    }

    @Override
    void generateCurves() {

    }
}
