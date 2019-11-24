package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.virus.agobot.Agobot;

@TeleOp
public class StallPower extends LinearOpMode {
    double speed = 0;
    double stallMin;
    boolean active;
    @Override
    public void runOpMode() throws InterruptedException {
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        waitForStart();
        Agobot.drivetrain.updatePosition();
        active = true;
        while(opModeIsActive()){
            if (active) {
                Agobot.drivetrain.runMotors(speed,speed,speed,speed,0);
                speed += 0.01;
                try {
                    Thread.sleep(500);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            if (Agobot.drivetrain.odometry.getRobotCentricDelta().getComponent(1) > 0.1) {
                Agobot.drivetrain.runMotors(0,0,0,0,0);
                active = false;
            }
            telemetry.addData("Speed",speed);
            telemetry.update();
            Agobot.drivetrain.updatePosition();
        }
    }
}
