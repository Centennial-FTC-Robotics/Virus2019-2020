package org.virus.util;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class Location extends Position {
    public Location(){
        super();
    }

    public Location(DistanceUnit unit, double x, double y, double z, long acquisitionTime){
        super(unit, x, y, z, acquisitionTime);
    }
    public void updateX(double x){
        super.x = x;
    }
    public void updateY(double y){
        super.y = y;
    }
    public void updateZ(double z){
        super.z = z;
    }
    public void updateTime(long acquisitionTime){
        super.acquisitionTime = acquisitionTime;
    }
    public void updateLocation(double x, double y, double z, long acquisitionTime){
        updateX(x);
        updateY(y);
        updateZ(z);
        updateTime(acquisitionTime);
    }

}
