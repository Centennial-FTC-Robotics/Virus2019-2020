package org.virus.agobot;

import android.graphics.Color;

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.virus.superclasses.Subsystem;

public class Arm extends Subsystem {


    public Servo leftArm;
    public Servo rightArm;
    public LynxI2cColorRangeSensor stoneSensor;
    // drop = 0, standby = 1, in = 2
    public String[] armPositions = {"drop", "standby", "in"};
    public String[] extendPositions = {"drop", "topstone", "standby", "in"};
    public int armPosition;
    public double leftPosition = 0.0;
    public double rightPosition = 1.0;
    public boolean topStone = false;
    public boolean prevTopStone = false;
    private final double offset=.05;
    //in: left = 0.0, right = 1.0
    //standby: left = 0.2, right = 0.8
    //drop: left: 1.0, right = 0.0

    @Override
    public void initialize(LinearOpMode opMode) {
        //TODO
        leftArm = opMode.hardwareMap.servo.get("leftArm");
        rightArm = opMode.hardwareMap.servo.get("rightArm");
        stoneSensor = opMode.hardwareMap.get(LynxI2cColorRangeSensor.class, "stoneSensor");
        stoneSensor.initialize();
        armPosition = 2;
    }

    public void armFlipOut(boolean out){

        topStone = Agobot.slides.getPosition() > (6 * 4 * Slides.ENCODER_PER_INCH);

        if (topStone && !prevTopStone) {

            if (armPosition > 0 && armPosition < 3) {

                armPosition++;
            }
        } else if (!topStone && prevTopStone) {

            if (armPosition > 1) {

                armPosition--;
            }
        }

        double left = 0.0;
        double right = 1.0;



        if(out && armPosition > 0){
            armPosition--;
        }else if(!out && !getArmPosition().equals("in")){
            armPosition++;
        }

        if (topStone) {

            switch(armPosition){
                case 0:
                    //drop
                    left = 0.1;
                    right = 0.9;
                    break;
                case 1:
                    //intermediate 2
                    left = 0.22;
                    right = 0.78;
                    break;
                case 2:
                    //standby // intermediate 1
                    left = 0.87;
                    right = 0.13;
                    break;
                case 3:
                    //in
                    left = 1;
                    right = 0;
                    break;
            }
        } else {

            switch(armPosition){
                case 0:
                    //drop
                    left = 0.05;
                    right = 0.95;
                    break;
                case 1:
                    //standby
                    left = 0.92;
                    right = 0.08;
                    break;
                case 2:
                    //in
                    left = 1;
                    right = 0;
                    break;
//                default:
//                    // why is it here
//                    left = 1;
//                    right = 0;
//                    armPosition = 2;
//                    break;
            }
        }

        leftArm.setPosition(left);
        leftPosition = left;
        rightArm.setPosition(right);
        rightPosition = right;

        prevTopStone = topStone;
    }

    public String getArmPosition(){

        String armPos = "";

        if (topStone) {

            armPos = extendPositions[armPosition];
        } else {

            armPos = armPositions[armPosition];
        }

        return armPos;
    }

    public double getStonePosition() {

        return stoneSensor.getDistance(DistanceUnit.MM);
    }

    public boolean isStoneIn() {

        // this tries to check if the stone is within 5cm and tries to compare its color to a manually computed RGB value
        return (getStonePosition() < 70 && isColor(stoneSensor.red(), stoneSensor.green(), stoneSensor.blue(), 16389122));
    }

    public boolean isColor(int red, int green, int blue, int rgbRef) {

        double redDeviance = Math.abs(red - getRed(rgbRef));
        double greenDeviance = Math.abs(green - getGreen(rgbRef));
        double blueDeviance = Math.abs(blue - getBlue(rgbRef));

        return (redDeviance < 10 && greenDeviance < 10 && blueDeviance < 10);
    }

    public static int getRGB(int red, int green, int blue, int alpha) {
        return ((alpha << 24) + (red << 16) + (green << 8) + blue);
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xff;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xff;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xff;
    }

    public static void main(String[] args) {

        System.out.println(getRGB(250, 20, 2, 0));
    }
}
