package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.vision.StripDetector;

@TeleOp
public class OCVTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        StripDetector detector = new StripDetector();
        detector.initialize(this);
        waitForStart();
        while (true);
    }
}
