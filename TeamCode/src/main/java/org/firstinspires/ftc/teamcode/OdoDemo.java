package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.virus.agobot.Agobot;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;

@Autonomous(group = "Autonomous", name = "Odometry Demo")

public class OdoDemo extends LinearOpMode {
    double maxSpeed = 1;
    Vector2D motorSpeeds;
    Vector2D currentPosition = new Vector2D(0,0);

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
//        Agobot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        //Agobot.drivetrain.initializeIMU();

        waitForStart();

        while(!gamepad1.a && opModeIsActive()){
            telemetry.addData("Position:", Agobot.drivetrain.updatePosition());
            telemetry.addData("Heading:", Math.toDegrees(Agobot.drivetrain.getHeading()));
            telemetry.update();
        }

        Vector2D newPosition = new Vector2D(12,0);
        double newHeading = 0;
        //while(Agobot.drivetrain.goToPosition(new Vector2D(-12,0), newHeading, maxSpeed));
        while(Agobot.drivetrain.goToPosition(new Vector2D(12,0), 90, maxSpeed));
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
