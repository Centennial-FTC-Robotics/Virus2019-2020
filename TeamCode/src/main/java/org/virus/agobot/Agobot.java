package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.openftc.revextensions2.ExpansionHubEx;
import org.virus.superclasses.Robot;
import org.virus.superclasses.Subsystem;

public class Agobot extends Robot {
    public static MecanumVectorDriveTrain drivetrain = new MecanumVectorDriveTrain();
    public static ElementLocator tracker = new ElementLocator();
    public static Slides slides = new Slides();
    public static Intake intake = new Intake();
    public static Grabber grabber = new Grabber();
    public static Arm  arm = new Arm();

    public static ElapsedTime clock = new ElapsedTime();
    static Subsystem[] subsystems = {drivetrain, tracker, slides, intake, grabber, arm};

    public static void initialize(LinearOpMode opMode){
        setHub1(opMode.hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 1"));
        //setHub2(opMode.hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2"));
        updateHub1Data();
        //readHub2Data();
        for(int i=0; i<subsystems.length; i++){
            subsystems[i].initialize(opMode);
        }
    }
}
