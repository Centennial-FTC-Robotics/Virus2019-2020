package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;
import org.virus.agobot.Arm;
import org.virus.util.Vector2D;

import java.util.Arrays;

//@TeleOp(group = "TeleOp", name = "Arm Tester")
public class TestArm extends LinearOpMode {

    double gp1RightTriggerPrev = 0;
    double gp2LeftTriggerPrev = 0;
    double gp2rightTriggerPrev = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(63, -36), 179);

        waitForStart();

        while(opModeIsActive()) {

            Agobot.slides.slidePower(-0.5 * gamepad2.left_stick_y + -0.2 * gamepad2.right_stick_y);

            //arm
            if(gp2LeftTriggerPrev < 0.1 && gamepad2.left_trigger > 0.1) { //only calls if trigger is pressed and on previous loop iteration it wasn't

                Agobot.arm.armFlipOut(false);
            }else if(gp2rightTriggerPrev < 0.1 && gamepad2.right_trigger > 0.1){
                Agobot.arm.armFlipOut(true);
            }

            gp1RightTriggerPrev = gamepad1.right_trigger;
            gp2LeftTriggerPrev = gamepad2.left_trigger;
            gp2rightTriggerPrev = gamepad2.right_trigger;



            telemetry.addData("Rev color", Arrays.toString(new double[] {Arm.getRed(Agobot.arm.stoneSensor.argb()), Arm.getGreen(Agobot.arm.stoneSensor.argb()), Arm.getBlue(Agobot.arm.stoneSensor.argb())}));
            telemetry.addData("Color RGB int output", Agobot.arm.stoneSensor.argb());
            telemetry.addData("Distance", Agobot.arm.getStonePosition());
            telemetry.addData("Arm Position", Agobot.arm.getArmPosition());
            telemetry.addData("Arm State Number", Agobot.arm.armPos);
            telemetry.addData("Arm State", Agobot.arm.getArmPosition());
            telemetry.addData("Slide Position", Agobot.slides.getPosition());
//            telemetry.addData("Left Arm Position", Agobot.arm.leftPosition);
//            telemetry.addData("Right Arm Position", Agobot.arm.rightPosition);
//            telemetry.addData("Left Trigger", gamepad2.left_trigger);
//            telemetry.addData("Right Trigger", gamepad2.right_trigger);
            telemetry.update();
        }
    }
}
