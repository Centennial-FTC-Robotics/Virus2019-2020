package org.virus.agobot;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.openftc.revextensions2.ExpansionHubEx;
import org.virus.superclasses.Robot;
import org.virus.superclasses.Subsystem;

import java.util.List;

public class Agobot extends Robot {

    // Robot Components
    public static MecanumVectorDriveTrain drivetrain = new MecanumVectorDriveTrain();
    public static ElementLocator tracker = new ElementLocator();
    public static Slides slides = new Slides();
    public static Intake intake = new Intake();
    public static Grabber grabber = new Grabber();
    public static Arm arm = new Arm();
    public static FoundationDragger dragger = new FoundationDragger();

    public static ElapsedTime clock = new ElapsedTime();
    static Subsystem[] subsystems = {drivetrain, slides, intake, grabber, arm, dragger,tracker};
    // robot variables
    public static double autoStarted = 0;

    public static void initialize(LinearOpMode opMode){
        revHubs = opMode.hardwareMap.getAll(LynxModule.class);
        for (LynxModule module : revHubs) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        for(int i=0; i < subsystems.length-1; i++){
            subsystems[i].initialize(opMode);
        }
    }

    public static void initializeWithVision(LinearOpMode opMode){
        initialize(opMode);
        tracker.initialize(opMode);
    }

    public static void autoStart() {

        autoStarted = clock.milliseconds();
    }

    public static int getCurrentMotorPos(DcMotor motor) {

        try {

            return motor.getCurrentPosition();
        } catch(NullPointerException e) {

            for (LynxModule module : revHubs) {
                module.setBulkCachingMode(LynxModule.BulkCachingMode.OFF);
            }

            int motorPos = motor.getCurrentPosition();

            for (LynxModule module : revHubs) {
                module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
            }

            return motorPos;
        }
    }


}
