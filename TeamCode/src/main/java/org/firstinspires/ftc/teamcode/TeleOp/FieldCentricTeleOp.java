package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.Arm;
import org.virus.agobot.PIDControllers;
import org.virus.util.Vector2D;

import java.io.File;

@TeleOp(group = "TeleOP", name = "FieldCentricTeleOp")
public class FieldCentricTeleOp extends LinearOpMode {

    public Vector2D leftStick;
    public Vector2D rightStick;
    public Vector2D motorSpeeds;
    double speedMultiplier = 1;
    double gp1RightTriggerPrev = 0;
    double gp2LeftTriggerPrev = 0;
    double gp2rightTriggerPrev = 0;
    boolean gp2upPrev = false;
    boolean gp2downPrev = false;
    private File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");
    String importedAutoData;
    String[] autoData = new String[4];
    public Vector2D startPosition;
    double startHeading;
    double driverHeading;
    double snap90Correction = 0;
    int towerHeight = 0;

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
            if(autoData[0].equals("Red")){
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

            Agobot.arm.setArmPos(Agobot.arm.leftPosition, Agobot.arm.rightPosition);

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
//            if (!Arrays.equals(leftStick.getComponents(), new double[]{0, 0}) || !Arrays.equals(rightStick.getComponents(), new double[]{0, 0})) {
//                telemetry.update();
//            }

            speedMultiplier = 1 - (0.7 * gamepad1.left_trigger);

            if ((leftStick.getComponent(0) != 0) || (leftStick.getComponent(1) != 0)) {
                Agobot.drivetrain.runMotors(speedMultiplier * diagSpeed1, speedMultiplier * diagSpeed2, speedMultiplier * diagSpeed2, speedMultiplier * diagSpeed1, speedMultiplier * rightStick.getComponent(0) + snap90Correction); //var1 and 2 are computed values found in theUpdateControllerValues method
            } else {
                Agobot.drivetrain.runMotors(0, 0, 0, 0, speedMultiplier * rightStick.getComponent(0) + snap90Correction);
            }

            //slides
            Agobot.slides.slidePower(-0.5 * gamepad2.left_stick_y + -0.2 * gamepad2.right_stick_y);

            //arm
            if (gp2LeftTriggerPrev < 0.1 && gamepad2.left_trigger > 0.1) { //only calls if trigger is pressed and on previous loop iteration it wasn't

                Agobot.arm.armFlipOut(false);
            } else if (gp2rightTriggerPrev < 0.1 && gamepad2.right_trigger > 0.1) {
                Agobot.arm.armFlipOut(true);
            }

            //grabber
            if (gamepad2.b) {
                Agobot.grabber.grab(true);
            }
            if (gamepad2.a) {
                Agobot.grabber.grab(false);
            }

            //intake
            if(gamepad2.left_bumper && gamepad2.right_bumper){
                Agobot.intake.deployIntake();
            }else if(gamepad2.right_bumper){
                Agobot.intake.runIntake(0.7);
            }else if(gamepad2.left_bumper){
                Agobot.intake.runIntake(-0.3);
            } else{
                Agobot.intake.runIntake(0);
            }

            //foundation dragger
            if(gamepad2.x) {
                Agobot.dragger.drag(false);
            }

            if(gamepad2.y){
                Agobot.dragger.drag(true);
            }

            //capstone dropper
            if(gamepad2.back) {
                Agobot.capstone.drop(true);
            }else{
                Agobot.capstone.drop(false);
            }

            //snap 90 for driver 1

            if (gamepad1.right_trigger > 0.001) {

                snap90Correction = PIDControllers.headingController.getValue((float) Math.abs((Agobot.drivetrain.odometry.currentHeading() / 90.0) - Math.round(Agobot.drivetrain.odometry.currentHeading() / 90.0)));
            } else {

                snap90Correction = 0;
            }

            if(gamepad1.x && gamepad1.y){
                Agobot.parker.extend(true);
            }



            if(gamepad2.dpad_up && !gp2upPrev){ //if pressed this loop but not in the previous one
                towerHeight++;
                Agobot.slides.goToStoneHeight(towerHeight);
                if(Math.abs(Agobot.slides.getPosition() - Agobot.slides.stoneHeight(towerHeight)) < 100){ //when slides are almost there, flip the arm
                    if(towerHeight < 8){
                        Agobot.arm.setArmPos(Arm.armPosition.drop);
                    }else{
                        Agobot.arm.setArmPos(Arm.armPosition.topstone);
                    }
                }
            }
            if(gamepad2.dpad_down && !gp2downPrev){ //pondering whether I want this going down a stone or retracting all the way
                towerHeight--;
                Agobot.slides.goToStoneHeight(towerHeight);
            }

            gp1RightTriggerPrev = gamepad1.right_trigger;
            gp2LeftTriggerPrev = gamepad2.left_trigger;
            gp2rightTriggerPrev = gamepad2.right_trigger;
            gp2upPrev = gamepad2.dpad_up;
            gp2downPrev = gamepad2.dpad_down;

//            telemetry.addData("cool if statement", Math.abs((Agobot.drivetrain.odometry.currentHeading() / 90.0) - Math.round(Agobot.drivetrain.odometry.currentHeading() / 90.0)));
//            telemetry.addData("Right Trigger gp1", gamepad1.right_trigger);
//            telemetry.addData("Right Stick gp1", Math.abs(gamepad1.right_stick_x));
//            telemetry.addData("is90", is90);

//            telemetry.addData("leftStick: ", leftStick.toString());
           // telemetry.addData("Heading: ", Agobot.drivetrain.getHeading());
            telemetry.addData("Arm State Number", Agobot.arm.armPos);
            telemetry.addData("Arm State", Agobot.arm.getArmPosition());
            telemetry.addData("Arm Position", Agobot.arm.getArmPosition());
            telemetry.addData("Slide right Pos", Agobot.slides.slideRight.getCurrentPosition());
            telemetry.addData("Slide left Pos", Agobot.slides.slideLeft.getCurrentPosition());
            telemetry.addData("Change in angle", getHeading());
            telemetry.addData("lEncoder", Agobot.drivetrain.odometry.getlEncoderCounts());
            telemetry.addData("rEncoder", Agobot.drivetrain.odometry.getrEncoderCounts());
            telemetry.addData("bEncoder", Agobot.drivetrain.odometry.getbEncoderCounts());
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

    public double getHeading(){
        Orientation currentOrientation = Agobot.drivetrain.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return currentOrientation.firstAngle;
    }
}
