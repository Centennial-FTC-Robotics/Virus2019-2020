package org.virus.agobot;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.virus.superclasses.Subsystem;
import org.virus.util.Vector2D;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.INTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

public class ElementLocator extends Subsystem {

    // constants for positioning of assets
    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Constant for Stone Target
    private static final float stoneY = 2.00f * mmPerInch;

    // Constants for the center support targets
    private static final float bridgeZ = 6.42f * mmPerInch;
    private static final float bridgeY = 23 * mmPerInch;
    private static final float bridgeX = 5.18f * mmPerInch;
    private static final float bridgeRotY = 59;                                 // Units are degrees
    private static final float bridgeRotZ = 180;

    // Constants for perimeter targets
    private static final float halfField = 72 * mmPerInch;
    private static final float quadField  = 36 * mmPerInch;

    //TODO: Change these in response to our build!
    final float CAMERA_FORWARD_DISPLACEMENT  = 0;   // eg: Camera is -2 Inches in front of robot center
    final float CAMERA_VERTICAL_DISPLACEMENT = 0;   // eg: Camera is 3 Inches above ground
    final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

    private LinearOpMode opModeReference;
    VuforiaLocalizer.Parameters parameters;
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private VuforiaTrackables targetsSkyStone;
    List<VuforiaTrackable> allTrackables;
    List<VectorF> reportedLocations;

    private boolean targetVisible = false;
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    private static final boolean PHONE_IS_PORTRAIT = false;
    private float phoneXRotate = 0; // robot initially facing (positive?) y direction, so the phone is rotated on a perpendicular axis, the x axis and thus , 90 degrees to face forward
    private float phoneYRotate = 0;
    private float phoneZRotate = 0;

    // vision initialization stuff
    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";
    private static final String VUFORIA_KEY =
            "AQmuIUP/////AAAAGR6dNDzwEU07h7tcmZJ6YVoz5iaF8njoWsXQT5HnCiI/oFwiFmt4HHTLtLcEhHCU5ynokJgYSvbI32dfC2rOvqmw81MMzknAwxKxMitf8moiK62jdqxNGADODm/SUvu5a5XrAnzc7seCtD2/d5bAIv1ZuseHcK+oInFHZTi+3BvhbUyYNvnVb0tQEAv8oimzjiQW18dSUcEcB/d6QNGDvaDHpxuRCJXt8U3ShJfBWWQEex0Vp6rrb011z8KxU+dRMvGjaIy+P2p5GbWXGJn/yJS9oxuwDn3zU6kcQoAwI7mUgAw5zBGxxM+P35DoDqiOja6ST6HzDszHxClBm2dvTRP7C4DEj0gPkhX3LtBgdolt";

    public void initialize(LinearOpMode opMode) {

        opModeReference = opMode;

        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            opModeReference.telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        int cameraMonitorViewId = opModeReference.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", opModeReference.hardwareMap.appContext.getPackageName());
        parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        initTrackables();
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = opModeReference.hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", opModeReference.hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }

    private void initTrackables() {
        targetsSkyStone = vuforia.loadTrackablesFromAsset("Skystone");

        VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
        stoneTarget.setName("Stone Target");
        VuforiaTrackable blueRearBridge = targetsSkyStone.get(1);
        blueRearBridge.setName("Blue Rear Bridge");
        VuforiaTrackable redRearBridge = targetsSkyStone.get(2);
        redRearBridge.setName("Red Rear Bridge");
        VuforiaTrackable redFrontBridge = targetsSkyStone.get(3);
        redFrontBridge.setName("Red Front Bridge");
        VuforiaTrackable blueFrontBridge = targetsSkyStone.get(4);
        blueFrontBridge.setName("Blue Front Bridge");
        VuforiaTrackable red1 = targetsSkyStone.get(5);
        red1.setName("Red Perimeter 1");
        VuforiaTrackable red2 = targetsSkyStone.get(6);
        red2.setName("Red Perimeter 2");
        VuforiaTrackable front1 = targetsSkyStone.get(7);
        front1.setName("Front Perimeter 1");
        VuforiaTrackable front2 = targetsSkyStone.get(8);
        front2.setName("Front Perimeter 2");
        VuforiaTrackable blue1 = targetsSkyStone.get(9);
        blue1.setName("Blue Perimeter 1");
        VuforiaTrackable blue2 = targetsSkyStone.get(10);
        blue2.setName("Blue Perimeter 2");
        VuforiaTrackable rear1 = targetsSkyStone.get(11);
        rear1.setName("Rear Perimeter 1");
        VuforiaTrackable rear2 = targetsSkyStone.get(12);
        rear2.setName("Rear Perimeter 2");

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);

        reportedLocations = new ArrayList<VectorF>();

        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, stoneY)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //Set the position of the bridge support targets with relation to origin (center of field)
        blueFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, bridgeRotZ)));

        blueRearBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, bridgeRotZ)));

        redFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, 0)));

        redRearBridge.setLocation(OpenGLMatrix
                .translation(bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, 0)));

        //Set the position of the perimeter targets with relation to origin (center of field)
        red1.setLocation(OpenGLMatrix
                .translation(quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        red2.setLocation(OpenGLMatrix
                .translation(-quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        front1.setLocation(OpenGLMatrix
                .translation(-halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        front2.setLocation(OpenGLMatrix
                .translation(-halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90)));

        blue1.setLocation(OpenGLMatrix
                .translation(-quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        blue2.setLocation(OpenGLMatrix
                .translation(quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        rear1.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));

        rear2.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90 ;
        }

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(INTRINSIC, XYZ, DEGREES, phoneXRotate, phoneYRotate, phoneZRotate));

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

        targetsSkyStone.activate();
    }

    public VuforiaLocalizer getVuforia() {
        return vuforia;
    }

    public TFObjectDetector getTensorFlowObject() {
        return tfod;
    }

    public boolean isSkyStoneVisible() {

        if (opModeReference.opModeIsActive()) {

            if (tfod != null) {

                tfod.activate();
            }

            if (tfod != null) {
                // put good code in here to check if there are skystones
                List<Recognition> elements = tfod.getRecognitions();
//                opModeReference.telemetry.addData("elements", elements);
                if (elements != null) {
                    for (Recognition r : elements) {
                        if (r.getLabel().toUpperCase().equals(LABEL_SECOND_ELEMENT.toUpperCase())) {

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public ArrayList<Vector2D>  getSkyStonePositions() {

        ArrayList<Vector2D> skyStonePositions = new ArrayList<Vector2D>();

        if (opModeReference.opModeIsActive()) {

            if (tfod != null) {
                tfod.activate();
                // put good code in here to check if there are skystones
                List<Recognition> elements = tfod.getRecognitions();
                // TODO: modify to use vuforia trackables and get the positions of the skystones to convert to a vector!

                if (elements != null) {
                    for (Recognition r : elements) {

                        if (r.getLabel().equals(LABEL_SECOND_ELEMENT)) {

                            VectorF robotPos = getRobotPos();

                            double angle = r.estimateAngleToObject(AngleUnit.RADIANS);
                            double distanceToStones = robotPos.getData()[1] - stoneY; // x direction is parallel to alliance walls, y is perpendicular to them
                            double specificStoneDist = distanceToStones / Math.cos(angle);

                            Vector2D relativeStonePos = new Vector2D(angle, specificStoneDist, true);

                            skyStonePositions.add(relativeStonePos);
                        }
                    }
                }
            }
        }

        return skyStonePositions;
    }

    public VectorF getRobotPos() {

        updateRobotPositions();

        VectorF robotPos = null;

        double xTolerance = 10;
        double yTolerance = 10;

        if (reportedLocations.size() > 0) {

            int maxToleranceCount = 0;
            ArrayList<VectorF>  maxWithinTolerance = new ArrayList<VectorF>();

            for (int v = 0; v < reportedLocations.size(); v++) {

                int toleranceCount = 0;
                ArrayList<VectorF> withinTolerance = new ArrayList<VectorF>();
                VectorF currentVector = reportedLocations.get(v);

                for (int c = 0; c < reportedLocations.size(); c++) {
                    if (c != v) {
                        VectorF compared = reportedLocations.get(c);
                        boolean withinX = false;
                        boolean withinY = false;

                        if (Math.abs(compared.getData()[0] - currentVector.getData()[0]) < xTolerance) {

                            withinX = true;
                        }

                        if (Math.abs(compared.getData()[1] - currentVector.getData()[1]) < yTolerance) {

                            withinY = true;
                        }

                        if (withinX && withinY) {

                            withinTolerance.add(compared);
                            toleranceCount++;
                        }
                    }
                }

                if (toleranceCount > maxToleranceCount) {

                    maxToleranceCount = toleranceCount;
                    maxWithinTolerance.clear();
                    maxWithinTolerance.addAll(withinTolerance);
                    maxWithinTolerance.add(currentVector);
                }
            }

            robotPos = new VectorF(0, 0);

            for (VectorF w: maxWithinTolerance) {

                robotPos.add(w);
            }

            robotPos.multiply( (float) (1.0 / maxWithinTolerance.size()));
        }

        return robotPos;
    }

    public void updateRobotPositions() {

        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                //opModeReference.telemetry.addData("Visible Target", trackable.getName());

                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {

//                    int lastLocDataIndex = -1;
//                    for (int r = 0; r < reportedLocations.size(); r++) {
//                        if (reportedLocations.get(r).getName().equals(trackable.getName())) {
//                            lastLocDataIndex = r;
//                        }
//                    }

                    VectorF location = robotLocationTransform.toVector();
                    reportedLocations.add(location);

//                    if (lastLocDataIndex == -1) {
//                        reportedLocations.add(location);
//                    } else {
//
//                        reportedLocations.set(lastLocDataIndex, location);
//                    }
                }
            }

            opModeReference.telemetry.update();
        }
    }

    public OpenGLMatrix[] getRobotLocationTransform() {

        OpenGLMatrix[] locationTransforms = new OpenGLMatrix[allTrackables.size()];

        int t = 0;
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                //opModeReference.telemetry.addData("Visible Target", trackable.getName());

                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
                locationTransforms[t] = robotLocationTransform;
            }

            t++;
        }

        return locationTransforms;
    }
}
