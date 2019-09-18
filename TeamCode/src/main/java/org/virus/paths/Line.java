package org.virus.paths;

import org.virus.util.OdometryMath;

public class Line extends PathComponent {
    public float distance;
    public Line(float distance){
        this.distance = OdometryMath.inchToEncoder(distance);
    }
    public Line(float distance, int units){
        if(units== Path.ENCODER){
            this.distance = distance;
        }
        else if(units== Path.INCH){
            this.distance = OdometryMath.inchToEncoder(distance);
        }
    }

    @Override
    public float getDistance() {
        return distance;
    }

    @Override
    public float getDegrees() {
        return 0;
    }

    @Override
    public float getHeading(float distance) {
        return 0;
    }
}
