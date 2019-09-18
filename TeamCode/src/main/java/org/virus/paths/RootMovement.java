package org.virus.paths;

public class RootMovement extends Movement {
    public RootMovement(float maxAccel, float maxDeaccel, int distance, float max, float bias){
        LBackCurve = new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
        RBackCurve = new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
        LFrontCurve =new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
        RFrontCurve = new RootCurve(maxAccel,  maxDeaccel,  max, distance, bias);
    }

    @Override
    void generateCurves() {

    }
}
