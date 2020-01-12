package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

//@TeleOp(group = "TeleOP", name = "MechDrive")
public class MechDrive extends LinearOpMode {

    Vector2D leftStick;
    Vector2D rightStick;
    double lefty;
    double leftx;
    Vector2D motorSpeeds;

    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.initialize(this);
        Agobot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        waitForStart();

        while(opModeIsActive()) {
            updateControllerValues();
            double diagSpeed1 = motorSpeeds.getComponent(0);
            double diagSpeed2 = motorSpeeds.getComponent(1);

            if (leftx!=0 || lefty!=0){
                telemetry.addData("lFront:", diagSpeed1);
                telemetry.addData("lBack:", diagSpeed2);
                telemetry.addData("rFront:", diagSpeed2);
                telemetry.addData("rBack:", diagSpeed1);
                telemetry.update();
                Agobot.drivetrain.runMotors(diagSpeed1, diagSpeed2, diagSpeed2, diagSpeed1, rightStick.getComponent(0)); //var1 and 2 are computed values found in theUpdateControllerValues method
            } else {
                Agobot.drivetrain.runMotors(0, 0, 0, 0, rightStick.getComponent(0));
            }

            telemetry.addData("GamePad 1:", (gamepad1 == null));
        }
    }

    public void updateControllerValues(){

        leftStick = new Vector2D((double) gamepad1.left_stick_x, (double) gamepad1.left_stick_y);
        rightStick = new Vector2D((double) gamepad1.right_stick_x, (double) gamepad1.right_stick_y);
        leftx = -leftStick.getComponent(0);
        lefty = -leftStick.getComponent(1);
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(Math.pow(lefty, 2) + Math.pow(leftx, 2));

        motorSpeeds = new Vector2D((lefty-leftx)*magnitude/scalar, (lefty+leftx)*magnitude/scalar);
    }
}
