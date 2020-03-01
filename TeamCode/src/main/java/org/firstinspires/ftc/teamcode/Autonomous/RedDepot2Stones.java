package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.PIDControllers;
import org.virus.purepursuit.PurePursuitPath;
import org.virus.purepursuit.Waypoint;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;
import org.virus.vision.StripDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

//@Autonomous(name = "Red Depot 2 Stones", group = "Auto")
public class RedDepot2Stones extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private String skyStoneLocation = "Middle";
    private int resetTime = 29500;
    private int diagonalAngle1 = 235;
    private int diagonalAngle2 = 120;
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

        Agobot.alliance = "red";
        Agobot.initializeWithVision(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        //SCAN STONE-------------------------------------------------------------------------------------------------------------------
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

        //COLLECT SKYSTONE #1 & DELIVER------------------------------------------------------------------------------------------------
        //get ready to pick up skystone
        //TODO: test values for y offset for diagonal getting stones
        int yOffset = -45;

        if (skyStoneLocation.equals("Right")) {

            yOffset -= 4;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset -= 12;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset -= 19;
        }

        // grab skystone
        Agobot.intake.runIntake(1);
        Agobot.intake.runIntake(1);


//        //go at angle to collect inner set of stones
//        while(Agobot.drivetrain.goToPosition(new Vector2D(38, yOffset + 10), diagonalAngle1, 0.6) && opModeIsActive()){
//            Agobot.intake.runIntake(1);
//            Agobot.intake.getLeft().setPower(1);
//            Agobot.intake.getRight().setPower(1);
//        }

        while(Agobot.drivetrain.goToPosition(new Vector2D(30, yOffset + 10), diagonalAngle1, 0.6, .5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
//            Agobot.intake.runIntake(1);
//            Agobot.intake.getLeft().setPower(1);
//            Agobot.intake.getRight().setPower(1);
        }

        Agobot.intake.runIntake(1);
        Agobot.intake.getLeft().setPower(1);
        Agobot.intake.getRight().setPower(1);

        //run diagonal at stones
        double timeCondition = Agobot.clock.milliseconds();
        while((Agobot.clock.milliseconds() < timeCondition + 2000) && Agobot.drivetrain.goToPosition(new Vector2D(24, yOffset + 2.5), diagonalAngle1, 0.4,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startIntake + 300) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive()) {}
        Agobot.grabber.grab(true);


        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        Agobot.arm.armFlipOut(true); //go from in to standby

        // move back out of the way
        while(Agobot.drivetrain.goToPosition(new Vector2D(38, yOffset + 2), 0, 1,1.3,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

        }

        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(38, 44), 0, 1,1.5,1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
        }

        //get closer to foundation
        PIDControllers.xController.changeConstants(.1f,.6f ,0.001f,0.3f);
        PIDControllers.yController.changeConstants(.1f,.6f ,0.001f,0.3f);
        while(Agobot.drivetrain.goToPosition(new Vector2D(31.5, 44), 0, 0.3, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

        }
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
        while(Agobot.drivetrain.goToPosition(new Vector2D(45, 44), 0, .8,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            if(Agobot.clock.milliseconds() > startDrag + 300){
                Agobot.grabber.grab(false);
            }
        }

        Agobot.arm.armFlipOut(false);

        //change heading constant for rotating foundation
        PIDControllers.headingController.changeConstants(-.5f, -.15f,-.001f, 1.5f);

        while(Agobot.drivetrain.goToPosition(new Vector2D(38, 30), 270, 1,1.5,2) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

        }

        Agobot.dragger.drag(false);
        startGrab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        //Revert PID constants back to normal
        PIDControllers.headingController.changeConstants(-.065f, -.009f,-.001f, .1f);
        PIDControllers.xController.changeConstants(.07f,.16f ,0.001f,0.15f);
        PIDControllers.yController.changeConstants(.07f,.16f ,0.001f,0.15f);

        //COLLECT SKYSTONE #2 & DELIVER------------------------------------------------------------------------------------------------
        // grab skystone
        // grab skystone
        Agobot.intake.runIntake(1);
        Agobot.intake.runIntake(1);


        //TODO: need to test values for second skystone
        yOffset = -51;

        if (skyStoneLocation.equals("Right")) {

            yOffset += 20;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset += 12;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset += 6;
        }

        //go at angle to collect inner set of stones
        while(Agobot.drivetrain.goToPosition(new Vector2D(38, yOffset - 8), 270, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            Agobot.intake.runIntake(-1);
            Agobot.intake.getLeft().setPower(-1);
            Agobot.intake.getRight().setPower(-1);
        }

        while(Agobot.drivetrain.goToPosition(new Vector2D(28, yOffset - 8), diagonalAngle2, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        //run diagonal at stones
        timeCondition = Agobot.clock.milliseconds();
        while((Agobot.clock.milliseconds() < timeCondition + 2000) && Agobot.drivetrain.goToPosition(new Vector2D(23, yOffset - 1.5), diagonalAngle2, 0.4) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

//        startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
//        while(Agobot.clock.milliseconds() < (startIntake + 200) && opModeIsActive()) {}

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}
        Agobot.grabber.grab(true);


        startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

        Agobot.arm.armFlipOut(true);

        while (Agobot.drivetrain.goToPosition(new Vector2D(38, yOffset), 180, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        //Deliver
        while(Agobot.drivetrain.goToPosition(new Vector2D(38, 41), 270, 1,1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        Agobot.arm.armFlipOut(true);
        double startDeliver = Agobot.clock.milliseconds();
        while (startDeliver + 1000 > Agobot.clock.milliseconds() && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.slides.slides(400);

        }

        Agobot.grabber.grab(false);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startGrab + 300 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {}

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
        while(Agobot.drivetrain.goToPosition(new Vector2D(38, 0), 270, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            Agobot.slides.slides(0);
        }

        ReadWriteFile.writeFile(opModeData, "Red," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }
}
