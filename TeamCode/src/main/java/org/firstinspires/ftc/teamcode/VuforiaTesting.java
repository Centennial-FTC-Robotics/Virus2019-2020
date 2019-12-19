package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.virus.agobot.Agobot;
import org.virus.agobot.ElementLocator;

@Autonomous(group = "Autonomous", name = "VuforiaTest")
public class VuforiaTesting extends LinearOpMode {

    ElementLocator tracker = new ElementLocator();

    public void runOpMode() throws InterruptedException {
        tracker.initialize(this);

        waitForStart();
        while (opModeIsActive()) {

            telemetry.addData("Robot Pos: ", tracker.getRobotPos());
            //telemetry.addData("Is SkyStone Visible", tracker.isSkyStoneVisible());
            telemetry.update();
        }
    }
}
