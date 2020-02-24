package org.virus.purepursuit;

public class IntersectionPoint extends Waypoint{
    int index = 0;

    public IntersectionPoint(double x, double y, double heading, int componentIndex) {
        super(x, y, heading);
        index = componentIndex;
    }

    public IntersectionPoint(Waypoint waypoint, int componentIndex){
        super(waypoint.getX(), waypoint.getY(), waypoint.getHeading());
        index = componentIndex;
    }


    public int index(){
        return index;
    }

}
