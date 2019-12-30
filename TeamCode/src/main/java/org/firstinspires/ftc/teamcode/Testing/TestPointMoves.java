package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@Autonomous(name="TestPointMoves", group="Autonomous")

public class TestPointMoves extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        //creates continuous path
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(0,0),90);
        waitForStart();

        //ExampleBot.horizontalSlides.move(500);
        //move horizontal slides
        //while(true&&opModeIsActive()){
          //  ExampleBot.drivetrain.moveLeftTest();
        //}

        while (Agobot.drivetrain.goToPosition(new Vector2D(24,24),90,.6)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }
        while (Agobot.drivetrain.goToPosition(new Vector2D(-24,24),90,.6)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }
        while (Agobot.drivetrain.goToPosition(new Vector2D(-24,-24),180,.6)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }
        while (Agobot.drivetrain.goToPosition(new Vector2D(24,-24),0,.6)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }
        while (Agobot.drivetrain.goToPosition(new Vector2D(24,24),270,.6)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }
        while (Agobot.drivetrain.goToPosition(new Vector2D(0,0),90,.6)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }


//        while (ExampleBot.drivetrain.pivot(90f, .5f)); //turns the robot another 90 degrees
    }
}