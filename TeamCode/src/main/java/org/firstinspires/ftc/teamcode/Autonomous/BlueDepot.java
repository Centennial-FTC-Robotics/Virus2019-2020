package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@Autonomous(name = "Blue Depot", group = "Auto")
public class BlueDepot extends LinearOpMode {
    //TODO

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

        //get ready to position to start scanning
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, -60), startHeading, 0.6)){

        }

        //TODO: scan stones
        //Agobot.tracker.getTensorFlowObject().getUpdatedRecognitions();
        //skyStoneLocation = new Vector2D(21, )

        //TODO: grab skystone

        //recenter position, face backwards to be ready to place skystone
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, -40), 0, 0.6)){

        }

        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 40), 0, 0.6)){

        }

        //get closer to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(33, 40), 0, 0.6)){
            //TODO: place skystone
            //TODO: bring dragger down
        }

        //drag and rotate foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 40), 270, 0.6)){

        }

        //push against wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 44.5), 270, 0.6)){
            //TODO: retract foundation grabber
        }

        //park on red tape, closer to left side
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 0), 270, 0.6)){

        }
    }
}
