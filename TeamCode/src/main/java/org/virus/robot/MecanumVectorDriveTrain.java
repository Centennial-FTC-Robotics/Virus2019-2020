package org.virus.robot;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.lynx.LynxEmbeddedIMU;
import com.qualcomm.hardware.lynx.LynxI2cDeviceSynchV1;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.Advanced_Paths.ParametricPath;
import org.virus.agobot.Agobot;
import org.virus.paths.Arc;
import org.virus.paths.Path;
import org.virus.superclasses.Drivetrain;
import org.virus.superclasses.Robot;
import org.virus.util.BetterI2cDeviceSynchImplOnSimple;
import org.virus.util.PIDController;
import org.virus.Advanced_Paths.ParametricFunction2D;
import org.virus.util.Vector2D;

public class MecanumVectorDriveTrain extends Drivetrain {
    public static final float TRACKWIDTHIN =13.25f;
    //standard 4 motor drivetrain
    private int IMUUPDATERATE=100;
    private int odoLoopCounter=0;
    private ExpansionHubMotor lFront;
    private ExpansionHubMotor rFront;
    private ExpansionHubMotor lBack;
    private ExpansionHubMotor rBack;
    private BNO055IMU imu;
    float initialHeading;
    float initialPitch;
    float initialRoll;
    private Orientation currentOrientation;
    private double heading;
    private Vector2D currentPosition;
    PIDController headingController = new PIDController(-.04f, 0 ,0);
    final PIDController moveController = new PIDController(.01f ,0.000f ,.0000f);
    final PIDController arcController = new PIDController(.01f ,0.000f ,.0000f);
    PIDController xController = new PIDController(.06f,.05f ,0, 0.1f);
    PIDController yController = new PIDController(-.06f,-.05f ,0,0.1f);
    private LinearOpMode opMode;
    final static double ENCODER_COUNTS_PER_INCH = (1120.0/(100.0*Math.PI))*25.4;
    float prevLeft;
    float prevRight;
    public Odometry odometry;
    double steerMag;
    Vector2D motorSpeeds;
    Vector2D translationalMvmt;

//    Odometry odometry = new Odometry();
    @Override
    public float encoderToInch(int encoder) {
        return (float)(encoder/ENCODER_COUNTS_PER_INCH);
    }

    @Override
    public int inchToEncoder(float inches) {
        return (int) (inches * ENCODER_COUNTS_PER_INCH);
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
    public void resetOrientation(){
        updateOrientation();
        initialHeading = currentOrientation.firstAngle;
        initialRoll = currentOrientation.secondAngle;
        initialPitch = currentOrientation.thirdAngle;
    }
    public Orientation updateOrientation() {
        //currentOrientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return currentOrientation;
    }
    public Vector2D updatePosition(){
        if(odoLoopCounter%IMUUPDATERATE==0){
            currentOrientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            currentOrientation.firstAngle = AngleUnit.normalizeDegrees(currentOrientation.firstAngle - initialHeading);
            odometry.setRelativeHeading(currentOrientation.firstAngle);
            odometry.updatePosition();
        } else {
            odometry.updatePosition();
        }
        currentOrientation.firstAngle = (float) odometry.relativeHeading();
        heading = odometry.currentHeading();
        odoLoopCounter++;
        return odometry.currentPosition();
    }
    public double getHeading(){ //returns in degrees
        return heading;
        //return AngleUnit.normalizeDegrees(currentOrientation.firstAngle - initialHeading);
    }
    public void initialize(LinearOpMode opMode) {
        lFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lFront");
        rFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rFront");
        lBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lBack");
        rBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rBack");
        rFront.setDirection(DcMotor.Direction.REVERSE);
        rBack.setDirection(DcMotor.Direction.REVERSE);

        odometry = new Odometry(lFront, rFront, lBack);
        odometry.initialize(opMode);

        lFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.opMode=opMode;
        //should be handled by odometry
        /*
        resetAllEncoders();
        waitAllMotors();
        setAllRunUsingEncoders();

         */
        LynxModule module = opMode.hardwareMap.get(LynxModule.class, "Expansion Hub 1");
        I2cDeviceSynch idkWhatThisMeans = new BetterI2cDeviceSynchImplOnSimple(
                new LynxI2cDeviceSynchV1(AppUtil.getDefContext(), module, 0), true);
        imu = new LynxEmbeddedIMU(idkWhatThisMeans);
        imu.initialize(new BNO055IMU.Parameters());
        currentOrientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        lFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

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

    public void waitAllMotors(){
        while(lFront.isBusy() || rFront.isBusy() || lBack.isBusy() || rBack.isBusy()){
        }
    }

    public void setAllRunUsingEncoders(){
        lFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
    public boolean movePath(Path path){
        return movePath(path, moveController);
    }

    public boolean movePath(Path path, PIDController pid){
        opMode.telemetry.addData("heading", currentOrientation.firstAngle - initialHeading);
        opMode.telemetry.addData("leftEncoder", getLeftPos());
        opMode.telemetry.addData("righttEncoder",getRightPos());
        opMode.telemetry.update();
//        if(headingController == null){
//            //.0004
//            headingController = pid;
//
//            //headingController = new PIDController(.016f   ` ,0.000f ,.0000f);
//            headingController.start();
//        }
        headingController = pid;

        updateOrientation();
        float position = (getRightPos() + getLeftPos())/2;
        float targetHeading = path.getHeading(position);
        float correction =  headingController.getValue(0, AngleUnit.normalizeDegrees(currentOrientation.firstAngle - initialHeading - targetHeading));
        //correction = 0;
        opMode.telemetry.addData("heading", currentOrientation.firstAngle - initialHeading);
        opMode.telemetry.addData("leftEncoder", getLeftPos());
        opMode.telemetry.addData("righttEncoder",getRightPos());
        opMode.telemetry.addData("error", AngleUnit.normalizeDegrees(currentOrientation.firstAngle - initialHeading - targetHeading));
        opMode.telemetry.addData("correction", correction);
        opMode.telemetry.update();
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
                power + /*FFRight/4f*/ + correction * (3f/4f + power/(4f*path.getMaxPower())),
                power + /*FFLeft/4f*/ - correction * (3f/4f + power/(4f*path.getMaxPower()))
        );

        //opMode.telemetry.addData("Heading", currentOrientation.firstAngle - initialHeading);
        if(Math.abs(position-path.getDistance())<10 && Math.abs(AngleUnit.normalizeDegrees(currentOrientation.firstAngle - initialHeading - targetHeading))<.5){
            runMotors(0,0);
            return true;
        } else {
            return false;
        }
    }

    public boolean movePath(ParametricPath p, PIDController pid) {

        // positions
        // TODO: find a better way to get the position using encoders - copy MecanumVectorDriveTrain
        updateOrientation();
        double position = (getRightPos() + getLeftPos())/2; // NOTE: this ONLY works when the robot turns with the path!
        double tVal = p.approximateDistance(position, 0.001);

        // targets
        double targetHeading = Math.atan2(ParametricFunction2D.derivative(p.getPathComponent(tVal), p.translatePoint(tVal)), 1);
        Vector2D targetDisplacement = p.getPoint(tVal);

        double headingCorrection = pid.getValue((float) targetHeading, (float) AngleUnit.normalizeDegrees(currentOrientation.firstAngle - initialHeading));
        double xCorrection = pid.getValue((float) (targetDisplacement.getComponent(0).doubleValue()), (float) (odometry.currentPosition().getComponent(0).doubleValue()));
        double yCorrection = pid.getValue((float) (targetDisplacement.getComponent(1).doubleValue()), (float) (odometry.currentPosition().getComponent(1).doubleValue()));
        opMode.telemetry.addData("Heading: ", currentOrientation.firstAngle - initialHeading);
        opMode.telemetry.addData("heading correction: ", headingCorrection);
        opMode.telemetry.addData("x component correction: ", xCorrection);
        opMode.telemetry.addData("y component correction: ", yCorrection);
        opMode.telemetry.update();

        double radius = 1 / ParametricFunction2D.getCurvature(p.getPathComponent(tVal), tVal);



        /*
        * 2 possibilities
        *  - write a method that emulates matthew's move path method with the new curvature methods, might work but kinda sketch
        *  - write a brand new method and employ multiple new methods to get heading and maybe even store a speed and direction function somewhere
        *    move onto rotating while following the path?
        * */
        return false;
    }

    public boolean move(Vector2D move) { // move relative to the robot, but in the field's heading

        move.add(odometry.currentPosition());
        return goToPosition(move, currentOrientation.firstAngle, 1);
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

    public void runMotors(double Left0, double Left1, double Right0, double Right1, double steerMagnitude){
        double maxPower = 1;

//        if (Math.abs(Left0) > 0.01 && Math.abs(Left1) > 0.01 && Math.abs(Right0) > 0.01 && Math.abs(Right1) > 0.01) {
//            steerMagnitude *= 2 * Math.max(Math.max(Left0, Left1), Math.max(Right0, Right1));
//        }


        Left0=Left0+steerMagnitude;
        Left1=Left1+steerMagnitude;
        Right0=Right0-steerMagnitude;
        Right1=Right1-steerMagnitude;
        //make sure no exception thrown if power > 0
        Left0 = Range.clip(Left0, -maxPower, maxPower);
        Left1 = Range.clip(Left1, -maxPower, maxPower);
        Right0 = Range.clip(Right0, -maxPower, maxPower);
        Right1 = Range.clip(Right1, -maxPower, maxPower);

        rBack.setPower(Right1);
        rFront.setPower(Right0);
        lBack.setPower(Left1);
        lFront.setPower(Left0);
//
//        opMode.telemetry.addData("Left Odometry Encoder", odometry.lEncoder.getCurrentPosition());
//        opMode.telemetry.addData("Right Odometry Encoder", odometry.rEncoder.getCurrentPosition());
//        opMode.telemetry.addData("Back Odometry Encoder", odometry.bEncoder.getCurrentPosition());
//        opMode.telemetry.addData("Odometry Position",odometry.position);
//        opMode.telemetry.addData("Odometry Heading",odometry.heading);
    }

    public boolean goToPosition(Vector2D newPosition, double newHeading, double maxSpeed){

        currentPosition = Agobot.drivetrain.updatePosition();
        opMode.telemetry.addData("Position:", currentPosition);
        opMode.telemetry.addData("Heading:", Agobot.drivetrain.getHeading());
        opMode.telemetry.addData("New Position",newPosition);
        opMode.telemetry.addData("New Heading", newHeading);

        updateMotorPowers(newPosition, newHeading);
        opMode.telemetry.addData("Translational Movement", translationalMvmt);
        opMode.telemetry.addData("Steer Magnitude", steerMag);

        double diagSpeed1 = Range.clip(motorSpeeds.getComponent(0), -maxSpeed, maxSpeed);
        double diagSpeed2 = Range.clip(motorSpeeds.getComponent(1), -maxSpeed, maxSpeed);

        if ((translationalMvmt.getComponent(0) != 0) || (translationalMvmt.getComponent(1) != 0)){
            Agobot.drivetrain.runMotors(diagSpeed1, diagSpeed2, diagSpeed2, diagSpeed1, steerMag); //var1 and 2 are computed values found in theUpdateControllerValues method
        } else {
            Agobot.drivetrain.runMotors(0, 0, 0, 0, steerMag);
        }
        opMode.telemetry.update();

        double xDiff = currentPosition.getComponent(0) - newPosition.getComponent(0);
        double yDiff = currentPosition.getComponent(1) - newPosition.getComponent(1);
        double headingDiff = Agobot.drivetrain.getHeading() - newHeading;

        if (Math.abs(xDiff) < 0.5 && Math.abs(yDiff) < 0.5 && Math.abs(headingDiff) < 0.5) {
            Agobot.drivetrain.runMotors(0,0,0,0,0);
            xController.clear();
            yController.clear();
            headingController.clear();
            return false;
        }
        return true;
    }

    public void updateMotorPowers(Vector2D newPosition, double newHeading){
        double x = currentPosition.getComponent(0);
        double y = currentPosition.getComponent(1);

        translationalMvmt = new Vector2D((double) xController.getValue((float)(double)newPosition.getComponent(0), (float)x), (double) -yController.getValue((float)(double)newPosition.getComponent(1), (float)y));
        steerMag = headingController.getValue((float)newHeading, AngleUnit.normalizeDegrees((float) Agobot.drivetrain.getHeading()));
        translationalMvmt.rotate(-Math.toRadians(Agobot.drivetrain.getHeading()));

        double leftx = translationalMvmt.getComponent(0);
        double lefty = translationalMvmt.getComponent(1);
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(Math.pow(lefty, 2) + Math.pow(leftx, 2));

        motorSpeeds = new Vector2D((lefty+leftx)*magnitude/scalar, (lefty-leftx)*magnitude/scalar);

    }
}
