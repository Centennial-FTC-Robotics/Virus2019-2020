package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.virus.agobot.Agobot;
import org.virus.util.Vector2D;

@TeleOp(group = "Autonomous", name = "Get Stone Method Tester")
public class GetStoneMethodTester extends LinearOpMode {

    public static enum modes {FLAT, ANGLE};

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
}
