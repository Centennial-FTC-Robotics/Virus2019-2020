package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@TeleOp(group = "TeleOp", name = "Grabber Tester")
public class TestGrabber extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {
            Agobot.slides.slidePower(gamepad2.left_stick_y);

            telemetry.addData("Grabber Position", Agobot.grabber.position);
            telemetry.addData("Button A", gamepad2.a);
            telemetry.addData("Button B", gamepad2.b);
            telemetry.update();
        }
    }
}
