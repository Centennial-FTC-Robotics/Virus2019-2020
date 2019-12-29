package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MotorToTick extends LinearOpMode {

    DcMotorEx leftIntake;
    DcMotorEx rightIntake;

    @Override
    public void runOpMode() throws InterruptedException {
        leftIntake = hardwareMap.get(DcMotorEx.class, "leftIntake");
        rightIntake = hardwareMap.get(DcMotorEx.class, "rightIntake");

        rightIntake.setDirection(DcMotor.Direction.REVERSE);

        leftIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        leftIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftIntake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while(opModeIsActive()){
            leftIntake.setPower(1);
            rightIntake.setPower(1);

            telemetry.addData("Left intake velocity", leftIntake.getVelocity());
            telemetry.addData("Right intake velocity", rightIntake.getVelocity());
            telemetry.update();
        }
    }
}
