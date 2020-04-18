package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;

import org.virus.agobot.Agobot;
import org.virus.util.RobotLogger;
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
        RobotLogger.startLogging();
        while (Agobot.drivetrain.goToPosition(new Vector2D(0,66),90,.6)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)}
            RobotLogger.logUpdate();
        }
        RobotLogger.stopLogging();
        RobotLogger.writeLog();
    }
}
