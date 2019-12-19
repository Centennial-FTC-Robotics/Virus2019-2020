package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.virus.superclasses.Subsystem;

public class Arm extends Subsystem {

    //includes 2 servos
        //should be opposite (Ex. left seervo = 1, right servo = 0)
    //states include flip in and flip out

    public Servo leftArm;
    public Servo rightArm;
    public double leftPosition = 0.0;
    public double rightPosition = 1.0;


    @Override
    public void initialize(LinearOpMode opMode) {
        //TODO
        leftArm = opMode.hardwareMap.servo.get("leftArm");
        rightArm = opMode.hardwareMap.servo.get("rightArm");
    }

    //more methods TODO
    //TODO: fix values when testing
    public void armFlipIn(boolean in){
        if(in){
            leftArm.setPosition(0.0);
            rightArm.setPosition(1.0);
            leftPosition = 0.0;
            rightPosition = 1.0;
        }else{
            leftArm.setPosition(1.0);
            rightArm.setPosition(0.0);
            leftPosition = 1.0;
            rightPosition = 0.0;
        }
    }
}
