package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@Autonomous(name = "Red Bridge", group = "Auto")
public class RedBridge extends LinearOpMode {
    private Vector2D startPosition = new Vector2D(63, -12); //against wall to the right
    private double startHeading = 180; //straight left
    private Vector2D skyStoneLocation;

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();

        //go towards skystones
        //TODO: maybe have path go hypotenuse instead of legs (talk to ere if confusion)
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, -12), startHeading, 0.6)){

        }

        //TODO: scan stones

        //TODO: grab skystone

        //recenter position, face backwards to be ready to place skystone
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, -18), 0, 0.6)){

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

        //go towards alliance wall
        //TODO: also another place to go hypotenuse instead
        while(Agobot.drivetrain.goToPosition(new Vector2D(63, 44.5), 270, 0.6)) {

        }

        //park on red tape, closer to wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(63, 0), 270, 0.6)){

        }
    }
}
