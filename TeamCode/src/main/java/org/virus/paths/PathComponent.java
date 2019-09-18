package org.virus.paths;

public abstract class PathComponent {

    public abstract float getDistance();
    public abstract float getDegrees();
    public abstract float getHeading(float distance);
}
