package org.exponential.paths;


public class Path {
    public static final int ENCODER=0;
    public static final int INCH=1;
    PathComponent[] pathComponents;
    TrapezoidalCurve motorCurve;
    public Path(PathComponent[] pathComponents, float accel, float deaccel, float max, float bias){
        this.pathComponents = pathComponents;
        float distance=0;
        for(int i = 0; i< pathComponents.length; i++){
            distance+= pathComponents[i].getDistance();
        }
        motorCurve = new TrapezoidalCurve(accel,  deaccel, Math.round(distance), max, bias);
    }
    public float getHeading(float distance){
        float heading=0;
        int i=0;
        for(; i<pathComponents.length && !(distance<pathComponents[i].getDistance()); i++){
            if(distance>=pathComponents[i].getDistance()){
                distance-=pathComponents[i].getDistance();
                heading+=pathComponents[i].getHeading(pathComponents[i].getDistance());
            }
        }
        if(i<pathComponents.length) {
            heading += pathComponents[i].getHeading(distance);
        }
        return heading;
    }
    public PathComponent getCurrentPathComponent(float distance){
        int i=0;
        for(; i<pathComponents.length && !(distance<pathComponents[i].getDistance()); i++){
            if(distance>=pathComponents[i].getDistance()){
                distance-=pathComponents[i].getDistance();
            }
        }
        if(i<pathComponents.length) {
            return pathComponents[i];
        }
        else{
            return pathComponents[i-1];
        }
    }
    public float getDistance(){
        return motorCurve.getLength();
    }

    public float getPower(float distance){
        return motorCurve.getValue(distance);
    }
    public float getMaxPower(){
        return motorCurve.getMaxVal();
    }


}
