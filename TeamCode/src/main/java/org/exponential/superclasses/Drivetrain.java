package org.exponential.superclasses;

public abstract class Drivetrain extends Subsystem {
    public abstract float encoderToInch(int encoder);
    public abstract int inchToEncoder(float inches);
}
