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

    public String toString() {
        return "(" + x + "," + y + ") " + heading + " degrees";
    }

    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if (!(obj instanceof Waypoint)) {
            return false;
        }

        Waypoint other = (Waypoint)obj;
        return (getX() == other.getX() && getY() == other.getY() && getHeading() == other.getHeading());
    }

    public int hashCode() {
        return toString().hashCode();
    }

}