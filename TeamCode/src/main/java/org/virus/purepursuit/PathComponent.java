package org.virus.purepursuit;

import org.virus.util.Vector2D;

import java.util.Set;

public abstract class PathComponent {
    Waypoint startPoint;
    Waypoint endPoint;
    double index;
    public abstract Set<Waypoint> findIntersections(Vector2D robotPosition, double lookaheadRadius);
}
