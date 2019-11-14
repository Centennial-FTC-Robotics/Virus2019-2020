package org.virus.robot;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.lynx.LynxEmbeddedIMU;
import com.qualcomm.hardware.lynx.LynxI2cDeviceSynchV1;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.openftc.revextensions2.ExpansionHubMotor;

import org.virus.paths.Arc;
import org.virus.paths.LinearMovement;
import org.virus.paths.Movement;
import org.virus.paths.Path;
import org.virus.superclasses.Drivetrain;
import org.virus.superclasses.Robot;
import org.virus.util.BetterI2cDeviceSynchImplOnSimple;
import org.virus.util.PIDController;

public class TankDrivetrain extends Drivetrain {
    Robot robot;
    public static final float MAXACCEL = .015f;
    public static final float MAXDEACCEL = 0.0006f;
    public static final float TRACKWIDTHIN = 16.75f;
    public static final float COUNTSPERREV = 383.6f;
    public static final float GEARRATIO = 1.0f;
    //private Orientation targetHeading = new Orientation();
    private Orientation currentHeading;
    private Position currentPosition = new Position();
    private Position targetPosition = new Position();
    private LinearOpMode opMode;
    private ExpansionHubMotor lFront;
    private ExpansionHubMotor rFront;
    private ExpansionHubMotor lBack;
    private ExpansionHubMotor rBack;
    private BNO055IMU imu;
    float initialHeading;
    float initialPitch;
    float initialRoll;
    PIDController headingController;
    PIDController moveController = new PIDController(.013f ,0.002f ,.00000f);
    PIDController arcController;
    float prevLeft;
    float prevRight;
    //Orientation orientation = new Orientation();

    TankDrivetrain(){
    }

    public void translate (float direction, float distance){

    }
    @Override
    public float encoderToInch(int encoder){
        float revs = 1f/GEARRATIO * (float)(encoder)/COUNTSPERREV;
        float distance = (float) (revs * 4f * Math.PI);
        return distance;
    }
    @Override
    public int inchToEncoder(float inches){
        float revs = (float)(inches /  (4f * Math.PI));
        float encoder = revs * COUNTSPERREV * GEARRATIO;
        return Math.round(encoder);

    }
    public void move(Movement movement) {
        runMotors(
                movement.RFrontCurve.getValue(getRightPos()),
                movement.LFrontCurve.getValue(getLeftPos())
        );
    }

    public void move(LinearMovement movement){
        runMotors(
                movement.RFrontCurve.getValue((getRightPos() + getLeftPos())/2),
                movement.RFrontCurve.getValue((getRightPos() + getLeftPos())/2)
        );
        opMode.telemetry.addData("distance", (getRightPos() + getLeftPos())/2);
    }

    public void moveIMU(LinearMovement movement, float targetHeading){
        if(headingController == null){
            headingController = new PIDController(.010f ,0 ,.0005f);
            headingController.start();
        }
        updateOrientation();
        float correction =  headingController.getValue(targetHeading, currentHeading.firstAngle - initialHeading);
        float position = (getRightPos() + getLeftPos())/2;
        float power = movement.RFrontCurve.getValue((position));
        runMotors(
                 power + correction  * (1f/2f + power/(2*movement.RFrontCurve.getMaxVal())) ,
                power - correction  * (1f/2f + power/(2*movement.RFrontCurve.getMaxVal()))
        );
        opMode.telemetry.addData("distance", (getRightPos() + getLeftPos())/2);
    }

    public void moveIMUArc(LinearMovement movement, float curvature){
        if(headingController == null){
            //.0004
            headingController = new PIDController(.05f ,0 ,.001f);
            headingController.start();
        }
        updateOrientation();
        float position = (getRightPos() + getLeftPos())/2;
        float targetHeading = position * curvature;
        float correction =  headingController.getValue(targetHeading, AngleUnit.normalizeDegrees(currentHeading.firstAngle - initialHeading));
        float power = movement.RFrontCurve.getValue((position));
        runMotors(
                power + correction * (2f/3f + power/(3f*movement.RFrontCurve.getMaxVal())),
                power - correction * (2f/3f + power/(3f*movement.RFrontCurve.getMaxVal()))
        );
        opMode.telemetry.addData("Heading", currentHeading.firstAngle - initialHeading);
    }

    public boolean movePath(Path path, PIDController pid){
        if(headingController == null){
            //.0004
            headingController = pid;

            //headingController = new PIDController(.016f ,0.000f ,.0000f);
            headingController.start();
        }
        updateOrientation();
        float position = (getRightPos() + getLeftPos())/2;
        float targetHeading = path.getHeading(position);
        float correction =  headingController.getValue(0, AngleUnit.normalizeDegrees(currentHeading.firstAngle - initialHeading - targetHeading));
        //correction = 0;
        float power = path.getPower(position);
        float FFLeft=0;
        float FFRight=0;

        /*
        if(path.getCurrentPathComponent(position).getDegrees()>0){
            float radius = ((Arc)(path.getCurrentPathComponent(position))).getRadius();
            FFLeft = power * (encoderToInch(Math.round(radius)) - TRACKWIDTHIN/2f) / (encoderToInch(Math.round(radius)));
            FFRight = power * (encoderToInch(Math.round(radius)) + TRACKWIDTHIN/2f) / (encoderToInch(Math.round(radius)));
            FFLeft = FFLeft-power;
            FFRight = FFRight-power;
            //FFLeft =0;
        }
        else if (path.getCurrentPathComponent(position).getDegrees()<0){
            float radius = ((Arc)(path.getCurrentPathComponent(position))).getRadius();
            FFLeft = power * (encoderToInch(Math.round(radius)) + TRACKWIDTHIN/2f) / (encoderToInch(Math.round(radius)));
            FFRight = power * (encoderToInch(Math.round(radius)) - TRACKWIDTHIN/2f) / (encoderToInch(Math.round(radius)));
            FFLeft = FFLeft-power;
            FFRight = FFRight-power;
            //FFRight =0;

        }
        else{
            FFLeft=0;
            FFRight=0;
        }
        */
        if(path.getCurrentPathComponent(position).getDegrees()>0){
            float radius = ((Arc)(path.getCurrentPathComponent(position))).getRadius();
            FFLeft = power * (encoderToInch(Math.round(radius)) - TRACKWIDTHIN/2f) / (encoderToInch(Math.round(radius)) + TRACKWIDTHIN/2f);
            FFLeft = FFLeft-power;
            FFLeft =0;

        }
        else if (path.getCurrentPathComponent(position).getDegrees()<0){
            float radius = ((Arc)(path.getCurrentPathComponent(position))).getRadius();
            FFRight = power * (encoderToInch(Math.round(radius)) - TRACKWIDTHIN/2f) / (encoderToInch(Math.round(radius)) + TRACKWIDTHIN/2f);
            FFRight = FFRight-power;
            FFRight =0;

        }

        runMotors(
                power + FFRight/4f + correction * (3f/4f + power/(4f*path.getMaxPower())),
                power + FFLeft/4f - correction * (3f/4f + power/(4f*path.getMaxPower()))
        );
        opMode.telemetry.addData("Error", AngleUnit.normalizeDegrees(currentHeading.firstAngle - initialHeading - targetHeading));
        //opMode.telemetry.addData("Heading", currentHeading.firstAngle - initialHeading);
        if(Math.abs(position-path.getDistance())<10 && Math.abs(AngleUnit.normalizeDegrees(currentHeading.firstAngle - initialHeading - targetHeading))<.5){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean movePath(Path path){
        return movePath(path, moveController);
    }

    public void driveIMU(float targetHeading, float power){
        if(headingController == null){
            headingController = new PIDController(.005f ,0 ,0);
            headingController.start();
        }
        updateOrientation();
        float correction =  headingController.getValue(targetHeading, currentHeading.firstAngle - initialHeading);
        opMode.telemetry.addData("current", currentHeading.firstAngle - initialHeading);
        opMode.telemetry.addData("1st angle", currentHeading.firstAngle);
        opMode.telemetry.addData("initial", initialHeading);
        opMode.telemetry.addData("correction", correction);
        runMotors(power + correction, power - correction);

    }

    public int getRightPos(){
        return (Robot.getHub1().getBulkInputData().getMotorCurrentPosition(rFront)+ Robot.getHub1().getBulkInputData().getMotorCurrentPosition(rBack))/2;
    }

    public int getLeftPos(){
        return (Robot.getHub1().getBulkInputData().getMotorCurrentPosition(lFront)+ Robot.getHub1().getBulkInputData().getMotorCurrentPosition(lBack))/2;
    }

    public void resetAllEncoders(){
        lFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setAllRunUsingEncoders(){
        lFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    public void waitAllMotors(){
        while(lFront.isBusy() || rFront.isBusy() || lBack.isBusy() || rBack.isBusy()){
        }
    }

    public void runMotors(float right,float left){
        if(prevLeft!=left){
            lFront.setPower(Range.clip(left,-1,1));
            lBack.setPower(Range.clip(left, -1, 1));
        }
        if(prevRight!=right){
            rFront.setPower(Range.clip(right,-1,1));
            rBack.setPower(Range.clip(right,-1,1));
        }
        prevLeft = left;
        prevRight = right;
    }

    public void initializeIMU() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);
        if(opMode.getClass()== LinearOpMode.class){
            while (((LinearOpMode)opMode).opModeIsActive() && !imu.isGyroCalibrated()) ;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        resetOrientation();
    }

    public Orientation updateOrientation() {
        currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return currentHeading;
    }

    public void resetOrientation(){
        updateOrientation();
        initialHeading = currentHeading.firstAngle;
        initialRoll = currentHeading.secondAngle;
        initialPitch = currentHeading.thirdAngle;
    }

    @Override
    public void initialize(LinearOpMode opMode) {
        this.opMode = opMode;
        lFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lFront");
        rFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rFront");
        lBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lBack");
        rBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rBack");
        lFront.setDirection(DcMotor.Direction.REVERSE);
        lBack.setDirection(DcMotor.Direction.REVERSE);
        resetAllEncoders();
        waitAllMotors();
        setAllRunUsingEncoders();
        LynxModule module = opMode.hardwareMap.get(LynxModule.class, "Expansion Hub 1");
        I2cDeviceSynch idkWhatThisMeans = new BetterI2cDeviceSynchImplOnSimple(
                new LynxI2cDeviceSynchV1(AppUtil.getDefContext(), module, 0), true);
        imu = new LynxEmbeddedIMU(idkWhatThisMeans);
        imu.initialize(new BNO055IMU.Parameters());

        //imu = opMode.hardwareMap.get(BNO055IMU.class, "imu");
    }
}
