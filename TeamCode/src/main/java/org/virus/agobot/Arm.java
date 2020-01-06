package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.virus.superclasses.Subsystem;

public class Arm extends Subsystem {


    public Servo leftArm;
    public Servo rightArm;
    // drop = 0, standby = 1, in = 2
    public String[] armPositions = {"drop", "standby", "in"};
    public int armPosition;
    public double leftPosition = 0.0;
    public double rightPosition = 1.0;
    //in: left = 0.0, right = 1.0
    //standby: left = 0.2, right = 0.8
    //drop: left: 1.0, right = 0.0

    @Override
    public void initialize(LinearOpMode opMode) {
        //TODO
        leftArm = opMode.hardwareMap.servo.get("leftArm");
        rightArm = opMode.hardwareMap.servo.get("rightArm");
        armPosition = 0;
    }

    public void armFlipOut(boolean out){
        double left = 0.0;
        double right = 1.0;

        if(out && armPosition > 0){
            armPosition--;
        }else if(!out && armPosition < 2){
            armPosition++;
        }

        switch(armPosition){
            case 0:
                left = 1.0;
                right = 0.0;
                break;
            case 1:
                left = 0.2;
                right = 0.8;
                break;
            case 2:
                left = 0.0;
                right = 1.0;
                break;
        }

        leftArm.setPosition(left);
        leftPosition = left;
        rightArm.setPosition(right);
        rightPosition = right;
    }

    public String getArmPosition(){
        return armPositions[armPosition];
    }
}
