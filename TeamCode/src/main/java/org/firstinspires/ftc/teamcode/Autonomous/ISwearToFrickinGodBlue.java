package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@Autonomous(name = "ISTFG Blue", group = "Auto")
public class ISwearToFrickinGodBlue extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(-63, -12); //against wall to the right
    private double startHeading = 90; //straight left

    public void runOpMode() throws InterruptedException {

        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        Agobot.alliance = "blue";

        waitForStart();

        while(Agobot.drivetrain.goToPosition(new Vector2D(-60, 0), startHeading, 0.6, 0.5)){

        }
    }
}
