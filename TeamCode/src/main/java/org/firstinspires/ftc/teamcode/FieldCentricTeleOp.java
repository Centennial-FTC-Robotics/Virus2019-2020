package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

import java.util.Arrays;

@TeleOp(group = "TeleOP", name = "FieldCentricTeleOp")
public class FieldCentricTeleOp extends LinearOpMode {

    public Vector2D leftStick;
    public Vector2D rightStick;
    public Vector2D motorSpeeds;

    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        waitForStart();

        leftStick = new Vector2D((double) gamepad1.left_stick_x, (double) -gamepad1.left_stick_y);
        rightStick = new Vector2D((double) gamepad1.right_stick_x, (double) gamepad1.right_stick_y);

        while(opModeIsActive()) {
            updateControllerValues();
            double diagSpeed1 = motorSpeeds.getComponent(0);
            double diagSpeed2 = motorSpeeds.getComponent(1);
//            telemetry.addData("lFront:", diagSpeed1);
//            telemetry.addData("lBack:", diagSpeed2);
//            telemetry.addData("rFront:", diagSpeed2);
//            telemetry.addData("rBack:", diagSpeed1);
            if (!Arrays.equals(leftStick.getComponents(), new double[] {0, 0}) || !Arrays.equals(rightStick.getComponents(), new double[] {0, 0})) {
                telemetry.update();
            }

            if ((leftStick.getComponent(0) != 0) || (leftStick.getComponent(1) != 0)){
                Agobot.drivetrain.runMotors(diagSpeed1, diagSpeed2, diagSpeed2, diagSpeed1, rightStick.getComponent(0)); //var1 and 2 are computed values found in theUpdateControllerValues method
            } else {
                Agobot.drivetrain.runMotors(0, 0, 0, 0, rightStick.getComponent(0));
            }

            //TODO: brake and throttle
            //TODO: slides, arm, intake, grabber
            Agobot.slides.slidePower(gamepad2.left_stick_y);

            if(gamepad1.right_bumper){
                if(!Agobot.intake.deployIntake()){ //deployIntake returns true when the intake has been deployed and returns false otherwise and deploys the intake
                    Agobot.intake.runIntake(1);
                }
            }else if(gamepad1.left_bumper){
                if(!Agobot.intake.deployIntake()){
                    Agobot.intake.runIntake(-1);
                }
            }else{
                Agobot.intake.runIntake(0);
            }
            //TODO: shortcut buttons (automatic foundation pulling)
            //TODO: snap 90 for driver 1
            //TODO: dpad stone heights
        }
    }

    public void updateControllerValues(){
        leftStick.setComponents(new double[] {gamepad1.left_stick_x, -gamepad1.left_stick_y});
        rightStick.setComponents(new double[] {gamepad1.right_stick_x, gamepad1.right_stick_y});
        telemetry.addData("leftStick: ", leftStick.toString());
        Agobot.drivetrain.updatePosition();
        leftStick.rotate(-Math.toRadians(Agobot.drivetrain.getHeading()));
        telemetry.addData("Heading: ", Agobot.drivetrain.getHeading());
        telemetry.addData("leftStick Rotated: ", leftStick.toString());

        double leftx = -leftStick.getComponent(0);
        double lefty = leftStick.getComponent(1);
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(Math.pow(lefty, 2) + Math.pow(leftx, 2));

        motorSpeeds = new Vector2D((lefty-leftx)*magnitude/scalar, (lefty+leftx)*magnitude/scalar);
    }
}
