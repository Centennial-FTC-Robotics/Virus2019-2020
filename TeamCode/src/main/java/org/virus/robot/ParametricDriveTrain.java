package org.virus.robot;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.Advanced_Paths.ParametricPath;
import org.virus.superclasses.Drivetrain;
import org.virus.superclasses.Robot;
import org.virus.util.PIDController;
import org.virus.util.ParametricFunction2D;
import org.virus.util.Vector2D;

public class ParametricDriveTrain extends Drivetrain {

    public static final float TRACKWIDTHIN =13.25f;
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

    private LinearOpMode opModeRef;

    // path definitions
    private Vector2D displacement;

    public float encoderToInch(int encoder) {
        return 0;
    }

    public int inchToEncoder(float inches) {
        return 0;
    }

    public Orientation updateOrientation() {
        currentHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return currentHeading;
    }

    public void initialize(LinearOpMode opMode) {
        lFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lFront");
        rFront = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rFront");
        lBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "lBack");
        rBack = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "rBack");
        rFront.setDirection(DcMotor.Direction.REVERSE);
        rBack.setDirection(DcMotor.Direction.REVERSE);
        opModeRef = opMode;
    }

    public int getRightPos(){
        return (Robot.getHub1().getBulkInputData().getMotorCurrentPosition(rFront)+ Robot.getHub1().getBulkInputData().getMotorCurrentPosition(rBack))/2;
    }

    public int getLeftPos(){
        return (Robot.getHub1().getBulkInputData().getMotorCurrentPosition(lFront)+ Robot.getHub1().getBulkInputData().getMotorCurrentPosition(lBack))/2;
    }

    public boolean movePath(ParametricPath p, PIDController pid) {

        // positions
        double position = (getRightPos() + getLeftPos())/2; // NOTE: this ONLY works when the robot turns with the path!
        double tVal = p.approximateDistance(position, 0.001);

        // targets
        double targetHeading = p.getAngle(tVal);
        Vector2D targetDisplacement = p.getPoint(tVal);

        return false;
    }
}
