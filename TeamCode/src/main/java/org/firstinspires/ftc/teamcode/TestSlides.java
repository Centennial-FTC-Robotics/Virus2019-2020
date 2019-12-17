package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@TeleOp(group = "TeleOp", name = "Slide Tester")
public class TestSlides extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {
            Agobot.horizontalSlides.slidePower(gamepad2.left_stick_y);

            telemetry.addData("Gamepad 2 Left Stick Y Value", gamepad2.left_stick_y);
            telemetry.addData("Left Slide", Agobot.horizontalSlides.slideLeft.getCurrentPosition());
            telemetry.addData("Right Slide", Agobot.horizontalSlides.slideRight.getCurrentPosition());
            telemetry.addData("Slide Min ", Agobot.horizontalSlides.slideMin);
            telemetry.addData("Slide Max", Agobot.horizontalSlides.slideMax);
            telemetry.addData("Slide Error", Agobot.horizontalSlides.error);
            telemetry.update();
        }
    }
}
