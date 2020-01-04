package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.openftc.revextensions2.ExpansionHubServo;
import org.virus.superclasses.Subsystem;

public class FoundationDragger extends Subsystem {

    //includes 1 servo
    //states include: grab & release

    public Servo dragger;
    public double position = 0.0;
    private final double dragPosition = 0.0;
    private final double upPosition = 1.0;

    @Override
    public void initialize(LinearOpMode opMode) {
        dragger = opMode.hardwareMap.servo.get("dragger");
    }

    public void drag(boolean grab){
        if(grab){
            dragger.setPosition(dragPosition);
            position = dragPosition;
        }else{
            dragger.setPosition(upPosition);
            position = upPosition;
        }
    }
}
