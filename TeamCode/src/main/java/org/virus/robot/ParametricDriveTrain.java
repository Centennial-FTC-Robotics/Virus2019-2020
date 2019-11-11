package org.virus.robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.virus.Advanced_Paths.ParametricPath;
import org.virus.superclasses.Drivetrain;
import org.virus.util.ParametricFunction2D;

public class ParametricDriveTrain extends Drivetrain {

    // path definitions
    ParametricPath autoPath;

    public float encoderToInch(int encoder) {
        return 0;
    }

    public int inchToEncoder(float inches) {
        return 0;
    }

    public void initialize(OpMode opMode) {

    }
}
