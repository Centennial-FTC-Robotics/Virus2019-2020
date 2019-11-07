package org.virus.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.superclasses.Subsystem;
import org.virus.util.Vector2D;

public class Odometry extends Subsystem {
    ExpansionHubMotor lEncoder;
    ExpansionHubMotor rEncoder;
    ExpansionHubMotor bEncoder;
    Vector2D position; // this has to be in inches
    double heading = 0;
    int lEncoderPrevious = 0;
    int rEncoderPrevious = 0;
    int bEncoderPrevious = 0;
    final static double ENCODER_COUNTS_PER_INCH = 4096/(2*1*Math.PI);
    final static double RADIUS = 6.5;
    Vector2D fieldCentricDelta;
    Vector2D robotCentricDelta;

    Odometry(ExpansionHubMotor lEncoder, ExpansionHubMotor rEncoder, ExpansionHubMotor bEncoder){
        this.lEncoder=lEncoder;
        this.rEncoder=rEncoder;
        this.bEncoder=bEncoder;

    }
    @Override
    public void initialize(OpMode opMode) {

        resetAllEncoders();
        waitAllEncoders();
        setAllRunUsingEncoders();

        position = new Vector2D(0,0);
    }
    public void setStartLocation(Vector2D startPosition, double startHeading){ //inches, and degrees
        position = new Vector2D(startPosition);
        heading = Math.toRadians(startHeading);
    }

    public void resetAllEncoders(){
        lEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    public void waitAllEncoders(){
        while(lEncoder.isBusy() || rEncoder.isBusy() || bEncoder.isBusy()){
        }
    }
    public void setAllRunUsingEncoders(){
        lEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void updatePosition(){
        int deltalEncoder = lEncoder.getCurrentPosition() - lEncoderPrevious;
        int deltarEncoder = rEncoder.getCurrentPosition() - rEncoderPrevious;
        int deltabEncoder = bEncoder.getCurrentPosition() - bEncoderPrevious;

        double deltaHeading = (deltarEncoder - deltalEncoder)/(2*RADIUS); //it's in radians
        double deltax;
        double deltay;

        if (Math.abs(deltaHeading) < 0.0001){ //don't really trust java to not floating point everything up
            deltax = deltabEncoder;
            deltay = (deltalEncoder + deltarEncoder)/2;
        } else {
            double moveRad = (deltalEncoder + deltarEncoder)/(2 * deltaHeading);
            double strafeRad = deltabEncoder/deltaHeading;
            deltax = moveRad * (Math.cos(deltaHeading) - 1) + strafeRad * Math.sin(deltaHeading);
            deltay = moveRad * Math.sin(deltaHeading) - strafeRad * (Math.cos(deltaHeading) - 1);
        }

        Vector2D deltaDisp = new Vector2D(encoderToInch(deltax), encoderToInch(deltay));
        robotCentricDelta=deltaDisp;
        heading += deltaHeading;
        deltaDisp.rotate(heading);
        fieldCentricDelta=deltaDisp;

        position.add(deltaDisp);

        lEncoderPrevious = lEncoder.getCurrentPosition();
        rEncoderPrevious = rEncoder.getCurrentPosition();
        bEncoderPrevious = bEncoder.getCurrentPosition();
        
        //return deltaDisp;
    }

    public Vector2D getRobotCentricDelta(){
        return robotCentricDelta;
    }
    public Vector2D getFieldCentricDelta(){
        return fieldCentricDelta;
    }
    public Vector2D currentPosition(){
        return position;
    }

    public float encoderToInch(double encoder) {
        return (float)(encoder/ENCODER_COUNTS_PER_INCH);
    }

    public int inchToEncoder(float inches) {
        return (int) (inches * ENCODER_COUNTS_PER_INCH);
    }

}
