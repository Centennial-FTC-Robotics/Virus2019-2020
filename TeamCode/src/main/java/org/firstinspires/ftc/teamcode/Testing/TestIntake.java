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
                if(!Agobot.intake.deployIntake()){ //deployIntake returns true when the intake has been deployed and returns false otherwise and deploys the intake
                    Agobot.intake.runIntake(1);
                }
            }else if(gamepad2.left_bumper){
                if(!Agobot.intake.deployIntake()){
                    Agobot.intake.runIntake(-1);
                }
            }else{
                Agobot.intake.runIntake(0);
            }
            telemetry.addData("Boolean deployIntake", Agobot.intake.deployIntake());
            telemetry.addData("Gamepad 2 Right Bumper", gamepad2.right_bumper);
            telemetry.addData("Gamepad 1 Left Bumper", gamepad2.left_bumper);
            telemetry.update();
        }
    }
}
