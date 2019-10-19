package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.virus.agobot.Agobot;
import org.virus.paths.Arc;
import org.virus.paths.Line;
import org.virus.paths.Path;
import org.virus.paths.PathComponent;
import org.virus.util.PIDController;

import java.util.ArrayList;

@Autonomous(name="PID Tester", group="TeleOp")

public class PIDTester extends LinearOpMode {

    int trials = 3;

    @Override
    public void runOpMode() throws InterruptedException {
        PathComponent[] pathComponents=
                {
//                        new Line(30, Path.INCH),
                        new Arc(25, 180, Path.INCH),
                };
        Path path = new Path(pathComponents, .05f, .0006f, .35f, .05f);
        //creates continuous path
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        waitForStart();

        ArrayList<Float> values = new ArrayList<Float>();
        float smallIncrement = 0.001f;
        float bigIncrement = 0.005f;
        float currentValue = 0;
        boolean showResults = false;
        ArrayList<Double> times = new ArrayList<Double>();
        int leastIndex = 0;
        float[] allPIDCalcP = {0.024f/*,0.013333333f, 0.008f*/, 0.028f};
        float[] allPIDCalcI = {0.072289f/*,0.040f, 0.024096f*/, 0.1054f};
        float[] allPIDCalcD = {0.001992f/*, 0.00295f, 0.00177f*/,  0.002788f};

        while(opModeIsActive()){

            //dpad for big increments
            if(gamepad1.dpad_up){
                currentValue+= bigIncrement;
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_up );
            }
            if(gamepad1.dpad_down){
                currentValue-= bigIncrement;
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_down);
            }
            //bumpers for small increments
            if(gamepad1.right_bumper){
                currentValue+= smallIncrement;
                telemetry.update();
                while(opModeIsActive() && gamepad1.right_bumper);
            }
            if(gamepad1.left_bumper){
                currentValue-= smallIncrement;
                telemetry.update();
                while(opModeIsActive() && gamepad1.left_bumper);
            }
            //b to save value
            if(gamepad1.b){
                values.add(currentValue);
                telemetry.update();
                while(opModeIsActive() && gamepad1.b);
            }
            //a to start testing
            if(gamepad1.a){
                //start testing
                //values.clear();
                //values.add(allPIDCalcP[0]);
                //values.add(allPIDCalcP[1]);
                //values.add(allPIDCalcP[2]);
                //values.add(allPIDCalcP[3]);

                showResults = true;
                ElapsedTime clock = new ElapsedTime();
                double[] alltrials = new double[trials];
                int counter = 0;
                for(float value: values){
                    for(int i = 0; opModeIsActive() && i < trials; i++) {
                        telemetry.addData("Current P", value);
                        telemetry.update();
                        clock.reset();
                        resetRobot();
                        PIDController testPID = new PIDController(value, allPIDCalcI[counter], allPIDCalcD[counter]);
                        testPID.start();
                        while (opModeIsActive() && !Agobot.drivetrain.movePath(path, testPID)) {
                            telemetry.addData("Current Time", clock.seconds());
                            telemetry.update();
                        }
//                    Agobot.drivetrain
                        Double timeToAdd = clock.seconds();
                        telemetry.addData("Total Time", timeToAdd);
                        telemetry.update();
                        alltrials[i] = timeToAdd;
                        while (opModeIsActive() && !gamepad1.x) ;
                    }
                    times.add(calculateAverage(alltrials));
                    while(opModeIsActive() && !gamepad1.x);
                    counter++;
                }
                leastIndex = findLeast(times);
            }
            if(showResults){
                telemetry.addData("P Values", values);
                telemetry.addData("Times", times);
                telemetry.addData("Best P Value", values.get(leastIndex));
                telemetry.addData("Best P Value Time", times.get(leastIndex));
            }
            else {
                telemetry.addData("current P value", currentValue);
                telemetry.addData("all P values", values);
            }
            telemetry.update();
        }

    }

    private void resetRobot(){
        Agobot.drivetrain.resetOrientation();
        Agobot.drivetrain.resetAllEncoders();
        Agobot.drivetrain.setAllRunUsingEncoders();
    }

    //given list of trials for one p value
    //return average
    private double calculateAverage(double[] array){
        double sum = 0;
        for(double d: array){
            sum+=d;
        }

        return sum/((double) array.length);
    }

    //given list of times
    //return index of least time
    private int findLeast(ArrayList<Double> a){
        if(a.size() == 1)
            return 0;
        int least = 0;
        for(int i = 1; opModeIsActive() && i < a.size(); i++){
            if(a.get(i) < least){
                least = i;
            }
        }

        return least;
    }
}