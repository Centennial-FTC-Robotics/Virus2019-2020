package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

import java.util.ArrayList;

//@TeleOp
public class MotorToTick extends LinearOpMode {

//    DcMotorEx leftIntake;
//    DcMotorEx rightIntake;

    @Override
    public void runOpMode() throws InterruptedException {
//        leftIntake = hardwareMap.get(DcMotorEx.class, "slideLeft");
//        rightIntake = hardwareMap.get(DcMotorEx.class, "slideRight");
//
//        rightIntake.setDirection(DcMotor.Direction.REVERSE);
//
//        leftIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        rightIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//
//        leftIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        rightIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(0,0), 0);

        Agobot.slides.slides(Agobot.slides.slideMax);
        waitForStart();

        ArrayList<Double> motorVelocities = new ArrayList<Double>();
        ElapsedTime movementTimer = new ElapsedTime();

        while(opModeIsActive() && (movementTimer.milliseconds() < 500)){
            Agobot.slides.slidePower(0.5);

            motorVelocities.add((Agobot.slides.slideLeft.getVelocity() + Agobot.slides.slideRight.getVelocity()) / 2.0);
            telemetry.update();
        }

        double max = motorVelocities.get(0);
        double average = max;

        for (int d = 1; d < motorVelocities.size(); d++) {

            if (motorVelocities.get(d) > max) {

                max = motorVelocities.get(d);
            }

            average += motorVelocities.get(d);
        }

        average /= motorVelocities.size();

        telemetry.addData("Max Velocity:", max);
        telemetry.addData("Average Velocity", average);
        telemetry.update();
    }
}
