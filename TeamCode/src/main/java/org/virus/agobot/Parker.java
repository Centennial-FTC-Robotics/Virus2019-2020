package org.virus.agobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.virus.superclasses.Subsystem;

public class Parker extends Subsystem {
    Servo parkingServo;

    @Override
    public void initialize(LinearOpMode opMode) {
        parkingServo = opMode.hardwareMap.servo.get("parkingServo");
        parkingServo.setPosition(0);
    }

    public void extend(boolean out){

        if(out){
            parkingServo.setPosition(1);
        }else{
            parkingServo.setPosition(0);
        }
    }

    public boolean isParkingExtended(){
        return (parkingServo.getPosition() == 1);
    }

}
