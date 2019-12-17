package org.virus.util;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    private float kP;
    private float kI;
    private float kD;
    private float antiWind=-1;
    private ElapsedTime PIDClock;
    private float prevError = 0;
    private float i = 0;
    private boolean started=false;

    public PIDController(float kP, float kI, float kD){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public PIDController(float kP, float kI, float kD, float antiWind){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.antiWind = antiWind;
    }

    public void start(){
        started=true;
        PIDClock = new ElapsedTime();
        prevError = 0;
        float i = 0;
    }
    public void clear(){
        started=false;
        PIDClock = null;
        prevError = 0;
        float i = 0;
    }
    //ideally called every loop, don't wait too long between calls unless running p loop
    public float getValue (float target, float actual) {
        return getValue(target - actual);
    }

    public float getValue (float error) {

        //throw (new Exception("NotStarted"));

        if (!started) {
            start();
        }
        float p = error;
        i += (float) (error * PIDClock.seconds());
        float d = (float) ((error - prevError) / PIDClock.seconds());
        prevError = error;
        PIDClock.reset();
        if (antiWind >= 0) {
            if (Math.abs(kI * i) >= antiWind) {
                i = antiWind / kI * (i/Math.abs(i));
                return kP * p + antiWind*(i/Math.abs(i)) + kD * d;
            } else {
                return kP * p + kI * i + kD * d;
            }
        }
        return kP * p + kI * i + kD * d;
    }
}
