package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.openftc.revextensions2.ExpansionHubServo;
import org.virus.superclasses.Subsystem;

public class FoundationDragger extends Subsystem {

    //includes 1 servo
    //states include: grab & release

    public Servo leftDragger;
    public Servo rightDragger;
    public double position = 0.0;
    private final double dragPosition = 0.0;
    private final double upPosition = 1.0;

    @Override
    public void initialize(LinearOpMode opMode) {
        rightDragger = opMode.hardwareMap.servo.get("rightdragger");
        leftDragger = opMode.hardwareMap.servo.get("leftdragger");
    }

    public boolean isDragging() {

        return (position == dragPosition);
    }

    public void drag(boolean grab){
        if(grab){
            rightDragger.setPosition(dragPosition);
            leftDragger.setPosition(1 - dragPosition);
            position = dragPosition;
        }else{
            rightDragger.setPosition(upPosition);
            leftDragger.setPosition(1 - upPosition);
            position = upPosition;
        }
    }

    public void drag(double position) {

        position = Range.clip(position, 0, 1);

        rightDragger.setPosition(position);
        leftDragger.setPosition(1 - position);
    }
}
