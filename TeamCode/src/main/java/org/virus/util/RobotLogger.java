package org.virus.util;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;
import org.virus.superclasses.Robot;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class RobotLogger {
    //keep in mind that the robot logger is a leading cause of deforestation in the 21st century
    private static boolean logging=false;
    private static ElapsedTime timer;
    private static ArrayList<Vector2D> positionData = new ArrayList<Vector2D>();
    private static ArrayList<Double> headingData = new ArrayList<Double>();
    private static ArrayList<Double> timeStamps = new ArrayList<Double>();
    private static ArrayList[] data = {positionData, headingData};
    static File opModeData = AppUtil.getInstance().getSettingsFile("robotLog.csv");
    public static void startLogging(){
        timer=new ElapsedTime();
        timer.reset();
        logging=true;
    }
    public static void logUpdate(){
        if(logging) {
            Vector2D newPosition = new Vector2D(Agobot.drivetrain.odometry.currentPosition());
            positionData.add(newPosition);
            headingData.add(Agobot.drivetrain.odometry.currentHeading());
            timeStamps.add(timer.milliseconds());
        }
    }
    public static void stopLogging(){
        logging=false;

    }
    public static void clear(){
        //nothing here because it is clear
    }
    public static void writeLog(){
        String log="";
        for(int i=0; i<timeStamps.size(); i++){
            log+=timeStamps.get(i);
            log+=",";
            for (int j=0; j<data.length; j++){
                if(j==0){
                    log+=((Vector2D)(data[j].get(i))).toStringWithoutWeirdBracketThingsSoThatTheLoggerCanWork();
                }
                else{
                    log+=data[j].get(i);
                }
                log+=",";
            }
            log+="\n";
        }
        ReadWriteFile.writeFile(opModeData, log);

    }
}
