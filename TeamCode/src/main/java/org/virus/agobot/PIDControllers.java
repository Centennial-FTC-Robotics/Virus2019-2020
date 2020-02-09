package org.virus.agobot;

import org.virus.util.PIDController;

public class PIDControllers {
    //drivetrain PID
    public static PIDController moveController = new PIDController(.01f ,0.000f ,.0000f);
    public static PIDController arcController = new PIDController(.01f ,0.000f ,.0000f);
    public static PIDController xController = new PIDController(.08f,.3f ,0.002f,0.2f);
    public static PIDController yController = new PIDController(.08f,.3f ,0.002f,0.2f);
    public static PIDController headingController = new PIDController(-.08f, -.009f,-.001f, .1f);
    //slides PID
    //
}
