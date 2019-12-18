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

            telemetry.addData("Grabber Position");
            telemetry.addData("Left Slide", Agobot.slides.slideLeft.getCurrentPosition());
            telemetry.addData("Right Slide", Agobot.slides.slideRight.getCurrentPosition());
            telemetry.addData("Slide Min ", Agobot.slides.slideMin);
            telemetry.addData("Slide Max", Agobot.slides.slideMax);
            telemetry.addData("Slide Error", Agobot.slides.error);
            telemetry.update();
        }
    }
}
