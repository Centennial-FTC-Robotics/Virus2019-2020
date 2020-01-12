package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

//@TeleOp(group = "TeleOp", name = "Arm Tester")
public class TestArm extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {
            if(gamepad2.left_trigger > 0) {
                Agobot.arm.armFlipOut(false);
            }else if(gamepad2.right_trigger > 0){
                Agobot.arm.armFlipOut(true);
            }

            telemetry.addData("Arm Position", Agobot.arm.getArmPosition());
            telemetry.addData("Left Arm Position", Agobot.arm.leftPosition);
            telemetry.addData("Right Arm Position", Agobot.arm.rightPosition);
            telemetry.addData("Left Trigger", gamepad2.left_trigger);
            telemetry.addData("Right Trigger", gamepad2.right_trigger);
            telemetry.update();
        }
    }
}
