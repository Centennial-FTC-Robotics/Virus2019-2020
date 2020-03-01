package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.virus.agobot.Agobot;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;
@Disabled
//@Autonomous(group = "Autonomous", name = "Odometry Test")

public class OdoTest extends LinearOpMode {
    double maxSpeed = 0.4;

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
//        Agobot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 270);

        waitForStart();

//        while(!gamepad1.a && opModeIsActive()){
//            telemetry.addData("Position:", Agobot.drivetrain.updatePosition());
//            telemetry.addData("Heading:", Agobot.drivetrain.getHeading());
//            telemetry.update();
//        }

        double newHeading = 0;
        while(Agobot.drivetrain.goToPosition(new Vector2D(31, -43), 225, maxSpeed));
        //while(Agobot.drivetrain.goToPosition(new Vector2D(-12,0), 90, maxSpeed));
//        while(opModeIsActive()){
//            currentPosition = Agobot.drivetrain.updatePosition();
//            telemetry.addData("Position:", currentPosition);
//            telemetry.addData("Heading:", Math.toDegrees(Agobot.drivetrain.getHeading()));
//            updateControllerValues();
//            double diagSpeed1 = motorSpeeds.getComponent(0);
//            double diagSpeed2 = motorSpeeds.getComponent(1);
//            if ((leftStick.getComponent(0) != 0) || (leftStick.getComponent(1) != 0)){
//                Agobot.drivetrain.runMotors(diagSpeed1, diagSpeed2, diagSpeed2, diagSpeed1, rightStick.getComponent(0)); //var1 and 2 are computed values found in theUpdateControllerValues method
//            } else {
//                Agobot.drivetrain.runMotors(0, 0, 0, 0, rightStick.getComponent(0));
//            }
//            telemetry.update();
//        }


    }
}
