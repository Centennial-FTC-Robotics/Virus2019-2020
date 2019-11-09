package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@Autonomous
public class RedDepot extends LinearOpMode {
    Vector2D startPosition = new Vector2D(63, 36); //against wall to the right
    double startHeading = 180; //straight left

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        //Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();

        //go to (63, -60)
        //scan stones
        //strafe to (63, -63) for left, (63, -57) for right, stay put for center
        //go to (12, y) (just keep y values from previous step)
        //go to (12, -12) and turn to face 90 degrees
    }


}
