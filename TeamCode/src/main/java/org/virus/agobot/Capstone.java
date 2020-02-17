package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.openftc.revextensions2.ExpansionHubServo;
import org.virus.superclasses.Subsystem;

public class Capstone extends Subsystem {

    //includes 1 servo
    //states include: grab & release

    public Servo capstoneServo;
    public double position = 0.0;

    @Override
    public void initialize(LinearOpMode opMode) {
        capstoneServo = opMode.hardwareMap.servo.get("capstone");
        capstoneServo.setPosition(0.6);
        //capstoneServo.setPosition(.5);
    }

    public void drop(boolean grab){
        if(grab){
            capstoneServo.setPosition(0.4);
            position = 0.4;
        }else{
            capstoneServo.setPosition(0.6);
            position = 0.6;
        }
    }
}
