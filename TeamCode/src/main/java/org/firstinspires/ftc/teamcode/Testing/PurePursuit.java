package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.purepursuit.Waypoint;
import org.virus.util.Vector2D;

import java.util.ArrayList;

public class PurePursuit extends LinearOpMode {

    ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
    private Vector2D startPosition = new Vector2D(42, -57); //right after picking up middle stone
    private double startHeading = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        //add all the waypoints in (this particular set is the path from the stones to the foundation)
        waypoints.add(new Waypoint(42,-57,90));
        waypoints.add(new Waypoint(42,40,90));
        waypoints.add(new Waypoint(31,40,90));

        waitForStart();

        while(Agobot.drivetrain.followPath(waypoints)){

        }

    }

}