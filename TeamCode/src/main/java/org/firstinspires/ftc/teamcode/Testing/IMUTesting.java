package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
//@Autonomous
public class IMUTesting extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        while(true){
            //telemetry.addData("IMU Heading", ;
            //telemetry.update();
            Agobot.drivetrain.imuHeading();
        }
    }
}
