package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

import java.io.File;

@Autonomous(name = "Red Bridge", group = "Auto")
public class RedBridge extends LinearOpMode {
    private Vector2D startPosition = new Vector2D(63, -12); //against wall to the right
    private double startHeading = 270; //straight left
    private Vector2D skyStoneLocation;
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        waitForStart();

        //go towards skystones
        //TODO: maybe have path go hypotenuse instead of legs (talk to ere if confusion)
        while(Agobot.drivetrain.goToPosition(new Vector2D(54, -36), startHeading, 0.6)){

        }

        //TODO: scan stones

        //TODO: grab skystone


        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(39, 40), 0, 0.6)){

        }

        //get closer to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 40), 0, 0.6)){
            //TODO: place skystone, and move closer
            //TODO: bring dragger down
        }

        //drag and rotate foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 40), 270, 0.6)){

        }

        //push against wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 44.5), 270, 0.6)){
            //TODO: retract foundation grabber
        }

        //park on red tape, closer to wall
        while(Agobot.drivetrain.goToPosition(new Vector2D(60, 0), 270, 0.6)){

        }
        ReadWriteFile.writeFile(opModeData, "Red," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }
}
