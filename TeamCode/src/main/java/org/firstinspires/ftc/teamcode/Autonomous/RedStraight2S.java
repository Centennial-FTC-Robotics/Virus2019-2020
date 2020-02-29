package org.firstinspires.ftc.teamcode.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.Arm;
import org.virus.agobot.PIDControllers;
import org.virus.purepursuit.PurePursuitPath;
import org.virus.purepursuit.Waypoint;
import org.virus.util.Vector2D;

import java.io.File;
import java.util.ArrayList;

@Autonomous(group = "Autonomous", name = "Red Straight")
public class RedStraight2S extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private String skyStoneLocation = "Middle";
    private int resetTime = 29500;
    private Vector2D stone1Offset = new Vector2D(4, 6);
    private Vector2D stone2Offset = new Vector2D(5.5, 8);
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");
    private Vector2D[] skystonePositions = new Vector2D[6];
    private Vector2D[] relativeSkystonePos = new Vector2D[6];

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
        telemetry.speak("gamer time");
        Agobot.autoStart();

        // deploy intake as the very first action
        Agobot.grabber.grab(false);
        Agobot.arm.setArmPos(Arm.armPosition.standby); //go from in to standby
        Agobot.intake.deployIntake();

        Vector2D goToPos = Vector2D.add(skystonePositions[1], stone1Offset);
        double angle = relativeSkystonePos[1].getTheta();

        if (skyStoneLocation.equals("Right")) {

            goToPos = Vector2D.add(skystonePositions[2], stone1Offset);
            angle = relativeSkystonePos[2].getTheta();
        } else if (skyStoneLocation.equals("Left")) {

            goToPos = Vector2D.add(skystonePositions[0], stone1Offset);
            angle = relativeSkystonePos[0].getTheta();
        }
        Vector2D relativeMvmt = Vector2D.sub(goToPos, startPosition);
        angle = relativeMvmt.getTheta();

        goToPos.sub(new Vector2D(angle, 6.0, true));


        ArrayList<Waypoint> stone1Waypoints = new ArrayList<>();
        stone1Waypoints.add(new Waypoint(63, -36, 270));
        stone1Waypoints.add(new Waypoint(Vector2D.sub(goToPos,new Vector2D(angle,5, true)), Math.toDegrees(angle)));
        stone1Waypoints.add(new Waypoint(goToPos, Math.toDegrees(angle)));
        PurePursuitPath stone1 = new PurePursuitPath(stone1Waypoints);

        Agobot.intake.runIntake(1);
        Agobot.intake.getLeft().setPower(1);
        Agobot.intake.getRight().setPower(1);

        //run diagonal at stones
        double timeCondition = Agobot.clock.milliseconds();
        while ((Agobot.clock.milliseconds() < timeCondition + 2000) && opModeIsActive() && Agobot.drivetrain.followPath(stone1, 0.4)  && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            telemetry.addData("Target", stone1.approachPoint);
            telemetry.addData("Current", Agobot.drivetrain.odometry.currentPosition());
            telemetry.update();
        }
//        while ((Agobot.clock.milliseconds() < timeCondition + 2000) && opModeIsActive() && Agobot.drivetrain.goToPosition(goToPos, Math.toDegrees(angle), 0.4, 1.5)  && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
//            telemetry.addData("Target", goToPos);
//            telemetry.addData("Current", Agobot.drivetrain.odometry.currentPosition());
//            telemetry.update();
////            Agobot.intake.runIntake(1);
////            Agobot.intake.getLeft().setPower(1);
////            Agobot.intake.getRight().setPower(1);
//        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startIntake + 800) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.setArmPos(Arm.armPosition.in); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive()) {
        }

        Agobot.grabber.grab(true);
        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) { }
        Agobot.intake.runIntake(0);

        Agobot.arm.setArmPos(Arm.armPosition.standby); //go from in to standby

        ArrayList<Waypoint> toFoundation1Waypoints = new ArrayList<>();
        toFoundation1Waypoints.add(new Waypoint(goToPos, Math.toDegrees(angle)));
        toFoundation1Waypoints.add(new Waypoint(48, goToPos.getComponent(1), 180));
        toFoundation1Waypoints.add(new Waypoint(38.5, -20, 270));
        toFoundation1Waypoints.add(new Waypoint(38, 20, 270));
        toFoundation1Waypoints.add(new Waypoint(42, 45, 0));
        toFoundation1Waypoints.add(new Waypoint(31.5, 45, 0));
        PurePursuitPath toFoundation1 = new PurePursuitPath(toFoundation1Waypoints);

        boolean path0 = false;
        boolean path1 = false;
        boolean path2 = false;
        boolean path3 = false;
        while(Agobot.drivetrain.followPath(toFoundation1, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            if(toFoundation1.currentIndex() == 0){
                path0 = true;
            }
            if(toFoundation1.currentIndex() == 1){
                path1 = true;
            }
            if(toFoundation1.currentIndex() == 2){
                path2 = true;
            }
            if(toFoundation1.currentIndex() == 3){
                path3 = true;
            }
            telemetry.addData("Path 0", path0);
            telemetry.addData("Path 1", path1);
            telemetry.addData("Path 2", path2);
            telemetry.addData("Path 3", path3);
            telemetry.update();
            if (toFoundation1.currentIndex() == 4){ //going to (31.5, 44)
                PIDControllers.xController.changeConstants(.1f, .6f, 0.001f, 0.3f);
                PIDControllers.yController.changeConstants(.1f, .6f, 0.001f, 0.3f);
            }
        }

//        // move back out of the way
//        while (Agobot.drivetrain.goToPosition(new Vector2D(38, goToPos.getComponent(1)), 0, 1, 1.3, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) { }
//
//        //go to foundation
//        while (Agobot.drivetrain.goToPosition(new Vector2D(38, 44), 0, 1, 1.5, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) { }
//
//        //get closer to foundation
//        PIDControllers.xController.changeConstants(.1f, .6f, 0.001f, 0.3f);
//        PIDControllers.yController.changeConstants(.1f, .6f, 0.001f, 0.3f);
//        while (Agobot.drivetrain.goToPosition(new Vector2D(31.5, 44), 0, 0.3, 1) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
//
//        }
        Agobot.arm.setArmPos(Arm.armPosition.drop);

        //DRAG FOUNDATION CLOSE------------------------------------------------------------------------------------------------
        Agobot.dragger.drag(true);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (startGrab + 1000) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

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

        Agobot.arm.setArmPos(Arm.armPosition.standby);

        //change heading constant for rotating foundation
        PIDControllers.headingController.changeConstants(-.5f, -.13f, -.001f, 1.4f);

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

        goToPos = Vector2D.add(skystonePositions[4], stone2Offset);

        if (skyStoneLocation.equals("Right")) {

            goToPos = Vector2D.add(skystonePositions[5], stone2Offset);
        } else if (skyStoneLocation.equals("Left")) {

            goToPos = Vector2D.add(skystonePositions[3], stone2Offset);
        }

        Vector2D afterDeliverVector = Vector2D.add(goToPos, new Vector2D(16.5, 16));
        goToPos.add(new Vector2D(45, 2, false)); // center of robot is 8 inches away from stone

        while (Agobot.drivetrain.goToPosition(afterDeliverVector, 225, 1, 1.125) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
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

        startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startIntake + 800) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.intake.runIntake(0);
        Agobot.arm.setArmPos(Arm.armPosition.in); //go from standby to in
        grab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }
        Agobot.grabber.grab(true);


        startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.setArmPos(Arm.armPosition.standby);// from in to standby

        while (Agobot.drivetrain.goToPosition(new Vector2D(38, goToPos.getComponent(1)), 180, 1, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        //Deliver
        while (Agobot.drivetrain.goToPosition(new Vector2D(38, 39), 270, 1, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        Agobot.arm.setArmPos(Arm.armPosition.drop);
        double startDeliver = Agobot.clock.milliseconds();
        while (startDeliver + 1000 > Agobot.clock.milliseconds() && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.slides.slides(400);
        }

        Agobot.grabber.grab(false);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startGrab + 300 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.setArmPos(Arm.armPosition.standby);
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
