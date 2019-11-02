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
    Vector2D position;
    double heading = 0;
    int lEncoderPrevious = 0;
    int rEncoderPrevious = 0;
    int bEncoderPrevious = 0;
    final static double ENCODER_COUNTS_PER_INCH = 1024/(2.0*Math.PI);
    final static double RADIUS = 6.5;
    @Override
    public void initialize(OpMode opMode) {
        lEncoder = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lEncoder");
        rEncoder = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rEncoder");
        bEncoder = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "bEncoder");

        rEncoder.setDirection(DcMotorSimple.Direction.REVERSE);

        resetAllEncoders();
        waitAllEncoders();
        setAllRunUsingEncoders();

        position = new Vector2D(0,0);
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
    public double updateOrientation(){
        heading = 180*(rEncoder.getCurrentPosition() - lEncoder.getCurrentPosition())/(2*Math.PI*RADIUS);
        return heading;
    }
    public Vector2D updatePosition(){
        int deltalEncoder = lEncoder.getCurrentPosition() - lEncoderPrevious;
        int deltarEncoder = rEncoder.getCurrentPosition() - rEncoderPrevious;
        int deltabEncoder = bEncoder.getCurrentPosition() - bEncoderPrevious;
        Vector2D deltaDisp = new Vector2D(deltabEncoder, (deltalEncoder + deltarEncoder)/2);
        deltaDisp.rotate(Math.toRadians(heading));
        position.add(deltaDisp);
        lEncoderPrevious = lEncoder.getCurrentPosition();
        rEncoderPrevious = rEncoder.getCurrentPosition();
        bEncoderPrevious = bEncoder.getCurrentPosition();
        return position;
    }

    public float encoderToInch(int encoder) {
        return (float)(encoder/ENCODER_COUNTS_PER_INCH);
    }

    public int inchToEncoder(float inches) {
        return (int) (inches * ENCODER_COUNTS_PER_INCH);
    }

}
