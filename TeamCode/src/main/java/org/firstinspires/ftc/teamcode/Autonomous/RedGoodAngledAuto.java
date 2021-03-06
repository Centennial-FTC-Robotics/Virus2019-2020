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

@Autonomous(group = "Autonomous", name = "Red Angled")
public class RedGoodAngledAuto extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private String skyStoneLocation = "Middle";
    private int resetTime = 29500;
    private Vector2D stone1Offset = new Vector2D(-1.5, 5);
    private Vector2D stone2Offset = new Vector2D(0, 4);
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");
    private Vector2D[] skystonePositions = new Vector2D[6];
    private Vector2D[] relativeSkystonePos = new Vector2D[6];

    public void runOpMode() {

        //initialize the fun static variables!
        Agobot.alliance = "red";
        Agobot.initializeWithVision(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        generateSkyStonePositions();

        //SCAN STONE-------------------------------------------------------------------------------------------------------------------
        while (!isStarted()) {
            skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
            telemetry.addData("Sky stone position", skyStoneLocation);
            telemetry.update();
        }

        waitForStart();

        Agobot.autoStart();

        //First actions: dragger up, grabber is release, arm to standby, deploy intake
        Agobot.dragger.drag(0.5);
        Agobot.grabber.grab(false);
        Agobot.arm.setArmPos(Arm.armPosition.standby); //go from in to standby
        Agobot.intake.deployIntake();


        Vector2D relativeMvmt = Vector2D.add(relativeSkystonePos[2], stone1Offset);
        double angle = relativeMvmt.getTheta();

        Vector2D goToPos = Vector2D.add(startPosition, new Vector2D(0, -8));

        if (skyStoneLocation.equals("Right")) {
            goToPos.add(new Vector2D(0, 8));
        } else if (skyStoneLocation.equals("Left")) {
            Vector2D finalV = new Vector2D(22, -60);
            goToPos = Vector2D.add(finalV, new Vector2D(16, 0));
            relativeMvmt = Vector2D.sub(finalV, goToPos);
        }

        relativeMvmt.sub(new Vector2D(angle, 6.0, true));

        if (!skyStoneLocation.equals("Left")) {
            ArrayList<Waypoint> stone1Waypoints = new ArrayList<>();
            stone1Waypoints.add(new Waypoint(63, -36, 270));
            stone1Waypoints.add(new Waypoint(goToPos, Math.toDegrees(angle)));
            stone1Waypoints.add(new Waypoint(Vector2D.add(Vector2D.add(goToPos, relativeMvmt), new Vector2D(-2, 0)), Math.toDegrees(angle)));
            PurePursuitPath stone1 = new PurePursuitPath(stone1Waypoints);

            //run diagonal at stones
            double timeCondition = Agobot.clock.milliseconds();
            while ((Agobot.clock.milliseconds() < timeCondition + 2500) && opModeIsActive() && Agobot.drivetrain.followPath(stone1, 0.4)  && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
                telemetry.addData("Target", stone1.approachPoint);
                telemetry.addData("Current", Agobot.drivetrain.odometry.currentPosition());
                telemetry.update();

                if (timeCondition + 300 > Agobot.clock.milliseconds()) {
                    Agobot.intake.runIntake(0.8);
                    Agobot.intake.getLeft().setPower(0.8);
                    Agobot.intake.getRight().setPower(0.8);
                }
            }
        } else {

            while (Agobot.drivetrain.goToPosition(goToPos, 217, 0.4, 1.5, 2) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

            }

            Agobot.intake.runIntake(0.8);
            Agobot.intake.getLeft().setPower(0.8);
            Agobot.intake.getRight().setPower(0.8);

            double timeCondition = Agobot.clock.milliseconds();
            while ((Agobot.clock.milliseconds() < timeCondition + 2500) && Agobot.drivetrain.goToPosition(Vector2D.add(Vector2D.add(goToPos, relativeMvmt), new Vector2D(-2, 0)), 217, 0.4, 1.5, 2) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

            }
        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startIntake + 800) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.setArmPos(Arm.armPosition.in); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive()) {
        }

        Agobot.grabber.grab(true);
        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be grabbed
        while (Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) { }
        Agobot.intake.runIntake(0);

        Agobot.arm.setArmPos(Arm.armPosition.standby); //go from in to standby

        ArrayList<Waypoint> toFoundation1Waypoints = new ArrayList<>();
        toFoundation1Waypoints.add(new Waypoint(goToPos, Math.toDegrees(angle)));
        toFoundation1Waypoints.add(new Waypoint(48, goToPos.getComponent(1), 180));
        toFoundation1Waypoints.add(new Waypoint(39, -20, 270));
        toFoundation1Waypoints.add(new Waypoint(39, 20, 270));
        toFoundation1Waypoints.add(new Waypoint(42, 45, 0));
        toFoundation1Waypoints.add(new Waypoint(31.5, 45, 0));
        PurePursuitPath toFoundation1 = new PurePursuitPath(toFoundation1Waypoints);

        double lmao = .65d;
        while(Agobot.drivetrain.followPath(toFoundation1, lmao) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            if (toFoundation1.currentIndex() == 4){ //going to (31.5, 44)
                lmao = 0.3d;
                PIDControllers.xController.changeConstants(.08f, .15f, 0.001f, 0.2f);
                PIDControllers.yController.changeConstants(.08f, .15f, 0.001f, 0.2f);
            }
        }
        Agobot.arm.setArmPos(Arm.armPosition.drop);

        //DRAG FOUNDATION CLOSE------------------------------------------------------------------------------------------------
        Agobot.dragger.drag(true);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (startGrab + 1000) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        //change x and y to pull foundation
        PIDControllers.xController.changeConstants(.07f, .3f, 0.001f, 0.3f);
        PIDControllers.yController.changeConstants(.07f, .3f, 0.001f, 0.3f);

        double startDrag = Agobot.clock.milliseconds();
        //drag foundation
        while (Agobot.drivetrain.goToPosition(new Vector2D(45, 44), 0, .8, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            if (Agobot.clock.milliseconds() > startDrag + 300) {
                Agobot.grabber.grab(false);
            }
        }

        Agobot.arm.setArmPos(Arm.armPosition.standby);

        //change heading constant for rotating foundation
        PIDControllers.headingController.changeConstants(-.5f, -.11f, -.001f, 1.4f);

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
        Agobot.intake.runIntake(0.8);
        Agobot.intake.runIntake(0.8);

        goToPos = Vector2D.add(skystonePositions[4], stone2Offset);

        if (skyStoneLocation.equals("Right")) {

            goToPos = Vector2D.add(skystonePositions[5], stone2Offset);
        } else if (skyStoneLocation.equals("Left")) {

            goToPos = Vector2D.add(skystonePositions[3], stone2Offset);
        }

        Vector2D afterDeliverOffset = new Vector2D(16, 7);
        Vector2D afterDeliverVector = Vector2D.add(goToPos, afterDeliverOffset);
        goToPos.add(new Vector2D(0, 8));

        ArrayList<Waypoint> backForStone2Waypoints = new ArrayList<>();
        backForStone2Waypoints.add(new Waypoint(41,30, 270));
        backForStone2Waypoints.add(new Waypoint(41, -10, 270));
        backForStone2Waypoints.add(new Waypoint(afterDeliverVector, 225));
        PurePursuitPath backForStone2 = new PurePursuitPath(backForStone2Waypoints);

        while(Agobot.drivetrain.followPath(backForStone2, 0.7) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))){
            if(backForStone2.currentIndex() == 0){
                Agobot.intake.runIntake(-0.8);
                Agobot.intake.getLeft().setPower(-0.8);
                Agobot.intake.getRight().setPower(-0.8);
            }else{
                Agobot.intake.runIntake(0.7);
                Agobot.intake.getLeft().setPower(0.7);
                Agobot.intake.getRight().setPower(0.7);
            }
        }
        //run head-on at stones
        double timeCondition = Agobot.clock.milliseconds();
        while ((Agobot.clock.milliseconds() < timeCondition + 2000) && Agobot.drivetrain.goToPosition(Vector2D.add(goToPos, new Vector2D(-2, 0)), 225, 0.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

            Agobot.intake.runIntake(0.7);
            Agobot.intake.getLeft().setPower(0.7);
            Agobot.intake.getRight().setPower(0.7);
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

        while (Agobot.drivetrain.goToPosition(new Vector2D(38, goToPos.getComponent(1)), 270, 1, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        int parkingX = 39;
        //Deliver
        while (Agobot.drivetrain.goToPosition(new Vector2D(parkingX, 42), 270, 0.8, 1.5) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {

        }

        Agobot.arm.setArmPos(Arm.armPosition.drop);
        double startDeliver = Agobot.clock.milliseconds();
        while (startDeliver + 1000 > Agobot.clock.milliseconds() && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            //Agobot.slides.slides(400);
        }

        Agobot.grabber.grab(false);
        startGrab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startGrab + 300 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.setArmPos(Arm.armPosition.standby);
        double startRetract = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < startRetract + 400 && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        //PARK------------------------------------------------------------------------------------------------
        while (Agobot.drivetrain.goToPosition(new Vector2D(parkingX, 0), 270, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            Agobot.slides.slides(0);
        }

        ReadWriteFile.writeFile(opModeData, "Red," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }

    public void generateSkyStonePositions(){
        //generate skystone positions
        double x = 22;
        double y = -68;

        for (int p = 0; p < 6; p++) {
            skystonePositions[p] = new Vector2D(x, y + (p * 8));
            relativeSkystonePos[p] = Vector2D.sub(new Vector2D(skystonePositions[p]), startPosition);
        }
    }
}
