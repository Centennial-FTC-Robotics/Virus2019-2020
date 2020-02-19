package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.virus.agobot.Agobot;
import org.virus.paths.Arc;
import org.virus.paths.Line;
import org.virus.paths.Path;
import org.virus.paths.PathComponent;
import org.virus.util.PIDController;
import org.virus.util.Vector2D;

import java.util.ArrayList;

//@Autonomous(name="PID Tester", group="TeleOp")

public class PIDTester extends LinearOpMode {

    int trials = 4;
    double distance = 24;
    int direction = 1;
    double startHeading = 90;

    @Override
    public void runOpMode() throws InterruptedException {

        PathComponent[] pathComponents=
                {
//                        new Line(30, PurePursuitPath.INCH),
                        new Arc(40, 180, Path.INCH),
                };
        Path path = new Path(pathComponents, .05f, .0006f, .35f, .05f);
        //creates continuous path
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        Agobot.drivetrain.odometry.setStartLocation(new Vector2D(0,0), 90);

        waitForStart();

        ArrayList<Float> values = new ArrayList<Float>();
        values.add(0.4f);
        float p = -.04f;
        float i = 0f;
        float d = 0f;
        float smallIncrement = 0.001f;
        float bigIncrement = 0.005f;
        float currentValue = 0;
        boolean showResults = false;
        ArrayList<Double> times = new ArrayList<Double>();
        int leastIndex = 0;
        float[] allPIDCalcP = {0.024f/*,0.013333333f, 0.008f*/, 0.028f};
        float[] allPIDCalcI = {0.072289f/*,0.040f, 0.024096f*/, 0.1054f};
        float[] allPIDCalcD = {0.001992f/*, 0.00295f, 0.00177f*/,  0.002788f};

        showResults = true;
        ElapsedTime clock = new ElapsedTime();
        double[] alltrials = new double[trials];
        for(float value: values){
            Agobot.drivetrain.headingController = new PIDController(p, i, d,0.20f);
            for(int j = 0; opModeIsActive() && j < trials; j++) {

                clock.reset();
                //resetRobot();
//                PIDController testPID = new PIDController(value, 0, 0);
//                testPID.start();

                Vector2D newPosition = new Vector2D(0,0);
//                if (j % 2 == 0) {
//                    newPosition = new Vector2D(distance, 0);
//                } else {
//                    newPosition = new Vector2D(0, 0);
//                }
                double newHeading = 90;
                if (j % 2 == 0) {
                    newHeading = -90;
                } else {
                    newHeading = 90;
                }
                while (opModeIsActive() && Agobot.drivetrain.goToPosition(newPosition, newHeading, 0.65)) {
                    telemetry.addData("Current P", value);
                    telemetry.addData("Current Time", clock.seconds());
                    telemetry.update();
                }
                Double timeToAdd = clock.seconds();
                telemetry.addData("Total Time", timeToAdd);
                telemetry.update();
                alltrials[j] = timeToAdd;
            }
            times.add(calculateAverage(alltrials));

        }
        leastIndex = findLeast(times);
        if(showResults){
            telemetry.addData("P Values", values);
            telemetry.addData("Times", times);
            telemetry.addData("Best P Value", values.get(leastIndex));
            telemetry.addData("Best P Value Time", times.get(leastIndex));
        } else {
            telemetry.addData("current P value", currentValue);
            telemetry.addData("all P values", values);
        }
        telemetry.update();
        while(opModeIsActive());




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
        for(int i = 0; opModeIsActive() && i < a.size(); i++){
            if(a.get(i) < least){
                least = i;
            }
        }

        return least;
    }
}