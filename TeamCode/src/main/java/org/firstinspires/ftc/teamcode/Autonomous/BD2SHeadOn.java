package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.PIDControllers;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;
import org.virus.vision.StripDetector;

import java.io.File;
import java.util.Arrays;

@Autonomous(name = "BD2S HEAD ON", group = "Auto")
public class BD2SHeadOn extends LinearOpMode {

    //NOT DONE!!!!!!! -Ere

    private Vector2D startPosition = new Vector2D(-63, -36); //against wall to the right
    private double startHeading = 90; //straight left
    private String skyStoneLocation = "Middle";
    //TODO: test diagonal angle for colecting stones
    private int resetTime = 29500;
    private int diagonalAngle = 0;
    private int stone1Offset = 0;
    private int stone2Offset = 0;
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");

    /*Autonomous has 6 Stages
    1) Scan
    2) Collect Skystone 1 & deliver to foundation
    3) Drag foundation close
    4) Collect Skystone 2 & deliver (+ more maybe)
    5) push foundation to build site
    6) Park
     */


    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.alliance = "blue";
        Agobot.initializeWithVision(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        while(!isStarted()) {
            skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
            telemetry.addData("Sky stone position", skyStoneLocation);
            telemetry.update();
        }

        waitForStart();
        Agobot.autoStart();

        // deploy intake as the very first action
        Agobot.grabber.grab(false);
        Agobot.arm.armFlipOut(true); //go from in to standby
        Agobot.intake.deployIntake();

//        waitForStart();
//        Agobot.autoStart();
//
//        // deploy intake as the very first action
//        Agobot.grabber.grab(false);
//        Agobot.arm.armFlipOut(true); //go from in to standby
//        Agobot.intake.deployIntake();
//
//
//        while (Agobot.drivetrain.goToPosition(startPosition, startHeading, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + 29000))) {
//
//        }
//
//        double startVision = Agobot.clock.milliseconds();
//        while (Agobot.clock.milliseconds() < (startVision + 1000) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + 29000))) {
//        } // make the robot wait for one second before reading the stones to ensure stable position
//        skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
//        telemetry.addData("Sky stone position", skyStoneLocation);
//        telemetry.update();

        //COLLECT SKYSTONE #1 & DELIVER------------------------------------------------------------------------------------------------
        // grab skystone
        Agobot.intake.runIntake(1);
        Agobot.intake.runIntake(1);


        //TODO: need to test values for second skystone
        int yOffset = -51;

        if (skyStoneLocation.equals("Right")) {

            yOffset += 4;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset += 12;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset += 20;
        }

        //go at angle to collect inner set of stones
//        while(Agobot.drivetrain.goToPosition(new Vector2D(-38, yOffset - 8), diagonalAngle1, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + 29500))){
//            Agobot.intake.runIntake(-1);
//            Agobot.intake.getLeft().setPower(-1);
//            Agobot.intake.getRight().setPower(-1);
//        }

        while(Agobot.drivetrain.goToPosition(new Vector2D(-32, yOffset - stone1Offset), diagonalAngle, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        //run diagonal at stones
        double timeCondition = Agobot.clock.milliseconds();
        while((Agobot.clock.milliseconds() < timeCondition + 2000) && (Agobot.drivetrain.goToPosition(new Vector2D(-24, yOffset - stone1Offset), diagonalAngle, 0.5,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime)))){

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startIntake + 300) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        Agobot.arm.armFlipOut(false); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 400) && opModeIsActive()) {}
        Agobot.intake.runIntake(0);
        Agobot.grabber.grab(true);


        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        Agobot.arm.armFlipOut(false);

        //Deliver skystone 1 to foundation
        // move back out of the way
        while(Agobot.drivetrain.goToPosition(new Vector2D(-37.5, yOffset + 2), 180, 1,1.3,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

        }

        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-37.5, 44), 180, 1,1.5,1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            Agobot.grabber.grab(true);

        }

        //get closer to foundation
        PIDControllers.xController.changeConstants(.1f,.6f ,0.001f,0.3f);
        PIDControllers.yController.changeConstants(.1f,.6f ,0.001f,0.3f);
        while(Agobot.drivetrain.goToPosition(new Vector2D(-31.5, 44), 180, 0.4, 1.2) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

        }
        Agobot.arm.armFlipOut(true);
        Agobot.arm.armFlipOut(true);

        //DRAG FOUNDATION CLOSE------------------------------------------------------------------------------------------------
        Agobot.dragger.drag(true);
        startGrab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (startGrab + 1000) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {};

        //change x and y to pull foundation
        PIDControllers.xController.changeConstants(.09f,.3f ,0.001f,0.3f);
        PIDControllers.yController.changeConstants(.09f,.3f ,0.001f,0.3f);

        double startDrag = Agobot.clock.milliseconds();
        //drag foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-45, 44), 180, .8,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            if(Agobot.clock.milliseconds() > startDrag + 300){
                Agobot.grabber.grab(false);
            }
            Agobot.intake.runIntake(-1);
        }

        Agobot.arm.armFlipOut(false);
        Agobot.intake.runIntake(0);

        //change heading constant for rotating foundation
        PIDControllers.headingController.changeConstants(-.5f, -.15f,-.001f, 1.5f);

        while(Agobot.drivetrain.goToPosition(new Vector2D(-37, 36), 270, 1,1.5,2) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

        }

        Agobot.dragger.drag(false);
        startGrab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (startGrab + 300) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        //Revert PID constants back to normal
        PIDControllers.headingController.changeConstants(-.065f, -.009f,-.001f, .1f);
        PIDControllers.xController.changeConstants(.07f,.16f ,0.001f,0.15f);
        PIDControllers.yController.changeConstants(.07f,.16f ,0.001f,0.15f);

        //COLLECT SKYSTONE #2 & DELIVER------------------------------------------------------------------------------------------------
        // grab skystone
        // grab skystone
        Agobot.intake.runIntake(-1);
        Agobot.intake.runIntake(-1);

        yOffset = -45;

        if (skyStoneLocation.equals("Right")) {

            yOffset -= 19;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset -= 12;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset -= 4;
        }

        //go at angle to collect inner set of stones
        while(Agobot.drivetrain.goToPosition(new Vector2D(-37, yOffset + stone2Offset), 270, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.intake.runIntake(-1);
            Agobot.intake.getLeft().setPower(-1);
            Agobot.intake.getRight().setPower(-1);
        }

        while(Agobot.drivetrain.goToPosition(new Vector2D(-34, yOffset + stone2Offset), diagonalAngle, 0.6, .5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        //run diagonal at stones
        timeCondition = Agobot.clock.milliseconds();
        while((Agobot.clock.milliseconds() < timeCondition + 2000) && Agobot.drivetrain.goToPosition(new Vector2D(-23.5, yOffset + stone2Offset), diagonalAngle, 0.5,1.1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

//        startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
//        while(Agobot.clock.milliseconds() < (startIntake + 200) && opModeIsActive()) {}

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}
        Agobot.grabber.grab(true);


        startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 500) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        Agobot.arm.armFlipOut(true);

        while (Agobot.drivetrain.goToPosition(new Vector2D(-37.5, yOffset), 0, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        //Deliver
        while(Agobot.drivetrain.goToPosition(new Vector2D(-37.5, 41), 270, 0.8,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        Agobot.arm.armFlipOut(true);
        double startDeliver = Agobot.clock.milliseconds();
        while (startDeliver + 1000 > Agobot.clock.milliseconds() && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.slides.slides(400);

        }

        Agobot.grabber.grab(false);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startGrab + 200 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        Agobot.arm.armFlipOut(false);
        double startRetract = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startRetract + 400 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

//        //Change PID constants for pushing foundation
//        PIDControllers.xController.changeConstants(.09f,.3f ,0.001f,0.3f);
//        PIDControllers.yController.changeConstants(.09f,.3f ,0.001f,0.3f);
//        //PIDControllers.headingController.changeConstants(-.09f, -.012f,-.001f, .2f);
//
//        //PUSH FOUNDATION TO BUILD SITE------------------------------------------------------------------------------------------------
//        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 40), 270, 0.6) && opModeIsActive()){
//
//        }
//
//        //Revert PID constants back to normal
//        //PIDControllers.headingController.changeConstants(-.065f, -.009f,-.001f, .1f);
//        PIDControllers.xController.changeConstants(.07f,.16f ,0.001f,0.15f);
//        PIDControllers.yController.changeConstants(.07f,.16f ,0.001f,0.15f);

        //PARK------------------------------------------------------------------------------------------------
        while(Agobot.drivetrain.goToPosition(new Vector2D(-39, 0), 270, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            Agobot.slides.slides(0);
        }

        ReadWriteFile.writeFile(opModeData, "Blue," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }
}
