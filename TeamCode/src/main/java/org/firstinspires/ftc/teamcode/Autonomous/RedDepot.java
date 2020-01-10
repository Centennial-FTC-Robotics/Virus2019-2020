package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

import java.io.File;
import java.util.Arrays;

@Autonomous(name = "Red Depot", group = "Auto")
public class RedDepot extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private String skyStoneLocation = "Middle";
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");

    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.initializeWithVision(this);

        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        while(!isStarted()) {
            skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
            telemetry.addData("Sky stone position", skyStoneLocation);
            telemetry.update();
        }

        waitForStart();

        // deploy intake as the very first action
        Agobot.grabber.grab(false);
        Agobot.arm.armFlipOut(true); //go from in to standby
        Agobot.intake.deployIntake();

        //go towards depot
        while(Agobot.drivetrain.goToPosition(new Vector2D(63, -60), startHeading, 0.6)){

        }

        //get ready to pick up skystone

        int yOffset = -40;

        if (skyStoneLocation.equals("Right")) {

            yOffset -= 4;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset -= 12;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset -= 20;
        }

        while(Agobot.drivetrain.goToPosition(new Vector2D(54, yOffset), startHeading, 0.6)){

        }

        //TODO: grab skystone
        Agobot.intake.runIntake(1);

        while(Agobot.drivetrain.goToPosition(new Vector2D(24, yOffset), 225, 0.6)){

        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startIntake + 1000)) {};

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 100)) {};
        Agobot.grabber.grab(true);

        telemetry.addData("Arm State", Agobot.arm.armPositions[Agobot.arm.armPosition]);
        telemetry.update();

        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 300)) {};

        Agobot.arm.armFlipOut(true);

        // move back out of the way
        while(Agobot.drivetrain.goToPosition(new Vector2D(48, yOffset), 0, 0.6)){

        }

        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(39, 40), 0, 0.6)){

        }

        //get closer to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 38), 0, 0.6)){
            //TODO: place skystone, get closer

            Agobot.arm.armFlipOut(true);
        }

        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 32), 0, 0.6)){
            Agobot.dragger.drag(true);
        }

        //drag and rotate foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 40), 270, 0.6)){

        }

        //push against wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 44.5), 270, 0.6)){
            //TODO: retract foundation grabber
            Agobot.dragger.drag(false);
        }

        //park on red tape, closer to left side
        while(Agobot.drivetrain.goToPosition(new Vector2D(39, 0), 270, 0.6)){

        }

        //Red,39,0,270
        ReadWriteFile.writeFile(opModeData, "Red," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());

    }
}
