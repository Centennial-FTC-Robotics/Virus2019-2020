package org.firstinspires.ftc.teamcode.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.PIDControllers;
import org.virus.util.Vector2D;

import java.io.File;

@Autonomous(group = "Autonomous", name = " Red Straight")
public class RedStraight2S extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private String skyStoneLocation;
    private int resetTime = 29500;
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");
    Vector2D[] skystonePositions = new Vector2D[6];
    Vector2D[] relativeSkystonePos = new Vector2D[6];

    public void runOpMode() {

        //initialize the fun static variables!
        Agobot.alliance = "red";
        Agobot.initializeWithVision(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        //generate skystone positions
        double x = 22;
        double y = -68;

        for (int p = 0; p < 6; p++) {

            skystonePositions[p] = new Vector2D(x, y + (p * 8));
            relativeSkystonePos[p] = Vector2D.sub(new Vector2D(skystonePositions[p]), startPosition);
        }


        //SCAN STONE-------------------------------------------------------------------------------------------------------------------
        while (!isStarted()) {
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

        Vector2D goToPos = skystonePositions[1];
        double angle = relativeSkystonePos[1].getTheta();

        if (skyStoneLocation.equals("Right")) {

            goToPos = skystonePositions[2];
            angle = relativeSkystonePos[2].getTheta();
        } else if (skyStoneLocation.equals("Left")) {

            goToPos = skystonePositions[0];
            angle = relativeSkystonePos[0].getTheta();
        }

        goToPos.sub(new Vector2D(angle, 8.0, true));

        //run diagonal at stones
        double timeCondition = Agobot.clock.milliseconds();
        while ((Agobot.clock.milliseconds() < timeCondition + 2000) && Agobot.drivetrain.goToPosition(goToPos, angle, 0.4, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startIntake + 300) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive()) {
        }
        Agobot.grabber.grab(true);


        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.armFlipOut(true); //go from in to standby

        // move back out of the way
        while (Agobot.drivetrain.goToPosition(new Vector2D(38, goToPos.getComponent(1)), 0, 1, 1.3, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        //go to foundation
        while (Agobot.drivetrain.goToPosition(new Vector2D(38, 44), 0, 1, 1.5, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        //get closer to foundation
        PIDControllers.xController.changeConstants(.1f, .6f, 0.001f, 0.3f);
        PIDControllers.yController.changeConstants(.1f, .6f, 0.001f, 0.3f);
        while (Agobot.drivetrain.goToPosition(new Vector2D(31.5, 44), 0, 0.3, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }
        Agobot.arm.armFlipOut(true);

        //DRAG FOUNDATION CLOSE------------------------------------------------------------------------------------------------
        Agobot.dragger.drag(true);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (startGrab + 1000) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }
        ;

        //change x and y to pull foundation
        PIDControllers.xController.changeConstants(.09f, .3f, 0.001f, 0.3f);
        PIDControllers.yController.changeConstants(.09f, .3f, 0.001f, 0.3f);

        double startDrag = Agobot.clock.milliseconds();
        //drag foundation
        while (Agobot.drivetrain.goToPosition(new Vector2D(45, 44), 0, .8, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            if (Agobot.clock.milliseconds() > startDrag + 300) {
                Agobot.grabber.grab(false);
            }
        }

        Agobot.arm.armFlipOut(false);

        //change heading constant for rotating foundation
        PIDControllers.headingController.changeConstants(-.5f, -.15f, -.001f, 1.5f);

        while (Agobot.drivetrain.goToPosition(new Vector2D(42, 30), 270, 1, 1.5, 2) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        Agobot.dragger.drag(false);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        //Revert PID constants back to normal
        PIDControllers.headingController.changeConstants(-.065f, -.009f, -.001f, .1f);
        PIDControllers.xController.changeConstants(.07f, .16f, 0.001f, 0.15f);
        PIDControllers.yController.changeConstants(.07f, .16f, 0.001f, 0.15f);

        //COLLECT SKYSTONE #2 & DELIVER------------------------------------------------------------------------------------------------
        // grab skystone
        Agobot.intake.runIntake(1);
        Agobot.intake.runIntake(1);

        goToPos = skystonePositions[4];

        if (skyStoneLocation.equals("Right")) {

            goToPos = skystonePositions[5];
        } else if (skyStoneLocation.equals("Left")) {

            goToPos = skystonePositions[3];
        }

        Vector2D afterDeliverVector = Vector2D.add(goToPos, new Vector2D(16, 16));
        goToPos.add(new Vector2D(45, 8.0, false)); // center of robot is 8 inches away from stone

        while (Agobot.drivetrain.goToPosition(afterDeliverVector, 225, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.intake.runIntake(-1);
            Agobot.intake.getLeft().setPower(-1);
            Agobot.intake.getRight().setPower(-1);
        }

        //run diagonal at stones
        timeCondition = Agobot.clock.milliseconds();
        while ((Agobot.clock.milliseconds() < timeCondition + 2000) && Agobot.drivetrain.goToPosition(goToPos, 225, 0.4) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        grab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }
        Agobot.grabber.grab(true);


        startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.armFlipOut(true);// from in to standby

        while (Agobot.drivetrain.goToPosition(new Vector2D(38, goToPos.getComponent(1)), 180, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        //Deliver
        while (Agobot.drivetrain.goToPosition(new Vector2D(38, 41), 270, 1, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        Agobot.arm.armFlipOut(true);
        double startDeliver = Agobot.clock.milliseconds();
        while (startDeliver + 1000 > Agobot.clock.milliseconds() && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.slides.slides(400);
        }

        Agobot.grabber.grab(false);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startGrab + 300 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.armFlipOut(false);
        double startRetract = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startRetract + 400 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

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
        while (Agobot.drivetrain.goToPosition(new Vector2D(38, 0), 270, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.slides.slides(0);
        }

        ReadWriteFile.writeFile(opModeData, "Red," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }
}
