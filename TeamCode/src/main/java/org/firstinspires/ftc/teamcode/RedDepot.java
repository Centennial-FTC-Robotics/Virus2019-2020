package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@Autonomous
public class RedDepot extends LinearOpMode {
    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 180; //straight left
    private Vector2D skyStoneLocation;

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        //Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();

        //go to (63, -60)
//        while(Agobot.drivetrain.goToPosition(new Vector2D(-24, 0), 0, 0.6)){
//
//        }
        while(Agobot.drivetrain.goToPosition(new Vector2D(63, -60), startHeading, 0.6)){

        }
        //scan stones
        //Agobot.tracker.getTensorFlowObject().getUpdatedRecognitions();
        //strafe to (63, -63) for left, (63, -57) for right, stay put for center

        //go to (12, y) (just keep y values from previous step)
//        while(Agobot.drivetrain.goToPosition(new Vector2D(-24, 51), 0, 0.6)){
//
//        }
        while(Agobot.drivetrain.goToPosition(new Vector2D(12, -60), startHeading, 0.6)){

        }
        //go to (12, -12) and turn to face 90 degrees
//        while(Agobot.drivetrain.goToPosition(new Vector2D(24, 51), -90, 0.6)){
//
//        }
        while(Agobot.drivetrain.goToPosition(new Vector2D(12, -12), 90, 0.6)){

        }
    }
}
