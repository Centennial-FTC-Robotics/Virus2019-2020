package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.util.Range;

import org.virus.agobot.Agobot;
import org.virus.util.Pair;
import org.virus.util.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;

public class FieldCentricTesting {

    public static Vector2D leftStick;
    public static Vector2D rightStick;
    public static Vector2D motorSpeeds;

    public static void main(String[] args) {

        singleInput();
    }

    public void multiInputSet() {

        String[] inputSetNames = new String[] {"Steer", "noSteer"};
        leftStick = new Vector2D((double) 0, (double) 1);
        rightStick = new Vector2D((double) -1, (double) 0);
        Pair<Vector2D, Vector2D> steer = new Pair<Vector2D, Vector2D>(leftStick, rightStick);
        leftStick = new Vector2D((double) 0, (double) 1);
        rightStick = new Vector2D((double) 0, (double) 0);
        Pair<Vector2D, Vector2D> noSteer = new Pair<Vector2D, Vector2D>(leftStick, rightStick);
        ArrayList<Pair<Vector2D, Vector2D>> inputs = new ArrayList<Pair<Vector2D, Vector2D>>();
        inputs.add(steer);
        inputs.add(noSteer);

        double[] headings = new double[] {120, 135, 150};

        for (int i = 0; i < inputs.size(); i++) {
            System.out.println(inputSetNames[i]);
            Pair<Vector2D, Vector2D> a = inputs.get(i);

            for (int h = 0; h < headings.length; h++) {
                System.out.println("Heading: " + headings[h]);
                a.get1().rotate(-Math.toRadians(headings[h]));

                double leftx = -a.get1().getComponent(0);
                double lefty = a.get1().getComponent(1);
                double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
                double magnitude = Math.sqrt(Math.pow(lefty, 2) + Math.pow(leftx, 2));

                motorSpeeds = new Vector2D((lefty-leftx)*magnitude/scalar, (lefty+leftx)*magnitude/scalar);

                double diagSpeed1 = motorSpeeds.getComponent(0);
                double diagSpeed2 = motorSpeeds.getComponent(1);

                if ((a.get1().getComponent(0) != 0) || (a.get1().getComponent(1) != 0)){
                    runMotors(diagSpeed1, diagSpeed2, diagSpeed2, diagSpeed1, a.get2().getComponent(0)); //var1 and 2 are computed values found in theUpdateControllerValues method
                } else {
                    runMotors(0, 0, 0, 0, a.get2().getComponent(0));
                }
                System.out.println();
            }
        }
    }

    public static void singleInput() {

        leftStick = new Vector2D((double) 0, (double) 1);
        rightStick = new Vector2D((double) -1, (double) 0);
        double heading = 150;
        System.out.println("Heading: " + heading);
        leftStick.rotate(-Math.toRadians(heading));

        double leftx = -leftStick.getComponent(0);
        double lefty = leftStick.getComponent(1);
        double scalar = Math.max(Math.abs(lefty-leftx), Math.abs(lefty+leftx)); //scalar and magnitude scale the motor powers based on distance from joystick origin
        double magnitude = Math.sqrt(Math.pow(lefty, 2) + Math.pow(leftx, 2));

        motorSpeeds = new Vector2D((lefty-leftx)*magnitude/scalar, (lefty+leftx)*magnitude/scalar);

        double diagSpeed1 = motorSpeeds.getComponent(0);
        double diagSpeed2 = motorSpeeds.getComponent(1);

        if ((leftStick.getComponent(0) != 0) || (leftStick.getComponent(1) != 0)){
            runMotors(diagSpeed1, diagSpeed2, diagSpeed2, diagSpeed1, rightStick.getComponent(0)); //var1 and 2 are computed values found in theUpdateControllerValues method
        } else {
            runMotors(0, 0, 0, 0, rightStick.getComponent(0));
        }
        System.out.println();
    }

    public static void runMotors(double Left0, double Left1, double Right0, double Right1, double steerMagnitude){

        double maxPower = 1;
//        System.out.println("Init SteerMag: " + steerMagnitude);
//        if (Math.abs(Left0) > 0.01 && Math.abs(Left1) > 0.01 && Math.abs(Right0) > 0.01 && Math.abs(Right1) > 0.01) {
//            steerMagnitude *= 2 * Math.max(Math.max(Left0, Left1), Math.max(Right0, Right1));
//        }
        Left0=Left0+steerMagnitude;
        Left1=Left1+steerMagnitude;
        Right0=Right0-steerMagnitude;
        Right1=Right1-steerMagnitude;
        //make sure no exception thrown if power > 0
        Left0 = Range.clip(Left0, -maxPower, maxPower);
        Left1 = Range.clip(Left1, -maxPower, maxPower);
        Right0 = Range.clip(Right0, -maxPower, maxPower);
        Right1 = Range.clip(Right1, -maxPower, maxPower);

        System.out.println("rBack: " + Right1);
        System.out.println("rFront: " + Right0);
        System.out.println("lBack: " + Left1);
        System.out.println("lFront: " + Left0);
        System.out.println("Steer Magnitude: " + steerMagnitude);
    }
}
