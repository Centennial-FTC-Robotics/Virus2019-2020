package org.virus.example;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.openftc.revextensions2.ExpansionHubMotor;

import org.virus.superclasses.Subsystem;

public class ExampleSlides extends Subsystem {
    //Defenition for some type of slides with only 1 motor, only controlled by passing it a height to be at.
    private ExpansionHubMotor slideMotor;
    @Override
    public void initialize(OpMode opMode) {
        slideMotor = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lFront");
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    //moves slides to specified slide motor encoder count
    public boolean move(int distance){
        slideMotor.setTargetPosition(distance);
        slideMotor.setPower(1);
        return slideMotor.isBusy();
    }

}
