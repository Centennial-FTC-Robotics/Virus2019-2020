package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.agobot.PIDControllers;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;

import java.io.File;
import java.util.Arrays;

@Autonomous(name = "Red Depot 2 Stones", group = "Auto")
public class RedDepot2Stones extends LinearOpMode {

    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left
    private String skyStoneLocation = "Middle";
    //TODO: test diagonal angle for colecting stones
    private int diagonalAngle = 225;
    File opModeData = AppUtil.getInstance().getSettingsFile("opModeData.txt");

    /*Autonomous has 6 Stages
    1) Scan
    2) Collect Skystone 1 & deliver to foundation
    3) Drag foundation close
    4) Collect Skystone 2 & deliver (+ more maybe)
    5) push foundation to build site
    6) Park
     */


    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initializeWithVision(this);

        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(startPosition, startHeading);
        //SCAN STONE-------------------------------------------------------------------------------------------------------------------
        while(!isStarted()) {
            skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
            telemetry.addData("Sky stone position", skyStoneLocation);
            telemetry.update();
        }

        waitForStart();
        Agobot.autoStart();

        // deploy intake as the very first action
        Agobot.grabber.grab(false);
        Agobot.arm.armFlipOut(true); //go from in to standby
        Agobot.intake.deployIntake();

        //move into position to read stones
        while(Agobot.drivetrain.goToPosition(new Vector2D(63, -36), startHeading, 0.6) && opModeIsActive()){

        }

        //BRUH
//        double startVision = Agobot.clock.milliseconds();
//        while (Agobot.clock.milliseconds() < (startVision + 700) && opModeIsActive()) {} // make the robot wait for one second before reading the stones to ensure stable position
//        skyStoneLocation = Agobot.tracker.relativeSkyStonePosOpenCV();
//        telemetry.addData("Sky stone position", skyStoneLocation);
//        telemetry.update();

        //COLLECT SKYSTONE #1 & DELIVER------------------------------------------------------------------------------------------------
        //get ready to pick up skystone
        //TODO: test values for y offset for diagonal getting stones
        int yOffset = -59;

        if (skyStoneLocation.equals("Right")) {

            yOffset += 20;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset += 12;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset += 4;
        }

//

        // grab skystone
        Agobot.intake.runIntake(1);
        Agobot.intake.runIntake(1);


        //go at angle to collect inner set of stones
        while(Agobot.drivetrain.goToPosition(new Vector2D(3, yOffset), diagonalAngle, 0.6) && opModeIsActive()){
            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        //run diagonal at stones
        while(Agobot.drivetrain.goToPosition(new Vector2D(24, yOffset), diagonalAngle, 0.6) && opModeIsActive()){

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        double startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startIntake + 1000) && opModeIsActive()) {}

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        double grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 100) && opModeIsActive()) {}
        Agobot.grabber.grab(true);


        double startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 300) && opModeIsActive()) {}

        Agobot.arm.armFlipOut(true);

        //Deliver skystone 1 to foundation
        // move back out of the way
        while(Agobot.drivetrain.goToPosition(new Vector2D(42, yOffset), 0, 0.6,1.3) && opModeIsActive()){

        }

        //go to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(42, 40), 0, 0.6) && opModeIsActive()){

        }

        //get closer to foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(31, 40), 0, 0.6, 1) && opModeIsActive()){
            // place skystone
            Agobot.arm.armFlipOut(true);
        }

        //DRAG FOUNDATION CLOSE------------------------------------------------------------------------------------------------
        Agobot.dragger.drag(true);
        startGrab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (startGrab + 700) && opModeIsActive()) {};
        PIDControllers.xController.changeConstants(.09f,.3f ,0.001f,0.3f);
        PIDControllers.yController.changeConstants(.09f,.3f ,0.001f,0.3f);

        double startDrag = Agobot.clock.milliseconds();
        //drag foundation
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 38), 0, 0.6,1.5) && opModeIsActive()){
            if(Agobot.clock.milliseconds() > startDrag + 300){
                Agobot.grabber.grab(false);
            }
        }
        Agobot.arm.armFlipOut(false);
        PIDControllers.headingController.changeConstants(-.5f, -.04f,-.001f, .7f);

        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 30), 270, 0.6,1.5) && opModeIsActive()){

        }

        Agobot.dragger.drag(false);
        startGrab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (startGrab + 300) && opModeIsActive()) {}

        PIDControllers.headingController.changeConstants(-.09f, -.012f,-.001f, .2f);

        //COLLECT SKYSTONE #2 & DELIVER------------------------------------------------------------------------------------------------
        // grab skystone
        Agobot.intake.runIntake(1);
        Agobot.intake.runIntake(1);


        //TODO: need to test values for second skystone
        //These values are def wrong -ere
        yOffset = -59;

        if (skyStoneLocation.equals("Right")) {

            yOffset += 20;
        } else if (skyStoneLocation.equals("Middle")) {

            yOffset += 12;
        } else if (skyStoneLocation.equals("Left")) {

            yOffset += 4;
        }

        //go at angle to collect inner set of stones
        while(Agobot.drivetrain.goToPosition(new Vector2D(3, yOffset), diagonalAngle, 0.6) && opModeIsActive()){
            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        //run diagonal at stones
        while(Agobot.drivetrain.goToPosition(new Vector2D(24, yOffset), diagonalAngle, 0.6) && opModeIsActive()){

            Agobot.intake.runIntake(1);
            Agobot.intake.getLeft().setPower(1);
            Agobot.intake.getRight().setPower(1);
        }

        startIntake = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startIntake + 1000) && opModeIsActive()) {}

        Agobot.intake.runIntake(0);
        Agobot.arm.armFlipOut(false); //go from standby to in
        grab = Agobot.clock.milliseconds();
        while(Agobot.clock.milliseconds() < (grab + 100) && opModeIsActive()) {}
        Agobot.grabber.grab(true);


        startGrab = Agobot.clock.milliseconds(); // wait a second for the block to be taken in
        while(Agobot.clock.milliseconds() < (startGrab + 300) && opModeIsActive()) {}

        Agobot.arm.armFlipOut(true);

        //Deliver
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 28), 270, 0.6,1.5) && opModeIsActive()){
            Agobot.arm.armFlipOut(true);
        }
        Agobot.grabber.grab(false);


        //PUSH FOUNDATION TO BUILD SITE------------------------------------------------------------------------------------------------
        while(Agobot.drivetrain.goToPosition(new Vector2D(40, 42), 270, 0.6) && opModeIsActive()){

        }
        PIDControllers.headingController.changeConstants(-.065f, -.009f,-.001f, .1f);
        PIDControllers.xController.changeConstants(.07f,.15f ,0.001f,0.15f);
        PIDControllers.yController.changeConstants(.07f,.15f ,0.001f,0.15f);

        //PARK------------------------------------------------------------------------------------------------
        while(Agobot.drivetrain.goToPosition(new Vector2D(39, 0), 270, 0.6) && opModeIsActive() && (Agobot.clock.milliseconds() < (Agobot.autoStarted + 29000))){

        }

        ReadWriteFile.writeFile(opModeData, "Red," + Agobot.drivetrain.odometry.currentPosition().getComponent(0) + "," + Agobot.drivetrain.odometry.currentPosition().getComponent(1) + "," + Agobot.drivetrain.odometry.currentHeading());
    }


}
