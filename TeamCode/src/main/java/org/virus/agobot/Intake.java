package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.superclasses.Subsystem;

public class Intake extends Subsystem {
    private ExpansionHubMotor leftIntake;
    private ExpansionHubMotor rightIntake;
    private ElapsedTime deployTimer = new ElapsedTime();
    private boolean intakeDeploying = false; //is intake currently deploying?
    private boolean intakeDeployed = false; //is intake already deployed?
    final static double TICKS_PER_SEC_TO_SPEED = 1;

    @Override
    public void initialize(LinearOpMode opMode) {
        leftIntake = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "leftIntake");
        rightIntake = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rightIntake");

        rightIntake.setDirection(DcMotor.Direction.REVERSE);

        leftIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        leftIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public boolean isDeployed(){
        return intakeDeployed;
    }

    public boolean isDeploying(){
        return intakeDeploying;
    }

    public void runIntake(double leftSpeed, double rightSpeed){
        leftSpeed = Range.clip(leftSpeed, -1, 1);
        rightSpeed = Range.clip(rightSpeed, -1, 1);

        leftIntake.setPower(leftSpeed);
        rightIntake.setPower(rightSpeed);
    }

    public void runIntake(double speed){ //for convenience, if they both have the same speed
        runIntake(speed, speed);
    }

    public boolean deployIntake(){
        if (!intakeDeployed){
            if (!intakeDeploying){
                deployTimer.reset();
            }
            runIntake(1, 0);
            intakeDeploying = true;
            if (deployTimer.seconds() >= 0.5){
                intakeDeploying = false;
                intakeDeployed = true;
                runIntake(0);
            }
        }
        return !intakeDeployed; //so that it returns true until it's done, when it returns false
    }

    public boolean detectJam(DcMotorEx motor, double intendedSpeed){
        double threshold = 0.3;
        double actualSpeed = motor.getVelocity()/TICKS_PER_SEC_TO_SPEED;

        return Math.abs(intendedSpeed - actualSpeed) > threshold; //
    }

}