package org.virus.agobot;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.agobot.Agobot;
import org.virus.superclasses.Robot;
import org.virus.superclasses.Subsystem;
import org.virus.util.Vector2D;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeDegrees;

public class Odometry extends Subsystem {
    public ExpansionHubMotor lEncoder;
    public ExpansionHubMotor rEncoder;
    public ExpansionHubMotor bEncoder;
    public Vector2D position; // this has to be in inches
    public double heading = 0;
    double startHeading = 0;

    //make any of these directions -1 to reverse the encoder without affecting the corresponding drive motor
    public int lEncoderDirection = 1;
    public int rEncoderDirection = 1;
    public int bEncoderDirection = -1;

    public int lEncoderPrevious = 0;
    public int rEncoderPrevious = 0;
    public int bEncoderPrevious = 0;
    public int lEncoderCounts=0;
    public int rEncoderCounts=0;
    public int bEncoderCounts=0;

    public int deltalEncoder;
    public int deltarEncoder;
    public int deltabEncoder;
    public double deltaHeading;
    public double deltax;
    public double deltay;

    public double headingCorrection=0;


    final static double ENCODER_COUNTS_PER_INCH = 4096.0/(2.0*1.0*Math.PI);
    final static double RADIUS = 12.80873957/2.0 * 722.0/720.0;/*14.553420608108109/2.0   * 928.0/1080.0;*/
    final static double BENCODER_OFFSET = 4956.20177;
    Vector2D fieldCentricDelta;
    Vector2D robotCentricDelta;

    Odometry(ExpansionHubMotor lEncoder, ExpansionHubMotor rEncoder, ExpansionHubMotor bEncoder){
        this.lEncoder=lEncoder;
        this.rEncoder=rEncoder;
        this.bEncoder=bEncoder;

    }
    @Override
    public void initialize(LinearOpMode opMode) {

        resetAllEncoders();
        waitAllEncoders();
        setAllRunWithoutEncoders();

        position = new Vector2D(0,0);
    }
    public void setStartLocation(Vector2D startPosition, double startHeading){ //inches, and degrees
        position = new Vector2D(startPosition);
        this.startHeading = Math.toRadians(startHeading);
        heading = normalizeRadians(heading + this.startHeading);
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
    public void setAllRunWithoutEncoders(){
        lEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    //lrb
    public void updateEncoders(){
        Agobot.updateHub1Data();
        lEncoderCounts = Agobot.getHub1Data().getMotorCurrentPosition(lEncoder);
        rEncoderCounts = Agobot.getHub1Data().getMotorCurrentPosition(rEncoder);
        bEncoderCounts = Agobot.getHub1Data().getMotorCurrentPosition(bEncoder);
    }

    public int getlEncoderCounts() {
        return lEncoderCounts*lEncoderDirection;
    }

    public int getrEncoderCounts() {
        return rEncoderCounts*rEncoderDirection;
    }

    public int getbEncoderCounts() {
        return bEncoderCounts*bEncoderDirection;
    }

    public void updatePosition(){
        updateEncoders();

        deltalEncoder =  getlEncoderCounts() - lEncoderPrevious;
        deltarEncoder = getrEncoderCounts() - rEncoderPrevious;
        deltabEncoder = getbEncoderCounts() - bEncoderPrevious;

        lEncoderPrevious = getlEncoderCounts();
        rEncoderPrevious = getrEncoderCounts();
        bEncoderPrevious = getbEncoderCounts();

        deltaHeading = (deltarEncoder - deltalEncoder)/(2.0*RADIUS*ENCODER_COUNTS_PER_INCH); //it's in radians
        heading = /*normalizeRadians(heading + deltaHeading);*/ normalizeRadians((getrEncoderCounts()-getlEncoderCounts())/(2.0*RADIUS*ENCODER_COUNTS_PER_INCH) + startHeading+headingCorrection);

//        deltaHorizontal = deltabEncoder - (deltaHeading*BENCODER_OFFSET); //takes away the bEncoder counts that were a result of turning
//
//        deltay = (deltarEncoder + deltalEncoder)/2; //robot centric y and x
//        deltax = deltaHorizontal;

        if(deltaHeading == 0){ //have to do it like this because java doesn't do l'Hopital's rule
            deltax = deltabEncoder;
            deltay = (deltalEncoder + deltarEncoder)/2;
        }else{
            double turnRadius = RADIUS*ENCODER_COUNTS_PER_INCH*(deltalEncoder + deltarEncoder)/(deltarEncoder - deltalEncoder);
            double strafeRadius = deltabEncoder/deltaHeading - BENCODER_OFFSET;

            deltax = turnRadius*(Math.cos(deltaHeading) - 1) + strafeRadius*Math.sin(deltaHeading);
            deltay = turnRadius*Math.sin(deltaHeading) + strafeRadius*(1 - Math.cos(deltaHeading));
        }

        robotCentricDelta = new Vector2D(encoderToInch(deltax), encoderToInch(deltay));

        fieldCentricDelta = new Vector2D(encoderToInch(deltay), encoderToInch(-deltax));
        fieldCentricDelta.rotate(heading);
        position.add(fieldCentricDelta);
    }

    public Vector2D getRobotCentricDelta(){
        return robotCentricDelta;
    }
    public Vector2D getFieldCentricDelta(){
        return fieldCentricDelta;
    }
    public Vector2D currentPosition(){
        return position;
    }//degrees
    public double currentHeading(){
        return Math.toDegrees(heading);
    }//degrees
    public double relativeHeading(){ return Math.toDegrees(normalizeRadians(heading - startHeading)); }//degrees
    public void setHeading(double heading){ //degrees
        this.heading = normalizeRadians(Math.toRadians(heading));
    }
    public void setHeadingCorrection(double correct){
        headingCorrection = normalizeRadians(correct-this.heading+headingCorrection);
    }
    public void setRelativeHeading(double relativeHeading){
        heading = normalizeRadians(Math.toRadians(relativeHeading) + startHeading);
    }

    public float encoderToInch(double encoder) {
        return (float)(encoder/ENCODER_COUNTS_PER_INCH);
    }

    public int inchToEncoder(float inches) {
        return (int) (inches * ENCODER_COUNTS_PER_INCH);
    }

    public double normalizeRadians(double angle){
        while(angle >= 2*Math.PI) {
            angle -= 2*Math.PI;
        }
        while(angle < 0.0) {
            angle += 2*Math.PI;
        }
        return angle;
    }

}
