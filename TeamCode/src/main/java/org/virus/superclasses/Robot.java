package org.virus.superclasses;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.RevBulkData;

import java.lang.reflect.Constructor;
import java.util.List;

public abstract class Robot {
    public static List<LynxModule> revHubs;


    /*
    static RevBulkData hub1Data;
    static RevBulkData hub2Data;
    static ExpansionHubEx hub1;
    static ExpansionHubEx hub2;

    public static void updateHub1Data(){
        RevBulkData tempData = hub1.getBulkInputData() ;

        if (tempData != null){
            hub1Data = tempData;
        } else {

            Constructor robot = RevBulkData.class.getConstructors()[0];
            robot.setAccessible(true);

        }
    }
    public static void updateHub2Data(){
        RevBulkData tempData = hub2.getBulkInputData() ;
        if (tempData!=null){
            hub2Data = tempData;
        }
    }

    public static RevBulkData getHub1Data(){
        return hub1Data;
    }
    public static RevBulkData getHub2Data(){
        return hub2Data;
    }
    public static void setHub1(ExpansionHubEx hub1){
        Robot.hub1 = hub1;
    }
    public static void setHub2(ExpansionHubEx hub2){
        Robot.hub2 = hub2;
    }
    public static ExpansionHubEx getHub1(){
        return hub1;
    }
    public static ExpansionHubEx getHub2(){
        return hub2;
    }*/
}
