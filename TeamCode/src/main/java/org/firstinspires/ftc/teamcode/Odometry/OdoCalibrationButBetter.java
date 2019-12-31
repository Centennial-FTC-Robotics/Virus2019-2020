package org.firstinspires.ftc.teamcode.Odometry;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;

@TeleOp(name = "Odometry Calibration 2.0", group = "Calibration")

public class OdoCalibrationButBetter extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        waitForStart();
        while (!gamepad1.b) {
            //telemetry.addData("IMU Heading", ;
            //telemetry.update();
            Agobot.drivetrain.imuHeading();
        }
        double leftRightDelta = (Agobot.drivetrain.odometry.lEncoder.getCurrentPosition() - Agobot.drivetrain.odometry.rEncoder.getCurrentPosition());
        double diameter = (Agobot.drivetrain.odometry.lEncoder.getCurrentPosition() - Agobot.drivetrain.odometry.rEncoder.getCurrentPosition()) / (Math.toRadians(Agobot.drivetrain.imuHeading()));
        double offset = (Agobot.drivetrain.odometry.bEncoder.getCurrentPosition()) / (Math.toRadians(Agobot.drivetrain.imuHeading()));
        while (true) {
            telemetry.addData("diameter", diameter);
            telemetry.addData("offset", offset);
            telemetry.update();
        }
    }
}
