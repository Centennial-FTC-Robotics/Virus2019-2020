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
    double radiusSlope;


    public Line(ArrayList<Waypoint> waypoints, int index){
        startPoint = waypoints.get(0);
        endPoint = waypoints.get(1);
        generateSlopes(waypoints);
        this.index = index;
    }

    public double index(){
        return index;
    }

    public void generateSlopes(ArrayList<Waypoint> waypoints){
        xSlope = waypoints.get(1).getX() - waypoints.get(0).getX();
        ySlope = waypoints.get(1).getY() - waypoints.get(0).getY();
        headingSlope = angleDifference(waypoints.get(1).getHeading(), waypoints.get(0).getHeading());
        radiusSlope = waypoints.get(1).getLookaheadRadius() - waypoints.get(0).getLookaheadRadius();
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

    public Set<IntersectionPoint> findIntersections(Vector2D robotPosition, double lookaheadRadius){
        Set<IntersectionPoint> intersections = new LinkedHashSet<>(); //this type of set takes out duplicates
        //Find the terms of the quadratic equation
        double a = Math.pow(xSlope, 2) + Math.pow(ySlope, 2);
        double b = 2*startPoint.getX()*xSlope - 2*xSlope*robotPosition.getComponent(0) + 2*startPoint.getY()*ySlope - 2*ySlope*robotPosition.getComponent(1);
        double c = Math.pow(startPoint.getX(), 2) - 2*startPoint.getX()*robotPosition.getComponent(0) + Math.pow(robotPosition.getComponent(0), 2) + Math.pow(startPoint.getY(), 2) - 2*startPoint.getY()*robotPosition.getComponent(1) + Math.pow(robotPosition.getComponent(1), 2) - Math.pow(lookaheadRadius, 2);

        double discriminant = Math.pow(b, 2) - 4*a*c;
        if(discriminant >= 0){
            Set<Double> tValues = new LinkedHashSet<>();
            tValues.add((-b - Math.sqrt(discriminant))/(2*a));
            tValues.add((-b + Math.sqrt(discriminant))/(2*a));
            for(double t: tValues) {
                intersections.add(new IntersectionPoint(startPoint.getX() + xSlope*Range.clip(t, 0, 1), startPoint.getY() + ySlope*Range.clip(t, 0, 1), normalizeAngle(startPoint.getHeading() + headingSlope*Range.clip(t, 0, 1)), index));
            }
        }
        return intersections;
    }

    public double length(){
        return startPoint.getDistance(endPoint);
    }

}
