package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.virus.agobot.Agobot;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;

@Autonomous(group = "Autonomous", name = "Odometry Demo")

public class OdoDemo extends LinearOpMode {
    Vector2D leftStick;
    Vector2D rightStick;
    double lefty;
    double leftx;
    double steerMag;
    double maxSpeed = 1;
    Vector2D motorSpeeds;
    PIDController headingController = new PIDController(-.03f, 0 ,0);
    PIDController xController = new PIDController(.08f,0 ,0);
    PIDController yController = new PIDController(-.08f,0 ,0);
    Vector2D currentPosition = new Vector2D(0,0);

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
//        Agobot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        Agobot.drivetrain.initializeIMU();

        waitForStart();

        while(!gamepad1.a && opModeIsActive()){
            telemetry.addData("Position:", Agobot.drivetrain.updatePosition());
            telemetry.addData("Heading:", Math.toDegrees(Agobot.drivetrain.getHeading()));
            telemetry.update();
        }

        headingController.start();
        xController.start();
        yController.start();
        Vector2D newPosition = new Vector2D(0,0);
        double newHeading = 0;
        while(Agobot.drivetrain.goToPosition(newPosition, newHeading, maxSpeed));

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

    public void updateControllerValues(){
        double x = currentPosition.getComponent(0);
        double y = currentPosition.getComponent(1);
        leftStick = new Vector2D((double) xController.getValue(0, (float)x), (double)  yController.getValue(0, (float)y));
        rightStick = new Vector2D((double) headingController.getValue(0, AngleUnit.normalizeDegrees((float)Math.toDegrees(Agobot.drivetrain.getHeading()))),0);
        leftStick.rotate(-Agobot.drivetrain.getHeading());
        leftx = -leftStick.getComponent(0);
        lefty = -leftStick.getComponent(1);
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(Math.pow(lefty, 2) + Math.pow(leftx, 2));

        motorSpeeds = new Vector2D((lefty-leftx)*magnitude/scalar, (lefty+leftx)*magnitude/scalar);
    }

}
