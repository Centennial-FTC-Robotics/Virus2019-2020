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
    //TODO: fix values when testing
    private final double grabPosition = 0.0;
    private final double releasePosition = 1.0;

    @Override
    public void initialize(LinearOpMode opMode) {
        grabber = opMode.hardwareMap.servo.get("grabber");
    }

    //more methods TODO
    public void grab(boolean grab){
        if(grab){
            grabber.setPosition(grabPosition);
            position = grabPosition;
        }else{
            grabber.setPosition(releasePosition);
            position = releasePosition;
        }
    }
}
