package org.exponential.blueshift;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.exponential.superclasses.Robot;
import org.exponential.superclasses.Subsystem;
import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.RevExtensions2;

public class Blueshift extends Robot {

    public static TankDrivetrain drivetrain = new TankDrivetrain();
    public static ElapsedTime clock = new ElapsedTime();
    static Subsystem[] subsystems = {drivetrain};

    public static void initialize(OpMode opMode){
        RevExtensions2.init();
        setHub1(opMode.hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 1"));
        setHub2(opMode.hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2"));
        readHub1Data();
        readHub2Data();
        for(int i=0; i<subsystems.length; i++){
            subsystems[i].initialize(opMode);
        }
    }

}
