package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.virus.example.ExampleBot;
import org.virus.paths.Arc;
import org.virus.paths.Line;
import org.virus.paths.Path;
import org.virus.paths.PathComponent;
import org.virus.util.PIDController;

import java.util.ArrayList;

@Autonomous(name="PID Tester", group="TeleOp")

public class PIDTester extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        PathComponent[] pathComponents=
                {
//                        new Line(30, Path.INCH),
                        new Arc(20, 180, Path.INCH),
                };
        Path path = new Path(pathComponents, .05f, .0006f, .35f, .05f);
        //creates continuous path
        ExampleBot.initialize(this);
        //inits all hardware
        ExampleBot.drivetrain.initializeIMU();
        waitForStart();

        ArrayList<Float> values = new ArrayList<Float>();
        float smallIncrement = 0.001f;
        float bigIncrement = 0.005f;
        float currentValue = 0;
        boolean showResults = false;
        ArrayList<Double> times = new ArrayList<Double>();

        while(opModeIsActive()){

            //dpad for small increments
            if(gamepad1.dpad_up){
                currentValue+= bigIncrement;
                telemetry.update();
                while (gamepad1.dpad_up && opModeIsActive());
            }
            if(gamepad1.dpad_down){
                currentValue-= bigIncrement;
                telemetry.update();
                while (gamepad1.dpad_down && opModeIsActive());
            }
            //joystick for big jumps
            if(gamepad1.right_bumper){
                currentValue+= smallIncrement;
                telemetry.update();
                while(gamepad1.right_bumper);
            }
            if(gamepad1.left_bumper){
                currentValue-= smallIncrement;
                telemetry.update();
                while(gamepad1.left_bumper);
            }
            //b to save value
            if(gamepad1.b){
                values.add(currentValue);
                telemetry.update();
                while(gamepad1.b && opModeIsActive());
            }
            //a to start or continue
            if(gamepad1.a){
                //start testing
                showResults = true;
                ElapsedTime clock = new ElapsedTime();
                for(float value: values){
                    telemetry.addData("Current P", value);
                    telemetry.update();
                    clock.reset();
                    ExampleBot.drivetrain.resetOrientation();
                    ExampleBot.drivetrain.resetAllEncoders();
                    ExampleBot.drivetrain.setAllRunUsingEncoders();
                    PIDController testPID = new PIDController(value, 0, 0);
                    testPID.start();
                    while (!ExampleBot.drivetrain.movePath(path, testPID) && opModeIsActive()){
                        telemetry.addData("Current Time",clock.seconds());
                        telemetry.update();
                    }
//                    ExampleBot.drivetrain
                    Double timeToAdd = clock.seconds();
                    telemetry.addData("Total Time", timeToAdd);
                    telemetry.update();
                    times.add(timeToAdd);
                    while(!gamepad1.x && opModeIsActive());
                }
            }
            if(showResults){
                telemetry.addData("P Values", values);
                telemetry.addData("Times", times);
            }
            else {
                telemetry.addData("current P value", currentValue);
                telemetry.addData("all P values", values);
            }
            telemetry.update();
        }

        //ExampleBot.horizontalSlides.move(500);
        //move horizontal slides
        //while(true&&opModeIsActive()){
        //  ExampleBot.drivetrain.moveLeftTest();
        //}
/*
        while (!ExampleBot.drivetrain.movePath(path)) { //moves the robot along the path, while loop ends when path is complete (move method returns false)
            //ExampleBot.verticalSlides.move(200); //moves the vertical slides while the robot is following the path
        }

        while (ExampleBot.drivetrain.pivot(90f, .5f)); //turns the robot another 90 degrees
 */

    }

    private double findLeast(ArrayList<Double> a){
        if(a.size() == 1)
            return a.get(0);
        double least = a.get(0);
        for(int i = 1; i < a.size(); i++){
            if(a.get(i) < least){
                least = a.get(i);
            }
        }

        return least;
    }

//    private void startTester(ArrayList<Float> values, Path path){
//        ElapsedTime clock = new ElapsedTime();
//        ArrayList<Double> times = new ArrayList<Double>();
//        for(float value: values){
//            telemetry.addData("Current P", value);
//            telemetry.update();
//            clock.reset();
//            ExampleBot.drivetrain.resetAllEncoders();
//            ExampleBot.drivetrain.setAllRunUsingEncoders();
//            PIDController testPID = new PIDController(value, 0, 0);
//            testPID.start();
//            while (!ExampleBot.drivetrain.movePath(path, testPID) && opModeIsActive()){
//                telemetry.addData("Current Time",clock.seconds());
//                telemetry.update();
//            }
//            times.add(clock.seconds());
//            while(!gamepad1.x && opModeIsActive());
//        }
//
//        telemetry.addData("P Values", values);
//        telemetry.addData("Times", times);
//        telemetry.update();
//    }
}