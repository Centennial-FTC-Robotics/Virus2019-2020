package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.virus.agobot.Agobot;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;
@Disabled
//@Autonomous(group = "Autonomous", name = "Odometry Test")

public class OdoTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
//        Agobot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(0, 0), 0);
        waitForStart();


        while(Agobot.drivetrain.goToPosition(new Vector2D(0,0), 45, 0.6)){
        }

        while (opModeIsActive()) {
            telemetry.addData("Change in angle", getHeading());
            telemetry.addData("lEncoder", Agobot.drivetrain.odometry.getlEncoderCounts());
            telemetry.addData("rEncoder", Agobot.drivetrain.odometry.getrEncoderCounts());
            telemetry.addData("bEncoder", Agobot.drivetrain.odometry.getbEncoderCounts());
            telemetry.update();
        }

    }

    public double getHeading(){
        Orientation currentOrientation = Agobot.drivetrain.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return currentOrientation.firstAngle;
    }

    public double angleDifference(double target, double current){
        double difference = normalizeAngle(target) - normalizeAngle(current);
        if (difference > 180) {
            difference -= 360.0;
        }
        if (difference <= -180) {
            difference += 360.0;
        }
        return difference;
    }

    public double normalizeAngle(double degrees) {

        while (degrees < 0) {

            degrees += 360;
        }

        while (degrees > 360) {

            degrees -= 360;
        }

        return degrees;
    }

    public void turn(double targetAngle){
        Agobot.drivetrain.updatePosition();
        double currentAngle = getHeading();
        int direction;
        double turnRate = 0;
        double P = 1d / 60d;
        double minSpeed = 0;
        double maxSpeed = 0.2d;
        double tolerance = 0.5;

        double error = angleDifference(targetAngle, currentAngle);
        while (opModeIsActive() && error > tolerance) {
            currentAngle = getHeading();
            error = angleDifference(targetAngle, currentAngle);
            turnRate = Range.clip(P * error, minSpeed, maxSpeed);
            Agobot.drivetrain.runMotors(0,0,0,0,-turnRate);
            /*telemetry.addData("error: ", error);
            telemetry.addData("currentAngle: ", getRotationinDimension('Z'));
            telemetry.update();*/
        }
        Agobot.drivetrain.runMotors(0.0,0.0,0.0,0.0, 0);
    }
}
