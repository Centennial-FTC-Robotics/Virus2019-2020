package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.virus.superclasses.Subsystem;

public class Arm extends Subsystem {


    public Servo leftArm;
    public Servo rightArm;
    // drop = 0, standby = 1, in = 2
    public String[] armPositions = {"drop", "standby", "in"};
    public String[] extendPositions = {"drop", "topstone", "standby", "in"};
    public int armPosition;
    public double leftPosition = 0.0;
    public double rightPosition = 1.0;
    public boolean topStone = false;
    public boolean prevTopStone = false;
    //in: left = 0.0, right = 1.0
    //standby: left = 0.2, right = 0.8
    //drop: left: 1.0, right = 0.0

    @Override
    public void initialize(LinearOpMode opMode) {
        //TODO
        leftArm = opMode.hardwareMap.servo.get("leftArm");
        rightArm = opMode.hardwareMap.servo.get("rightArm");
        armPosition = 2;
    }

    public void armFlipOut(boolean out){

        topStone = Agobot.slides.getPosition() > (6 * 4 * Agobot.slides.ENCODER_PER_INCH);

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
                    left = 0.93;
                    right = 0.07;
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
                    left = 0.1;
                    right = 0.9;
                    break;
                case 1:
                    //standby
                    left = 0.93;
                    right = 0.07;
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
}
