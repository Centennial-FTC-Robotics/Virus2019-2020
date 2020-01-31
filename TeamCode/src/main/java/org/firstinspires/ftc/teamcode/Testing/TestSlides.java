package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

//@TeleOp(group = "TeleOp", name = "Slide Tester")
public class TestSlides extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {
            Agobot.slides.slidePower(gamepad2.left_stick_y);
            if(gamepad1.b){
                Agobot.slides.slides(600);
            }
            telemetry.addData("Gamepad 2 Left Stick Y Value", gamepad2.left_stick_y);
            telemetry.addData("Left Slide", Agobot.getCurrentMotorPos(Agobot.slides.slideLeft));
            telemetry.addData("Right Slide", Agobot.getCurrentMotorPos(Agobot.slides.slideRight));
            telemetry.addData("Slide Min ", Agobot.slides.slideMin);
            telemetry.addData("Slide Max", Agobot.slides.slideMax);
            telemetry.addData("Slide Error", Agobot.slides.tolerance);
            telemetry.update();
        }
    }
}
