package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.virus.example.ExampleBot;

@TeleOp(group = "TeleOP", name = "ExampleTeleOp")
public class ExampleTeleOp extends LinearOpMode {

    double maxPower=1;
    double lefty;
    double leftx;
    double righty;
    double rightx;
    double rtrigger;
    double ltrigger;
    double var1;
    double var2;

    @Override
    public void runOpMode() throws InterruptedException {

        ExampleBot.initialize(this);
        ExampleBot.drivetrain.setAllRunUsingEncoders();
        //inits all hardware
        ExampleBot.drivetrain.initializeIMU();
        waitForStart();

        while(opModeIsActive()) {
            updateControllerValues();
            if (leftx!=0 || lefty!=0){
                ExampleBot.drivetrain.runMotors(var1,var2,var2,var1,rightx); //var1 and 2 are computed values found in theUpdateControllerValues method
            } else {
                ExampleBot.drivetrain.runMotors(0,0,0,0,rightx);
            }

            telemetry.addData("GamePad 1:", (gamepad1 == null));
        }
    }

    public void updateControllerValues(){
        lefty = -gamepad1.left_stick_y;
        leftx = gamepad1.left_stick_x;
        righty = -gamepad1.right_stick_y;
        rightx = gamepad1.right_stick_x;
        rtrigger = -gamepad1.right_trigger;
        ltrigger = -gamepad1.left_trigger;
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(lefty*lefty+leftx*leftx);
        var1= (lefty-leftx)*magnitude/scalar;
        var2= (lefty+leftx)*magnitude/scalar;
    }
}
