package org.virus.purepursuit;

import com.qualcomm.robotcore.util.Range;

import org.virus.util.Vector2D;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Line extends PathComponent{

    double xSlope;
    double ySlope;
    double headingSlope;

    public Line(ArrayList<Waypoint> waypoints){
        startPoint = waypoints.get(0);
        endPoint = waypoints.get(1);
        generateSlopes(waypoints);
    }

    public void generateSlopes(ArrayList<Waypoint> waypoints){
        xSlope = waypoints.get(1).getX() - waypoints.get(0).getX();
        ySlope = waypoints.get(1).getY() - waypoints.get(0).getY();
        headingSlope = angleDifference(waypoints.get(1).getHeading(), waypoints.get(0).getHeading());
    }

    public double angleDifference(double target, double current){
        double difference = normalizeAngle(target) - normalizeAngle(current);
        if (difference > 180) {
            difference -= 360.0;
        }
        if (difference <= -180) {
            difference += 360.0;
        }
        return difference;
    }

    public double normalizeAngle(double degrees) {

        while (degrees < 0) {

            degrees += 360;
        }

        while (degrees > 360) {

            degrees -= 360;
        }

        return degrees;
    }

    public Set<Waypoint> findIntersections(Vector2D robotPosition, double lookaheadRadius){
        Set<Waypoint> intersections = new LinkedHashSet<>(); //this type of set takes out duplicates
        //Find the terms of the quadratic equation
        double a = Math.pow(xSlope, 2) + Math.pow(ySlope, 2);
        double b = 2*startPoint.getX()*xSlope - 2*xSlope*robotPosition.getComponent(0) + 2*startPoint.getY()*ySlope - 2*ySlope*robotPosition.getComponent(1);
        double c = Math.pow(startPoint.getX(), 2) - 2*startPoint.getX()*robotPosition.getComponent(0) + Math.pow(robotPosition.getComponent(0), 2) + Math.pow(startPoint.getY(), 2) - 2*startPoint.getY()*robotPosition.getComponent(1) + Math.pow(robotPosition.getComponent(1), 2) - Math.pow(lookaheadRadius, 2);

        double discriminant = Math.pow(b, 2) - 4*a*c;
        if(discriminant >= 0){
            double t1 = Range.clip((-b - Math.sqrt(discriminant))/(2*a), 0, 1);
            double t2 = Range.clip((-b + Math.sqrt(discriminant))/(2*a), 0, 1);
            intersections.add(new Waypoint(startPoint.getX() + xSlope*t1, startPoint.getY() + ySlope*t1, normalizeAngle(startPoint.getHeading() + headingSlope*t1)));
            intersections.add(new Waypoint(startPoint.getX() + xSlope*t2, startPoint.getY() + ySlope*t2, normalizeAngle(startPoint.getHeading() + headingSlope*t2)));
        }
        return intersections;
    }
}
