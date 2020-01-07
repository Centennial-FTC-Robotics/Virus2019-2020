package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.virus.agobot.Agobot;
import org.virus.agobot.ElementLocator;
import org.virus.util.Vector2D;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Autonomous(name = "Red Depot", group = "Auto")
public class RedDepot extends LinearOpMode {
    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private Vector2D skyStoneLocation;

    public ArrayList<String> currentState = new ArrayList<String>();

    @Override
    public void runOpMode() throws InterruptedException {

        Agobot.initializeWithVision(this);

        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        currentState.add("Auto: Red");
        currentState.add("Pos: " + Agobot.drivetrain.odometry.currentPosition());
        currentState.add("Rot: " + Agobot.drivetrain.odometry.currentHeading());

        waitForStart();

        //go towards depot
        while(Agobot.drivetrain.goToPosition(new Vector2D(63, -60), startHeading, 0.6)){

        }

        //get ready to pick up skystone
        while(Agobot.drivetrain.goToPosition(new Vector2D(54, -60), startHeading, 0.6)){

        }

        //TODO: scan stones
        ElapsedTime t = new ElapsedTime();
        while (opModeIsActive() && t.seconds() < 1) {}
        telemetry.addData("Sky stone position", Arrays.toString(Agobot.tracker.relativeSkyStonePos()));
        telemetry.update();
        t.reset();
        //while (opModeIsActive() && t.seconds() < 3) {}
//        Agobot.tracker.deactivate();
//
//        while(Agobot.drivetrain.goToPosition(new Vector2D(48, -60), 225, 0.6)){
//
//        }
//
//        //TODO: grab skystone
//
//        //recenter position, face backwards to be ready to place skystone
//        while(Agobot.drivetrain.goToPosition(new Vector2D(39, -40), 0, 0.6)){
//
//        }
//
//        //go to foundation
//        while(Agobot.drivetrain.goToPosition(new Vector2D(39, 40), 0, 0.6)){
//
//        }
//
//        //get closer to foundation
//        while(Agobot.drivetrain.goToPosition(new Vector2D(36, 40), 0, 0.6)){
//            //TODO: place skystone
//            //TODO: bring dragger down
//        }
//
//        //drag and rotate foundation
//        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 40), 270, 0.6)){
//
//        }
//
//        //push against wall
//        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 44.5), 270, 0.6)){
//            //TODO: retract foundation grabber
//        }
//
//        //park on red tape, closer to left side
//        while(Agobot.drivetrain.goToPosition(new Vector2D(39, 0), 270, 0.6)){
//
//        }
//
//        currentState.set(1, "Pos: " + Agobot.drivetrain.odometry.currentPosition());
//        currentState.set(2, "Rot: " + Agobot.drivetrain.odometry.currentHeading());
//        update();
    }

    public void update() {

        writeLines("Autodata.txt", currentState);
    }

    public static void writeLines(String writeFile, ArrayList<String> lines) {

        BufferedWriter bw = null;

        try {

            bw = new BufferedWriter(new FileWriter(writeFile));

            for (String str: lines) {

                bw.write(str);
                bw.newLine();
            }

            bw.flush();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            try {

                bw.close();
            } catch (IOException e) {

                e.printStackTrace();
            } finally {

                bw = null;
                System.gc();
            }
        }
    }
}
