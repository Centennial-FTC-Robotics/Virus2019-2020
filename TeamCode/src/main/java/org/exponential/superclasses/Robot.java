package org.exponential.superclasses;

import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.RevBulkData;

public abstract class Robot {
    static RevBulkData hub1Data;
    static RevBulkData hub2Data;
    static ExpansionHubEx hub1;
    static ExpansionHubEx hub2;
    public static void readHub1Data(){
        hub1Data = hub1.getBulkInputData();

    }
    public static void readHub2Data(){
        hub2Data = hub2.getBulkInputData();

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
    }



}
