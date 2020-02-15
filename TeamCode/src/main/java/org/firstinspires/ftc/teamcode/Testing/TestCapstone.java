package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@TeleOp(group = "TeleOp", name = "Capstone Tester")
public class TestCapstone extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {

            if(gamepad2.back) {
                Agobot.capstone.drop(true);
            }else{
                Agobot.capstone.drop(false);
            }

            telemetry.addData("Capstone Position", Agobot.capstone.position);
            telemetry.addData("Button A", gamepad2.a);
            telemetry.addData("Button B", gamepad2.b);
            telemetry.update();
        }
    }
}
