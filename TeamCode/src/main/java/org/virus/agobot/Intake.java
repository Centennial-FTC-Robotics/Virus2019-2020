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
    final static double TICKS_PER_SEC_TO_POWER = 3350; //only true if motor power is 0.85 or less
    private double prevLeft = 0;
    private double prevRight = 0;

    @Override
    public void initialize(LinearOpMode opMode) {
        leftIntake = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "leftIntake");
        rightIntake = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rightIntake");

        rightIntake.setDirection(DcMotor.Direction.REVERSE);

        leftIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        leftIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeDeploying = false;
        intakeDeployed = false;
    }

    public ExpansionHubMotor getLeft() {

        return  leftIntake;
    }

    public ExpansionHubMotor getRight() {

        return  rightIntake;
    }

    public void runIntake(double leftSpeed, double rightSpeed){
        leftSpeed = Range.clip(leftSpeed, -1, 1);
        rightSpeed = Range.clip(rightSpeed, -1, 1);

        if(leftSpeed != prevLeft){
            leftIntake.setPower(leftSpeed);
        }
        if(rightSpeed != prevRight){
            rightIntake.setPower(rightSpeed);
        }

        prevLeft = leftSpeed;
        prevRight = rightSpeed;
    }

    public void runIntake(double speed){ //for convenience, if they both have the same speed
        runIntake(speed, speed);
    }

    public void deployIntake(){
//        if (!intakeDeployed){
//            if (!intakeDeploying){
//                deployTimer.reset();
//            }
//            runIntake(1, 0);
//            intakeDeploying = true;
//            if (deployTimer.seconds() >= 0.3){
//                intakeDeploying = false;
//                intakeDeployed = true;
//                runIntake(0);
//            }
//        }
//        return !intakeDeployed; //so that it returns true until it's done, when it returns false

        runIntake(1, 0);
    }

    public boolean detectJam(DcMotorEx motor, double intendedPower){
        double actualSpeed = motor.getVelocity();
        double intendedSpeed;
        if(intendedPower <= 0.85){
            intendedSpeed = intendedPower*TICKS_PER_SEC_TO_POWER;
        }else{
            intendedSpeed = 2860;
        }
        return intendedSpeed/actualSpeed > 2; //
    }

}