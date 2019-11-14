package org.virus.example;

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

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.openftc.revextensions2.ExpansionHubMotor;

import org.virus.paths.Arc;
import org.virus.paths.Path;
import org.virus.superclasses.Drivetrain;
import org.virus.superclasses.Robot;
import org.virus.util.BetterI2cDeviceSynchImplOnSimple;
import org.virus.util.PIDController;

public class ExampleDrivetrain extends Drivetrain {

    public static final float TRACKWIDTHIN =13.25f;
    //standard 4 motor drivetrain
    private ExpansionHubMotor lFront;
    private ExpansionHubMotor rFront;
    private ExpansionHubMotor lBack;
    private ExpansionHubMotor rBack;
    private BNO055IMU imu;
    float initialHeading;
    float initialPitch;
    float initialRoll;
    private Orientation currentHeading;
    PIDController headingController;
    final PIDController moveController = new PIDController(.01f ,0.000f ,.0000f);
    final PIDController arcController = new PIDController(.01f ,0.000f ,.0000f);
    private LinearOpMode opMode;

    float prevLeft;
    float prevRight;

    @Override
    public float encoderToInch(int encoder) {
        return 0;
    }

    @Override
    public int inchToEncoder(float inches) {
        return 0;
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
        initialHeading = currentHeading.firstAngle;
        initialRoll = currentHeading.secondAngle;
        initialPitch = currentHeading.thirdAngle;
    }
    @Override
    public void initialize(LinearOpMode opMode) {
        lFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lFront");
        rFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rFront");
        lBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lBack");
        rBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rBack");
        rFront.setDirection(DcMotor.Direction.REVERSE);
        rBack.setDirection(DcMotor.Direction.REVERSE);
        this.opMode=opMode;
        resetAllEncoders();
        waitAllMotors();
        setAllRunUsingEncoders();
        LynxModule module = opMode.hardwareMap.get(LynxModule.class, "Expansion Hub 1");
        I2cDeviceSynch idkWhatThisMeans = new BetterI2cDeviceSynchImplOnSimple(
                new LynxI2cDeviceSynchV1(AppUtil.getDefContext(), module, 0), true);
        imu = new LynxEmbeddedIMU(idkWhatThisMeans);
        imu.initialize(new BNO055IMU.Parameters());
        currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
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

    public Orientation updateOrientation() {
        currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return currentHeading;
    }

    //follows a defined smooth continuous path
//    public boolean move(Path path){
//        //pretend there's implementation here
//        //there's implementation in the TankDrivetrain class and stuff if you really care
//        return false;
//        //once implemented should return true or false based on whether path is complete
//    }
    public void moveLeftTest() {
        lFront.setPower(1);
        lBack.setPower(1);

    }

    public boolean movePath(Path path){
        return movePath(path, moveController);
    }

    public boolean movePath(Path path, PIDController pid){
        opMode.telemetry.addData("heading", currentHeading.firstAngle - initialHeading);
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
        float correction =  headingController.getValue(0, AngleUnit.normalizeDegrees(currentHeading.firstAngle - initialHeading - targetHeading));
        //correction = 0;
        opMode.telemetry.addData("heading", currentHeading.firstAngle - initialHeading);
        opMode.telemetry.addData("leftEncoder", getLeftPos());
        opMode.telemetry.addData("righttEncoder",getRightPos());
        opMode.telemetry.addData("error", AngleUnit.normalizeDegrees(currentHeading.firstAngle - initialHeading - targetHeading));
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

        //opMode.telemetry.addData("Heading", currentHeading.firstAngle - initialHeading);
        if(Math.abs(position-path.getDistance())<10 && Math.abs(AngleUnit.normalizeDegrees(currentHeading.firstAngle - initialHeading - targetHeading))<.5){
            runMotors(0,0);
            return true;
        } else {
            return false;
        }
    }

    private boolean turnAbsolute(float angle, float speed, PIDController pid) {



        return false;
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

    //turn around the center of the robot
    public boolean pivot(float angle, float speed){
        //pretend there's implementation here


        return false;
        //once implemented should return true or false based on whether pivot is complete
    }



    public void runMotors(double Left0, double Left1, double Right0, double Right1, double steerMagnitude){
        double maxPower=1;

        if (Left0!=0&&Left1!=0&&Right0!=0&&Right1!=0) {
            steerMagnitude *= 2 * Math.max(Math.max(Left0, Left1), Math.max(Right0, Right1));
        }

        Left0=Left0+steerMagnitude;
        Left1=Left1+steerMagnitude;
        Right0=Right0-steerMagnitude;
        Right1=Right1-steerMagnitude;
        //make sure no exception thrown if power > 0
        Left0 = Range.clip(Left0, -maxPower, maxPower);
        Left1 = Range.clip(Left1, -maxPower, maxPower);
        Right0 = Range.clip(Right0, -maxPower, maxPower);
        Right1 = Range.clip(Right1, -maxPower, maxPower);
        rBack.setPower(Right0);
        rFront.setPower(Right1);
        lBack.setPower(Left0);
        lFront.setPower(Left1);
    }

    //turn around the center of the robot
//    public boolean pivot(float angle, float speed, PIDController pid) {
//        //pretend there's implementation here
//        double distance = ((12.9) * 2 * Math.PI) * ((angle % 360) / 360);
//
//
//        return false;
//        //once implemented should return true or false based on whether pivot is complete
//    }
}
