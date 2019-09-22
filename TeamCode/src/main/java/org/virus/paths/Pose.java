package org.virus.paths;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Pose {
    private float x;
    private float y;
    private float angle;

    Pose(float x, float y, float angle){
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public float distance(Pose pose){
        return (float) Math.sqrt((this.getY()- pose.getY())*(this.getY()- pose.getY()) + (this.getX()- pose.getX())*(this.getX()- pose.getX()));
    }
    public float angle(Pose pose){
        return AngleUnit.normalizeDegrees(this.getAngle()  - pose.getAngle());
    }
}
