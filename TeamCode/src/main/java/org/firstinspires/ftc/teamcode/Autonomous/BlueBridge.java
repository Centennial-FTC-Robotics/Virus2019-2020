package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.PIDControllers;
import org.virus.util.Vector2D;

import java.io.File;

@Autonomous(name = "Blue Bridge", group = "Auto")
public class BlueBridge extends LinearOpMode {
    private Vector2D startPosition = new Vector2D(-63, -12); //against wall to the right
    private double startHeading = 90; //straight left
    private String skyStoneLocation;
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");

    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        waitForStart();
        Agobot.autoStart();

        while(Agobot.drivetrain.goToPosition(new Vector2D(-63, -52), startHeading, 0.6) && opModeIsActive()) {

        }

        double startVision = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (startVision + 3000) && opModeIsActive()) {} // make the robot wait for one second before reading the stones to ensure stable position
        skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
        telemetry.addData("Sky stone position", skyStoneLocation);
        telemetry.update();

        int yOffset = -32;

        if (skyStoneLocation.equals("Right")) {

            yOffset -= 20;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset -= 13;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset -= 4;
        }

//        while(Agobot.drivetrain.goToPosition(new Vector2D(54, yOffset), startHeading, 0.6)){
//
//        }

        // grab skystone
        Agobot.intake.runIntake(1);
        Agobot.intake.runIntake(1);

        while(Agobot.drivetrain.goToPosition(new Vector2D(-50, yOffset), 270, 0.6) && opModeIsActive()) {

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        while(Agobot.drivetrain.goToPosition(new Vector2D(-24, yOffset), 270, 0.6) && opModeIsActive()) {

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }
        while(Agobot.drivetrain.goToPosition(new Vector2D(-24, yOffset - 8), 270, 0.6) && opModeIsActive()) {

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startIntake + 1000) && opModeIsActive()) {}

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 100) && opModeIsActive()) {}
        Agobot.grabber.grab(true);

//        telemetry.addData("Arm State", Agobot.arm.armPositions[Agobot.arm.armPosition]);
//        telemetry.update();

        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 300) && opModeIsActive()) {}

        Agobot.arm.armFlipOut(true);

        // move back out of the way
        while(Agobot.drivetrain.goToPosition(new Vector2D(-39, yOffset), 180, 0.6,1.3, 3) && opModeIsActive()) {

        }

        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-39, 40), 180, 0.8, 1.5, 3) && opModeIsActive()) {

        }

        //get closer to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-30, 40), 180, 0.6, 1.5) && opModeIsActive()) {
            // place skystone
            Agobot.arm.armFlipOut(true);
        }

        Agobot.dragger.drag(true);
        startGrab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (startGrab + 700) && opModeIsActive()) {}
        PIDControllers.xController.changeConstants(.09f,.3f ,0.001f,0.3f);
        PIDControllers.yController.changeConstants(.09f,.3f ,0.001f,0.3f);

        double startDrag = Agobot.clock.milliseconds();
        //drag foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-40, 40), 180, 0.6,1.5) && opModeIsActive()) {
            if(Agobot.clock.milliseconds() > startDrag + 300){
                Agobot.grabber.grab(false);
            }
        }
        Agobot.arm.armFlipOut(false);
        PIDControllers.headingController.changeConstants(-.5f, -.15f,-.001f, .9f);

        while(Agobot.drivetrain.goToPosition(new Vector2D(-40, 30), 270, 0.8,1.5) && opModeIsActive()) {

        }

        Agobot.dragger.drag(false);
        startGrab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (startGrab + 300) && opModeIsActive()) {}

        PIDControllers.headingController.changeConstants(-.09f, -.012f,-.001f, .2f);

        //push against wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(-40, 42), 270, 0.6, 1.5) && opModeIsActive()) {

        }
        PIDControllers.headingController.changeConstants(-.065f, -.009f,-.001f, .1f);
        PIDControllers.xController.changeConstants(.07f,.16f ,0.001f,0.15f);
        PIDControllers.yController.changeConstants(.07f,.16f ,0.001f,0.15f);

        //park on red tape, closer to wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(-58, 0), 270, 0.6) && opModeIsActive()) {

        }

        ReadWriteFile.writeFile(opModeData, "Blue," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }
}
