package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;
@Autonomous
public class PIDTuningHours extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(0,0),90);
        waitForStart();

        while(true){
            while (Agobot.drivetrain.goToPosition(new Vector2D(0,66),90,1)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)}
            }
            while (Agobot.drivetrain.goToPosition(new Vector2D(0,0),90,1)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            }
        }

    }
}
