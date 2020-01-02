package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@TeleOp(group = "TeleOp", name = "Arm Tester")
public class TestArm extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {
            if(gamepad2.x) {
                Agobot.arm.armFlipIn(true);
            }

            if(gamepad2.y){
                Agobot.arm.armFlipIn(false);
            }

            telemetry.addData("Left Arm Position", Agobot.arm.leftPosition);
            telemetry.addData("Right Arm Position", Agobot.arm.rightPosition);
            telemetry.addData("Button X", gamepad2.x);
            telemetry.addData("Button Y", gamepad2.y);
            telemetry.update();
        }
    }
}
