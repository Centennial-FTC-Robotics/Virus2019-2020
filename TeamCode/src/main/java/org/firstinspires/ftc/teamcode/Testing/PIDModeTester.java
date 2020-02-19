package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.virus.agobot.Agobot;
import org.virus.paths.Arc;
import org.virus.paths.Path;
import org.virus.paths.PathComponent;
import org.virus.util.PIDController;

import java.lang.reflect.Array;
import java.util.ArrayList;

//@TeleOp(name="PID Mode Tester", group="TeleOp")

public class PIDModeTester extends LinearOpMode {

    public static enum modes {P, PI, PD, Classic_PID, Pessen_Integral_Rule, Some_Overshoot, No_Overshoot};

    @Override
    public void runOpMode() throws InterruptedException{
        PathComponent[] pathComponents=
                {
//                        new Line(30, Path.INCH),
                        new Arc(40, 180, Path.INCH),
                };
        Path path = new Path(pathComponents, .05f, .0006f, .35f, .05f);
        //creates continuous path
        Agobot.initialize(this);
        //inits all hardware
        Agobot.drivetrain.initializeIMU();
        waitForStart();

        int trials = 4;
        int modePointer = 0;
        double Ku = 0.4;
        double Tu = 0.45;

        double[][] proportions = {
                {0.5, 0, 0},
                {0.45, 0.54, 0},
                {0.8, 0, 1.0 / 10.0},
                {0.6, 1.2, (3.0 / 40.0)},
                {(7.0 / 10.0), 1.75, (21.0 / 200.0)},
                {(1.0 / 3.0), (2.0/ 3.0), (1.0 / 9.0)},
                {(1.0 / 5.0), (2.0 / 5.0), (1.0 / 15.0)}
        };

        ArrayList<Double> times = new ArrayList<Double>();
        ArrayList<Double> overshoots = new ArrayList<Double>();

        while(opModeIsActive()){

            if(gamepad1.dpad_up){
                modePointer++;
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_up );
            }
            if(gamepad1.dpad_down){
                modePointer--;
                telemetry.update();
                while (opModeIsActive() && gamepad1.dpad_down);
            }

            double[] alltrials = new double[trials];
            double[] trialOvershoots = new double[trials];

            modes mode = modes.values()[Math.abs(modePointer) % modes.values().length];

            if (gamepad1.a) {
                double[] PIDVal = new double[3];

                PIDVal[0] = proportions[modePointer % modes.values().length][0] * Ku;
                PIDVal[1] = proportions[modePointer % modes.values().length][1] * (Ku / Tu);
                PIDVal[2] = proportions[modePointer % modes.values().length][2] * (Ku * Tu);

                ElapsedTime clock = new ElapsedTime();
                alltrials = new double[trials];
                trialOvershoots = new double[trials];

                for(int i = 0; opModeIsActive() && i < 4; i++) {
                    resetRobot();
                    PIDController testPID = new PIDController((float) PIDVal[0],(float) PIDVal[1],(float) PIDVal[2]);
                    clock.reset();
                    testPID.start();
                    while (opModeIsActive() && !Agobot.drivetrain.movePath(path, testPID)) {
                        telemetry.addData("Current P", PIDVal[0]);
                        telemetry.addData("Current I", PIDVal[1]);
                        telemetry.addData("Current D", PIDVal[2]);
                        telemetry.addData("Current Time", clock.seconds());
                        telemetry.update();
                    }
//                    Agobot.drivetrain
                    Double timeToAdd = clock.seconds();
                    telemetry.addData("Current P", PIDVal[0]);
                    telemetry.addData("Current I", PIDVal[1]);
                    telemetry.addData("Current D", PIDVal[2]);
                    telemetry.addData("Total Time", timeToAdd);
                    telemetry.update();
                    alltrials[i] = timeToAdd;
                    PathComponent lastComponent = pathComponents[pathComponents.length - 1];
                    trialOvershoots[i] = Math.abs(path.getHeading(path.getDistance()) - Agobot.drivetrain.updateOrientation().firstAngle);
                    while (opModeIsActive() && !gamepad1.x) ;
                }
                times.add(calculateAverage(alltrials));
                overshoots.add(calculateAverage(trialOvershoots));
                while(opModeIsActive() && !gamepad1.x);
            }
            telemetry.addData("Current Mode: ", mode.name());
            telemetry.addData("All trial times:", alltrials.toString());
            telemetry.addData("All trial overshoots:", trialOvershoots.toString());
            telemetry.addData("overshoots", overshoots);
            telemetry.addData("Times", times);
            telemetry.update();
        }
    }

    private void resetRobot(){
        Agobot.drivetrain.resetOrientation();
        Agobot.drivetrain.resetAllEncoders();
        Agobot.drivetrain.setAllRunUsingEncoders();
    }

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
