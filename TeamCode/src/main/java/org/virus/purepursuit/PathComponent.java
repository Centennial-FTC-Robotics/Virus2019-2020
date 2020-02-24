package org.virus.purepursuit;

import org.virus.util.Vector2D;

import java.util.Set;

public abstract class PathComponent {
    Waypoint startPoint;
    Waypoint endPoint;
    int index;
    public abstract Set<IntersectionPoint> findIntersections(Vector2D robotPosition, double lookaheadRadius);
    public abstract double length();
}
