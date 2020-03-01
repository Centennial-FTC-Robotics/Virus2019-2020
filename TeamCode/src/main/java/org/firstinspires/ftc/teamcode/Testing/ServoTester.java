package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

//@TeleOp(group = "TeleOp", name = "Universal Servo Tester")
public class ServoTester extends LinearOpMode {

    private Servo[] servos;
    private String[] servoNames = {"left dragger", "right dragger", "leftArm", "rightArm", "grabber", "parking"};
    private double servoPos;
    private double refinementInterval = 0.05;
    private int servoIndex = 5;

    public void runOpMode() throws InterruptedException {

        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);
        servos = new Servo[] {Agobot.dragger.leftDragger, Agobot.dragger.rightDragger, Agobot.arm.leftArm, Agobot.arm.rightArm, Agobot.grabber.grabber, Agobot.parker.parkingServo};
        servoPos = servos[servoIndex].getPosition();

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.dpad_left) {

                servoIndex--;

                if (servoIndex == -1) {

                    servoIndex = servos.length - 1;
                }

                servoPos = servos[servoIndex].getPosition();

                while (opModeIsActive() && gamepad1.dpad_left);
            }

            if (gamepad1.dpad_right) {

                servoIndex++;

                if (servoIndex == servos.length) {

                    servoIndex = 0;
                }

                servoPos = servos[servoIndex].getPosition();

                while (opModeIsActive() && gamepad1.dpad_right);
            }

            if (gamepad1.dpad_up) {

                if (servoPos < 1) {

                    servoPos += refinementInterval;
                }

                servos[servoIndex].setPosition(servoPos);
                while (opModeIsActive() && gamepad1.dpad_up);
            }

            if (gamepad1.dpad_down) {

                if (servoPos > 0) {

                    servoPos -= refinementInterval;
                }

                servos[servoIndex].setPosition(servoPos);
                while (opModeIsActive() && gamepad1.dpad_down);
            }

            telemetry.addData("Current Servo", servoNames[servoIndex]);
            telemetry.addData("Current Position", servos[servoIndex].getPosition());
            telemetry.update();
        }
    }
}
