package org.virus.purepursuit;

import org.virus.util.Vector2D;

import java.util.ArrayList;
import java.util.Set;

public class PurePursuitPath {
    ArrayList<PathComponent> components = new ArrayList<>();
    public ArrayList<Waypoint> waypoints = new ArrayList<>();
    int currentIndex = 0;

    public PurePursuitPath(ArrayList<Waypoint> waypoints){
        for(int i = 0; i < waypoints.size() - 1; i++){
            components.add(new Line(new ArrayList<Waypoint>(waypoints.subList(i, i+2)), i));
        }
        this.waypoints = waypoints;
    }

    public ArrayList<IntersectionPoint> findIntersections(Vector2D robotPosition, double lookaheadRadius){
        ArrayList<IntersectionPoint> intersections = new ArrayList<>();
        for(PathComponent component: components){
            Set<IntersectionPoint> indivInter = component.findIntersections(robotPosition, lookaheadRadius);
            intersections.addAll(indivInter);
        }
        return intersections;
    }

    public IntersectionPoint findApproachPoint(Vector2D robotPosition, double lookaheadRadius){
        ArrayList<IntersectionPoint> intersections = findIntersections(robotPosition, lookaheadRadius);
        IntersectionPoint approachPoint;
        if(intersections.size() > 0){
            approachPoint = intersections.get(intersections.size() - 1);
        }else{
            approachPoint = closestWaypoint(robotPosition);
        }
        currentIndex = approachPoint.index();
        return approachPoint;
    }

    public double distanceAlongPath(Vector2D robotPosition, IntersectionPoint approachPoint){
        int approachPointIndex = approachPoint.index();
        Point position = new Point(robotPosition);
        double distance = position.getDistance(waypoints.get(approachPointIndex + 1)); //distance from current position to next waypoint
        for(int i = approachPointIndex + 1; i < waypoints.size() - 1; i++){
            distance += components.get(i).length(); //adding the lengths of all subsequent
        }
        return distance;
    }

    public IntersectionPoint closestWaypoint(Vector2D robotPosition){
        IntersectionPoint closestWaypoint = new IntersectionPoint(waypoints.get(0), 0);
        double closestWaypointDist = waypoints.get(0).getDistance(robotPosition);
        for(int i = 1; i < waypoints.size(); i++){
            if(waypoints.get(i).getDistance(robotPosition) < closestWaypointDist){
                closestWaypointDist = waypoints.get(i).getDistance(robotPosition);
                closestWaypoint = new IntersectionPoint(waypoints.get(i), i);
            }
        }
        return closestWaypoint;
    }

}
