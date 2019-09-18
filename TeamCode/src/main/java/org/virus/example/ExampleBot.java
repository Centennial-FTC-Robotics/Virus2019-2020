package org.virus.example;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.openftc.revextensions2.ExpansionHubEx;

import org.virus.superclasses.Robot;
import org.virus.superclasses.Subsystem;

public class ExampleBot extends Robot {

    public static ExampleDrivetrain drivetrain = new ExampleDrivetrain();
    public static ExampleSlides verticalSlides = new ExampleSlides();
    public static ExampleSlides horizontalSlides = new ExampleSlides();


    public static ElapsedTime clock = new ElapsedTime();
    static Subsystem[] subsystems = {drivetrain, verticalSlides, horizontalSlides};

    public static void initialize(OpMode opMode){
        setHub1(opMode.hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 1"));
        setHub2(opMode.hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2"));
        readHub1Data();
        readHub2Data();
        for(int i=0; i<subsystems.length; i++){
            subsystems[i].initialize(opMode);
        }
    }

}