package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.openftc.revextensions2.ExpansionHubServo;
import org.virus.superclasses.Subsystem;

public class Grabber extends Subsystem {

    //includes 1 servo
    //states include: grab & release

    public Servo grabber;
    public double position = 0.0;
    private final double grabPosition = 1.0;
    private final double releasePosition = 0.0;

    @Override
    public void initialize(LinearOpMode opMode) {
        grabber = (ExpansionHubServo)opMode.hardwareMap.servo.get("grabber");
    }

    //more methods TODO
    public void grabber(boolean grab){
        if(grab){
            //TODO: fix values
            grabber.setPosition(1.0);
            position = 1.0;
        }else{
            //TODO: fix values
            grabber.setPosition(0.0);

        }
    }
}
