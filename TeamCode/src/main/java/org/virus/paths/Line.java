package org.virus.paths;


import org.virus.agobot.Agobot;

public class Line extends PathComponent {
    public float distance;
    public Line(float distance){
        this.distance = Agobot.drivetrain.odometry.inchToEncoder(distance);
    }
    public Line(float distance, int units){
        if(units== Path.ENCODER){
            this.distance = distance;
        }
        else if(units== Path.INCH){
            this.distance = Agobot.drivetrain.odometry.inchToEncoder(distance);
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
