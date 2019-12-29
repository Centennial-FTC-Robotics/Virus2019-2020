package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.superclasses.Robot;
import org.virus.superclasses.Subsystem;

public class Slides extends Subsystem {
    public ExpansionHubMotor slideLeft;
    public ExpansionHubMotor slideRight;

    //TODO: test for correct values
    public final int slideMin = 0;
    public final int slideMax = 900;
    public final int error = 50;

    public int position = 0;

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
        slideLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //restrict slides to min and max values
        position = Range.clip(position, slideMin, slideMax);

        //set slides to position
        slideLeft.setTargetPosition(position);
        slideRight.setTargetPosition(position);
        //set speed of slides
        slideLeft.setPower(0.8);
        slideRight.setPower(0.8);
        //wait until slides are within error
        while (Math.abs(getPosition()-position) > error);
        //stop slides
        slideLeft.setPower(0);
        slideRight.setPower(0);

        slideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        return true;
    }

    //used with controllers
    //TODO: might need to make method use slideLeft instead (adjust while testing)
    public void slidePower(double power){
        //restrict slide movement between min and max values
        int slidePos = getPosition();
        if(slidePos <= -10 || slidePos >= slideMax){
            slideRight.setPower(0);
            slideLeft.setPower(0);
        }else{
            slideRight.setPower(power);
            slideLeft.setPower(power);
        }
    }
}
