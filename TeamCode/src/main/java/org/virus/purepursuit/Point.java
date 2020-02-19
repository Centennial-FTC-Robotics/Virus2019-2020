package org.virus.purepursuit;

import org.virus.util.Vector2D;

public class Point {
    double x;
    double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getDistance(Point point2){
        return Math.sqrt(Math.pow(x - point2.getX(), 2) + Math.pow(y - point2.getY(), 2));
    }

    public double getDistance(Vector2D point2){
        return Math.sqrt(Math.pow(x - point2.getComponent(0), 2) + Math.pow(y - point2.getComponent(1), 2));
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public Vector2D toVector(){
        return new Vector2D(x, y);
    }

    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if (!(obj instanceof Waypoint)) {
            return false;
        }

        Waypoint other = (Waypoint)obj;
        return (getX() == other.getX() && getY() == other.getY());
    }

    public int hashCode() {
        return toString().hashCode();
    }

}