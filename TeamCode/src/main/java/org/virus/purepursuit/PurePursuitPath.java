package org.virus.purepursuit;

import org.virus.util.Vector2D;

import java.util.ArrayList;
import java.util.Set;

public class PurePursuitPath {
    ArrayList<PathComponent> components = new ArrayList<>();
    public ArrayList<Waypoint> waypoints = new ArrayList<>();

    public PurePursuitPath(ArrayList<Waypoint> waypoints){
        for(int i = 0; i < waypoints.size() - 1; i++){
            components.add(new Line(new ArrayList<Waypoint>(waypoints.subList(i, i+2)), i));
        }
        this.waypoints = waypoints;
    }

    public ArrayList<Waypoint> findIntersections(Vector2D robotPosition, double lookaheadRadius){
        ArrayList<Waypoint> intersections = new ArrayList<>();
        for(PathComponent component: components){
            Set<Waypoint> indivInter = component.findIntersections(robotPosition, lookaheadRadius);
            intersections.addAll(indivInter);
        }
        return intersections;
    }

    public double distanceAlongPath(Vector2D currentPosition, Vector2D approachPoint){
        return 5.0;
    }
}
