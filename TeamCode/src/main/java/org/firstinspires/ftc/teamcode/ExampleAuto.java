package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.example.ExampleBot;
import org.virus.paths.Arc;
import org.virus.paths.Line;
import org.virus.paths.Path;
import org.virus.paths.PathComponent;

@Autonomous(name="ExampleAuto", group="Autonomous")

public class ExampleAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        PathComponent[] pathComponents=
                {
                        new Line(24, Path.INCH),
                        new Arc(24, 90, Path.INCH),
                };
        Path path = new Path(pathComponents, 1, 1, .35f, .1f);
        //creates continuous path
        ExampleBot.initialize(this);
        //inits all hardware
        ExampleBot.drivetrain.initializeIMU();
        waitForStart();

        //ExampleBot.horizontalSlides.move(500);
        //move horizontal slides

        while (!ExampleBot.drivetrain.movePath(path)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }

        while (ExampleBot.drivetrain.pivot(90f, .5f)); //turns the robot another 90 degrees
    }
}