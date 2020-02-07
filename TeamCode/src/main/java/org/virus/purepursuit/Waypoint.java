package org.virus.purepursuit;

public class Waypoint extends Point{
    private double heading;

    public Waypoint(double x, double y, double heading){
        super(x, y);
        this.heading = heading;
    }

    public double getHeading(){
        return heading;
    }

}