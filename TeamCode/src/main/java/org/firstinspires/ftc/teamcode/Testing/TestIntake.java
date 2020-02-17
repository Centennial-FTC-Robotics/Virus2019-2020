package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@TeleOp(group = "TeleOp", name = "Intake Tester")
public class TestIntake extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {
            //TODO: keertik write this
            if(gamepad2.right_bumper){
                Agobot.intake.runIntake(.8);
            }else if(gamepad2.left_bumper){
                Agobot.intake.runIntake(-.8);
            }else{
                Agobot.intake.runIntake(0);
            }

            telemetry.addData("Gamepad 2 Right Bumper", gamepad2.right_bumper);
            telemetry.addData("Gamepad 1 Left Bumper", gamepad2.left_bumper);
            telemetry.update();
        }
    }
}
