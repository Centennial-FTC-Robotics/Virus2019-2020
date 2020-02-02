package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.openftc.revextensions2.RevBulkData;
import org.virus.agobot.Agobot;
import org.virus.agobot.PIDControllers;
import org.virus.util.Vector2D;

import java.io.File;
import java.util.Arrays;

@TeleOp(group = "TeleOP", name = "FieldCentricTeleOp")
public class FieldCentricTeleOp extends LinearOpMode {

    public Vector2D leftStick;
    public Vector2D rightStick;
    public Vector2D motorSpeeds;
    double speedMultiplier = 1;
    double gp1RightTriggerPrev = 0;
    double gp2LeftTriggerPrev = 0;
    double gp2rightTriggerPrev = 0;
    private File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");
    String importedAutoData;
    String[] autoData = new String[4];
    public Vector2D startPosition;
    double startHeading;
    double driverHeading;
    double snap90Correction = 0;
    @Override
    public void runOpMode() throws InterruptedException {

        // import the file data and integrate it
        importedAutoData = ReadWriteFile.readFile(opModeData).trim();
        if(importedAutoData.equals("")){ // in case there is no data
            startPosition = new Vector2D(0,0);
            startHeading = 90;
            driverHeading = 90;
        }else{ // if there is data, assume it's properly formatted I guess. Thanks Keertik...
            autoData = importedAutoData.split(",");
            if(autoData[0] == "Red"){
                driverHeading = 180;
            }else{
                driverHeading = 0;
            }
            startPosition = new Vector2D(Double.parseDouble(autoData[1]), Double.parseDouble(autoData[2]));
            startHeading = Double.parseDouble(autoData[3]);
        }

        // actual initialization time
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();
        ReadWriteFile.writeFile(opModeData, "");
        leftStick = new Vector2D((double) gamepad1.left_stick_x, (double) -gamepad1.left_stick_y);
        rightStick = new Vector2D((double) gamepad1.right_stick_x, (double) gamepad1.right_stick_y);
        while(opModeIsActive()) {

            if (gamepad1.a && gamepad1.b && gamepad1.dpad_left && gamepad1.dpad_up) {

                driverHeading = Agobot.drivetrain.odometry.currentHeading();
            }

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

            speedMultiplier = 1 - (0.7 * gamepad1.left_trigger);

            if ((leftStick.getComponent(0) != 0) || (leftStick.getComponent(1) != 0)){
                Agobot.drivetrain.runMotors(speedMultiplier*diagSpeed1, speedMultiplier*diagSpeed2, speedMultiplier*diagSpeed2, speedMultiplier*diagSpeed1, speedMultiplier*rightStick.getComponent(0) + snap90Correction); //var1 and 2 are computed values found in theUpdateControllerValues method
            } else {
                Agobot.drivetrain.runMotors(0, 0, 0, 0, rightStick.getComponent(0) + snap90Correction);
            }

            //slides
            Agobot.slides.slidePower(-0.5 * gamepad2.left_stick_y + -0.2 * gamepad2.right_stick_y);

            //arm
            if(gp2LeftTriggerPrev < 0.1 && gamepad2.left_trigger > 0.1) { //only calls if trigger is pressed and on previous loop iteration it wasn't

                Agobot.arm.armFlipOut(false);
            }else if(gp2rightTriggerPrev < 0.1 && gamepad2.right_trigger > 0.1){
                Agobot.arm.armFlipOut(true);
            }

            //grabber
            if(gamepad2.b) {
                Agobot.grabber.grab(true);
            }
            if(gamepad2.a){
                Agobot.grabber.grab(false);
            }

            //intake
            if(gamepad2.right_bumper){
                Agobot.intake.runIntake(1);
            }else if(gamepad2.left_bumper){
                Agobot.intake.runIntake(-0.7);
            }else{
                Agobot.intake.runIntake(0);
            }

            //foundation dragger
            if(gamepad2.x) {
                Agobot.dragger.drag(false);
            }

            if(gamepad2.y){
                Agobot.dragger.drag(true);
            }

            //snap 90 for driver 1

            if (gamepad1.right_trigger > 0.001) {

                snap90Correction = PIDControllers.headingController.getValue((float) Math.abs((Agobot.drivetrain.odometry.currentHeading() / 90.0) - Math.round(Agobot.drivetrain.odometry.currentHeading() / 90.0)));
            } else {

                snap90Correction = 0;
            }

            //TODO: dpad stone heights
            int startStoneHeight = 24;
            //TODO: test for what encoder values mean 1 stone height
            int stoneHeight = 340;
            /*int currentSlideHeight = Agobot.slides.getPosition();
            if(gamepad2.dpad_up){
                Agobot.slides.slides(currentSlideHeight + stoneHeight);
            }
            if(gamepad2.dpad_down){
                Agobot.slides.slides(currentSlideHeight - stoneHeight);
            }*/

            gp1RightTriggerPrev = gamepad1.right_trigger;
            gp2LeftTriggerPrev = gamepad2.left_trigger;
            gp2rightTriggerPrev = gamepad2.right_trigger;

//            telemetry.addData("cool if statement", Math.abs((Agobot.drivetrain.odometry.currentHeading() / 90.0) - Math.round(Agobot.drivetrain.odometry.currentHeading() / 90.0)));
//            telemetry.addData("Right Trigger gp1", gamepad1.right_trigger);
//            telemetry.addData("Right Stick gp1", Math.abs(gamepad1.right_stick_x));
//            telemetry.addData("is90", is90);

//            telemetry.addData("leftStick: ", leftStick.toString());
           // telemetry.addData("Heading: ", Agobot.drivetrain.getHeading());
            telemetry.addData("Arm State Number", Agobot.arm.armPosition);
            telemetry.addData("Arm State", Agobot.arm.getArmPosition());
//            telemetry.addData("Arm Number", Agobot.arm.armPosition);
//            telemetry.addData("GamePad 2 Left Joystick Y", gamepad2.left_stick_y);
//            telemetry.addData("Slide Target Pos", Agobot.slides.holdSlidePos);
            //telemetry.addData("Current Slide Pos", Agobot.slides.getPosition());
//            telemetry.addData("Left Slide Pos", Agobot.slides.slideLeft.getCurrentPosition());
//            telemetry.addData("Right Slide Pos", Agobot.slides.slideRight.getCurrentPosition());
            telemetry.update();
        }
    }

    public void updateControllerValues() {

        leftStick.setComponents(new double[] {gamepad1.left_stick_x, -gamepad1.left_stick_y});
        rightStick.setComponents(new double[] {gamepad1.right_stick_x, -gamepad1.right_stick_y});
        Agobot.drivetrain.updatePosition();
        leftStick.rotate(Math.toRadians(driverHeading - Agobot.drivetrain.getHeading()));
        double leftx = leftStick.getComponent(0);
        double lefty = leftStick.getComponent(1);
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(Math.pow(lefty, 2) + Math.pow(leftx, 2));

        motorSpeeds = new Vector2D((lefty+leftx)*magnitude/scalar, (lefty-leftx)*magnitude/scalar);
    }
}
