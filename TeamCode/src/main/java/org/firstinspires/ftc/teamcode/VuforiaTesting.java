package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.virus.agobot.Agobot;
import org.virus.agobot.ElementLocator;

@Autonomous(group = "Autonomous", name = "VuforiaTest")
public class VuforiaTesting extends LinearOpMode {

    ElementLocator tracker = new ElementLocator();

    public void runOpMode() throws InterruptedException {
        tracker.initialize(this);

        waitForStart();
        while (opModeIsActive()) {

//            telemetry.addData("Robot Pos: ", tracker.getRobotPos());
//            telemetry.addData("Is SkyStone Visible", tracker.isSkyStoneVisible());
//            telemetry.addData("sky Stone Positions", tracker.getSkyStonePositions());

            OpenGLMatrix[] transforms = tracker.getRobotLocationTransform();
            for (int i = 0; i < transforms.length; i++) {
                OpenGLMatrix locationTransform = transforms[i];

                if (locationTransform != null) {
                    float[] data = locationTransform.getData();
                    telemetry.addData("Camera X-axis", "X %.3f Y %.3f Z %.3f", data[0], data[1], data[2]);
                    telemetry.addData("Camera Y-axis", "X %.3f Y %.3f Z %.3f", data[4], data[5], data[6]);
                    telemetry.addData("Camera Z-axis", "X %.3f : %.3f Z %.3f", data[8], data[9], data[10]);
                }
            }

            telemetry.update();
        }
    }
}
