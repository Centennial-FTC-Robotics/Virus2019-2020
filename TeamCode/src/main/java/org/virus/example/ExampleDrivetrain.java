package org.virus.example;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.openftc.revextensions2.ExpansionHubMotor;

import org.virus.paths.Path;
import org.virus.superclasses.Drivetrain;

public class ExampleDrivetrain extends Drivetrain {
    //standard 4 motor drivetrain
    private ExpansionHubMotor lFront;
    private ExpansionHubMotor rFront;
    private ExpansionHubMotor lBack;
    private ExpansionHubMotor rBack;
    private BNO055IMU imu;
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
        lFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lFront");
        rFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rFront");
        lBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lBack");
        rBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rBack");
    }

    //follows a defined smooth continuous path
    public boolean move(Path path){
        //pretend there's implementation here
        //there's implementation in the TankDrivetrain class and stuff if you really care
        return false;
        //once implemented should return true or false based on whether path is complete
    }

    //turn around the center of the robot
    public boolean pivot(float angle, float speed){
        //pretend there's implementation here
        return false;
        //once implemented should return true or false based on whether pivot is complete
    }






}
