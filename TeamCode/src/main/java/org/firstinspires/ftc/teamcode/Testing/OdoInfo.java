package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

//@TeleOp(group = "TeleOP", name = "Odometry Information")
public class OdoInfo extends LinearOpMode {

    Vector2D leftStick;
    Vector2D rightStick;
    double lefty;
    double leftx;
    Vector2D motorSpeeds;

    // testing
    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 180; //straight left

    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.initialize(this);
//        Agobot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();
        while(opModeIsActive()) {
            //updateControllerValues();
            //Agobot.drivetrain.runMotors(0,0.5,0,-0.5, 0);

            telemetry.addData("Position: ", Agobot.drivetrain.updatePosition());
            telemetry.addData("Relative Heading: ", Agobot.drivetrain.getHeading());
            telemetry.addData("Heading: ", Agobot.drivetrain.odometry.currentHeading());

            telemetry.addData("lEncoder:", Agobot.drivetrain.odometry.getlEncoderCounts());
            telemetry.addData("rEncoder:", Agobot.drivetrain.odometry.getrEncoderCounts());
            telemetry.addData("bEncoder:", Agobot.drivetrain.odometry.getbEncoderCounts());


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
