package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.vision.StripDetector;

import java.util.Arrays;

@TeleOp(group = "TeleOp", name = "OpenCV Test")
public class OCVTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        StripDetector detector = new StripDetector();
        detector.initialize(this);
        waitForStart();

        while (opModeIsActive()) {

            if (detector.read) {

                telemetry.addData("relative skystone pos", detector.relativePos());
                telemetry.addData("Brightness Values", Arrays.toString(detector.brightnesses));
            }
            telemetry.update();
        }
    }
}
