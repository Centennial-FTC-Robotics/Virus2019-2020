package org.virus.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.virus.superclasses.Drivetrain;

public class MecanumVectorDriveTrain extends Drivetrain {



    @Override
    public float encoderToInch(int encoder) {
        return 0;
    }

    @Override
    public int inchToEncoder(float inches) {
        return 0;
    }

    @Override
    public void initialize(OpMode opMode) {


    }
}
