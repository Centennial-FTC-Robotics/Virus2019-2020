package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

import java.io.File;
import java.util.Arrays;

@Autonomous(name = "Blue Depot", group = "Auto")
public class BlueDepot extends LinearOpMode {
    //TODO

    private Vector2D startPosition = new Vector2D(-63, -36); //against wall to the right
    private double startHeading = 90; //straight left
    private Vector2D skyStoneLocation;
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();

        //go towards depot
        while(Agobot.drivetrain.goToPosition(new Vector2D(-63, -60), startHeading, 0.6)){

        }

        //get ready to pick up skystone
        while(Agobot.drivetrain.goToPosition(new Vector2D(54, -60), startHeading, 0.6)){

        }

        //TODO: scan stones
        ElapsedTime t = new ElapsedTime();
        while (opModeIsActive() && t.seconds() < 3) {}
        telemetry.addData("Sky stone positions", Arrays.toString(Agobot.tracker.relativeSkyStonePos()));
        telemetry.update();
        t.reset();
        while (opModeIsActive() && t.seconds() < 3) {}
        Agobot.tracker.deactivate();

        while(Agobot.drivetrain.goToPosition(new Vector2D(-48, -60), 315, 0.6)){

        }

        //TODO: grab skystone


        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-39, 40), 180, 0.6)){

        }

        //get closer to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-36, 40), 180, 0.6)){
            //TODO: place skystone, get closer
            //TODO: bring dragger down
        }

        //drag and rotate foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(-40, 40), 270, 0.6)){

        }

        //push against wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(-40, 44.5), 270, 0.6)){
            //TODO: retract foundation grabber
        }

        //park on blue tape, closer to left side
        while(Agobot.drivetrain.goToPosition(new Vector2D(-39, 0), 270, 0.6)){

        }

        ReadWriteFile.writeFile(opModeData, "Blue," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }
}
