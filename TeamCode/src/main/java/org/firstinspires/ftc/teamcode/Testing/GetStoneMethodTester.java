package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

//@TeleOp(group = "Autonomous", name = "Get Stone Method Tester")
public class GetStoneMethodTester extends LinearOpMode {

    public static enum modes {FLAT, ANGLE};
    private Vector2D startPosition = new Vector2D(63, -36); //against wall to the right
    private double startHeading = 270; //straight left

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        int stoneNum = 1;
        int modePointer = 0;

        while(opModeIsActive()) {

            if(gamepad1.dpad_right){
                modePointer++;
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_right );
            }
            if(gamepad1.dpad_left){
                modePointer--;
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_left);
            }
            if(gamepad1.dpad_up){
                stoneNum++;
                stoneNum = Range.clip(stoneNum, 1, 6);
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_up );
            }
            if(gamepad1.dpad_down){
                stoneNum--;
                stoneNum = Range.clip(stoneNum, 1, 6);
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_down);
            }

            GetStoneMethodTester.modes mode = GetStoneMethodTester.modes.values()[Math.abs(modePointer) % GetStoneMethodTester.modes.values().length];

            if(gamepad2.a){
                if(mode.name().equals(modes.FLAT)){

                }
            }

            telemetry.addData("Stone", stoneNum);
            telemetry.addData("Mode", mode.name());

            telemetry.update();
        }
    }

    //gets stone
    public void getStone(int stoneNum){
        int xCoord = 36;
        switch(stoneNum){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                //6th stone
                break;
        }
    }

    //gets stone at 45 degree angle
    public void getStoneAngle(int stoneNum){
        int xCoord = 36;
        int yCoord = 0;
        int angle = 225;
        switch(stoneNum){
            case 1:
                yCoord = 56;
                break;
            case 2:
                yCoord = 48;
                break;
            case 3:
                yCoord = 40;
                break;
            case 4:
                yCoord = 32;
                break;
            case 5:
                yCoord = 24;
                break;
            default:
                //6th stone
                yCoord = 16;
                break;
        }

        // deploy intake as the very first action
        Agobot.grabber.grab(false);
        Agobot.arm.armFlipOut(true); //go from in to standby
        Agobot.intake.deployIntake();

        //go at angle to collect stone
        while(Agobot.drivetrain.goToPosition(new Vector2D(xCoord, yCoord), angle, 0.6) && opModeIsActive()){

        }

        Agobot.intake.runIntake(1);
        Agobot.intake.getLeft().setPower(1);
        Agobot.intake.getRight().setPower(1);

        //go collect stone
        while(Agobot.drivetrain.goToPosition(new Vector2D(24, yCoord), angle, 0.6) && opModeIsActive()){

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
    }
}
