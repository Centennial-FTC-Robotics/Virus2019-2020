package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.openftc.revextensions2.ExpansionHubMotor;
import org.virus.superclasses.Subsystem;

public class Slides extends Subsystem {
    public ExpansionHubMotor slideLeft;
    public ExpansionHubMotor slideRight;

    //TODO: test for correct values
    public final int slideMin = 0;
    public final int slideMax = 5000;
    public final int error = 50;

    public int position = 0;

    @Override
    public void initialize(LinearOpMode opMode) {
        slideLeft = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "slideLeft");
        slideRight = (ExpansionHubMotor)opMode.hardwareMap.get(DcMotor.class, "slideRight");

        slideRight.setDirection(DcMotor.Direction.REVERSE);

        slideLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        slideRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        slideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
        while (Math.abs(slideLeft.getCurrentPosition()-position) > error);
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
        if(slideRight.getCurrentPosition() <= 0 || slideRight.getCurrentPosition() >= slideMax){
            slideRight.setPower(0);
            slideLeft.setPower(0);
        }else{
            slideRight.setPower(power);
            slideLeft.setPower(power);
        }
    }
}