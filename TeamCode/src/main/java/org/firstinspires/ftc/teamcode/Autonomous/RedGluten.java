package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.Arm;
import org.virus.purepursuit.PurePursuitPath;
import org.virus.purepursuit.Waypoint;
import org.virus.util.Vector2D;

import java.io.File;
import java.util.ArrayList;

@Autonomous
public class RedGluten extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private String skyStoneLocation = "Middle";
    private int resetTime = 29500;
    private Vector2D stone1Offset = new Vector2D(0, 10);
    private Vector2D stoneOffset = new Vector2D(0, 2);
    double stone1Angle = 45; //this is relative to straight on so 0 = 180 degree global heading
    double stoneAngle = 0;
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");
    private Vector2D[] skystonePositions = new Vector2D[6];
    private Vector2D[] relativeSkystonePos = new Vector2D[6];

    @Override
    public void runOpMode() throws InterruptedException {
        //initialize the fun static variables!
        Agobot.alliance = "red";
        Agobot.initializeWithVision(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);

        //generate skystone positions
        double x = 22;
        double y = -68;

        for (int p = 0; p < 6; p++) {
            skystonePositions[p] = new Vector2D(x, y + (p * 8));
            relativeSkystonePos[p] = Vector2D.sub(new Vector2D(skystonePositions[p]), startPosition);
        }

        //SCAN STONE-------------------------------------------------------------------------------------------------------------------
        while (!isStarted()) {
            skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
            telemetry.addData("Sky stone position", skyStoneLocation);
            telemetry.update();
        }

        waitForStart();

        Agobot.autoStart();

        // deploy intake as the very first action
        Agobot.dragger.drag(0.5);
        Agobot.grabber.grab(false);
        Agobot.arm.setArmPos(Arm.armPosition.standby); //go from in to standby
        Agobot.intake.deployIntake();

        Vector2D stoneApproachPos = Vector2D.add(skystonePositions[1], stoneOffset);
        double approachAngle = stoneAngle + 180;

        if (skyStoneLocation.equals("Right")) {
            stoneApproachPos = Vector2D.add(skystonePositions[2], stoneOffset);
        } else if (skyStoneLocation.equals("Left")) {
            stoneApproachPos = Vector2D.add(skystonePositions[0], stone1Offset);
            approachAngle = stone1Angle + 180;
        }

        ArrayList<Waypoint> stone1Waypoints = new ArrayList<>();
        stone1Waypoints.add(new Waypoint(63, -36, 270));
        stone1Waypoints.add(new Waypoint(Vector2D.add(stoneApproachPos, new Vector2D(12,0)), approachAngle));
        stone1Waypoints.add(new Waypoint(Vector2D.add(stoneApproachPos, new Vector2D(-12,0)), approachAngle));
        PurePursuitPath stone1 = new PurePursuitPath(stone1Waypoints);

        //run diagonal at stones
        double timeCondition = Agobot.clock.milliseconds();
        while (opModeIsActive() && Agobot.drivetrain.followPath(stone1, 0.4)  && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
            telemetry.addData("Approach Angle", approachAngle);
            telemetry.update();

            if (timeCondition + 300 > Agobot.clock.milliseconds()) {
                Agobot.intake.runIntake(0.8);
                Agobot.intake.getLeft().setPower(0.8);
                Agobot.intake.getRight().setPower(0.8);
            }
        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while (Agobot.clock.milliseconds() < (startIntake + 800) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) {
        }

        Agobot.arm.setArmPos(Arm.armPosition.in); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while (Agobot.clock.milliseconds() < (grab + 500) && opModeIsActive()) {
        }

        Agobot.grabber.grab(true);
        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be grabbed
        while (Agobot.clock.milliseconds() < (startGrab + 400) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + resetTime))) { }
        Agobot.intake.runIntake(0);

        Agobot.arm.setArmPos(Arm.armPosition.standby); //go from in to standby
    }
}
