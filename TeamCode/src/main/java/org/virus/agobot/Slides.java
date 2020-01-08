package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.superclasses.Robot;
import org.virus.superclasses.Subsystem;
import org.virus.util.PIDController;

public class Slides extends Subsystem {

    public ExpansionHubMotor slideLeft;
    public ExpansionHubMotor slideRight;
    PIDController slidesController = new PIDController(.004f, 0f, 0f, .1f);

    //TODO: test for correct values
    public final int slideMin = 0;
    public final int slideMax = 1350;
    public final int tolerance = 50;

    public static final double ENCODER_PER_INCH = 84.81f;

    //public int position = 0;
    double prevPower = 0;
    public int holdSlidePos = 0;

    @Override
    public void initialize(LinearOpMode opMode) {
        slideLeft = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "slideLeft");
        slideRight = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "slideRight");

        slideLeft.setDirection(DcMotor.Direction.REVERSE);

        slideLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public int getPosition(){
        Robot.updateHub2Data();
        return (Robot.getHub2Data().getMotorCurrentPosition(slideLeft) + Robot.getHub2Data().getMotorCurrentPosition(slideLeft))/2;
    }
    //position in encoder counts
    public boolean slides(int position){
/*

        //restrict slides to min and max values
        position = Range.clip(position, slideMin, slideMax);

        //set slides to position
        slideLeft.setTargetPosition(position);
        slideRight.setTargetPosition(position);

        slideLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //set speed of slides
        slideLeft.setPower(1);
        slideRight.setPower(1);*/
        //slides done when within error
        slideLeft.setPower(slidesController.getValue(position, this.getPosition()));
        slideRight.setPower(slidesController.getValue(position, this.getPosition()));
        return !(Math.abs(getPosition() - position) > tolerance);

    }

    //used with controllers
    public void slidePower(double power){
        //if nothing is happening with controllers, hold current position
        if(Math.abs(power) < 0.01){
            if(Math.abs(prevPower) > 0.01){
                holdSlidePos = getPosition();
            }
            slides(holdSlidePos);
        }else {
            //restrict slide movement between min and max values
            int slidePos = getPosition();
            if ((slidePos <= 0 && power < 0) || (slidePos >= slideMax && power > 0)) {
                slideRight.setPower(0);
                slideLeft.setPower(0);
            } else {
                slideRight.setPower(power);
                slideLeft.setPower(power);
            }
        }
        prevPower = power;
    }
}
