package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@TeleOp(group = "TeleOP", name = "OdometryTest")
public class OdoTest extends LinearOpMode {

    Vector2D leftStick;
    Vector2D rightStick;
    double lefty;
    double leftx;
    Vector2D motorSpeeds;

    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.initialize(this);
//        Agobot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        waitForStart();

        while(opModeIsActive()) {
            updateControllerValues();
            double diagSpeed1 = motorSpeeds.getComponent(0);
            double diagSpeed2 = motorSpeeds.getComponent(1);
            if (leftx!=0 || lefty!=0){
                Agobot.drivetrain.runMotors(diagSpeed1, diagSpeed2, diagSpeed2, diagSpeed1, rightStick.getComponent(0)); //var1 and 2 are computed values found in theUpdateControllerValues method
            } else {
                Agobot.drivetrain.runMotors(0, 0, 0, 0, rightStick.getComponent(0));
            }
            telemetry.addData("Position:", Agobot.drivetrain.updatePosition());
            telemetry.addData("Heading:", Math.toDegrees(Agobot.drivetrain.getHeading()));
            /*
            telemetry.addData("lEncoder:", Agobot.drivetrain.odometry.getlEncoderCounts());
            telemetry.addData("rEncoder:", Agobot.drivetrain.odometry.getrEncoderCounts());
            telemetry.addData("bEncoder:", Agobot.drivetrain.odometry.getbEncoderCounts());
            telemetry.addData("lEncoderPrevious",Agobot.drivetrain.odometry.lEncoderPrevious);
            telemetry.addData("rEncoderPrevious",Agobot.drivetrain.odometry.rEncoderPrevious);
            telemetry.addData("bEncoderPrevious",Agobot.drivetrain.odometry.bEncoderPrevious);
            telemetry.addData("diagSpeed1", diagSpeed1);
            telemetry.addData("diagSpeed2", diagSpeed2);
            telemetry.addData("deltalEncoder",Agobot.drivetrain.odometry.deltalEncoder);
            telemetry.addData("deltarEncoder",Agobot.drivetrain.odometry.deltarEncoder);
            telemetry.addData("deltabEncoder",Agobot.drivetrain.odometry.deltabEncoder);
            telemetry.addData("deltaHeading",Agobot.drivetrain.odometry.deltaHeading);
            telemetry.addData("deltax",Agobot.drivetrain.odometry.deltax);
            telemetry.addData("deltay",Agobot.drivetrain.odometry.deltay);
            telemetry.addData("lEncoder Run Mode",Agobot.drivetrain.odometry.lEncoder.getMode().toString());
            telemetry.addData("deltaDisp",Agobot.drivetrain.odometry.deltaDisp);
            telemetry.addData("Position = deltaDisp?", Agobot.drivetrain.odometry.currentPosition().equals(Agobot.drivetrain.odometry.deltaDisp));*/

            telemetry.update();

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
