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
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();

        //go towards depot
        while(Agobot.drivetrain.goToPosition(new Vector2D(63, -60), startHeading, 0.6)){

        }
        //scan stones
        //Agobot.tracker.getTensorFlowObject().getUpdatedRecognitions();
        //skyStoneLocation = new Vector2D(21, )

        //go to (12, y) (just keep y values from previous step)
        while(Agobot.drivetrain.goToPosition(new Vector2D(12, -60), startHeading, 0.6)){

        }
        //go to (12, -12) and turn to face 90 degrees
        while(Agobot.drivetrain.goToPosition(new Vector2D(12, -12), 90, 0.6)){

        }
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, -12), 90, 0.6)){

        }
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 40), 0, 0.6)){

        }
        while(Agobot.drivetrain.goToPosition(new Vector2D(33, 40), 0, 0.6)){
            //bring dragger down
        }
        //drop
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 40), 270, 0.6)){

        }
        //push against wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 44.5), 270, 0.6)){

        }
        //retract foundation grabber
        //park
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 0), 270, 0.6)){

        }
    }
}
