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
    public double leftPosition;
    public double rightPosition;
    //TODO: fix values when testing


    @Override
    public void initialize(LinearOpMode opMode) {
        //TODO
        leftArm = opMode.hardwareMap.servo.get("leftArm");
        rightArm = opMode.hardwareMap.servo.get("rightArm");
    }

    //more methods TODO
//    public void
}
