package org.virus.vision;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.Arrays;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;


public class StripDetector {
    OpenCvCamera phoneCam;
    OpMode opMode;

    private double distFromCenter = 0;
    private int relativePosIndex = 1;

    public void initialize(OpMode opMode) {
        this.opMode=opMode;
        int cameraMonitorViewId = opMode.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", opMode.hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View
        //phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK);

        /*
         * Open the connection to the camera device
         */
        phoneCam.openCameraDevice();

        /*
         * Specify the image processing pipeline we wish to invoke upon receipt
         * of a frame from the camera. Note that switching pipelines on-the-fly
         * (while a streaming session is in flight) *IS* supported.
         */
        phoneCam.setPipeline(new SamplePipeline());

        /*
         * Tell the camera to start streaming images to us! Note that you must make sure
         * the resolution you specify is supported by the camera. If it is not, an exception
         * will be thrown.
         *
         * Also, we specify the rotation that the camera is used in. This is so that the image
         * from the camera sensor can be rotated such that it is always displayed with the image upright.
         * For a front facing camera, rotation is defined assuming the user is looking at the screen.
         * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
         * away from the user.
         */
        activate();
    }

    public void activate() {

        phoneCam.startStreaming(1280, 720, OpenCvCameraRotation.SIDEWAYS_LEFT);
    }

    public void deactivate() {

        phoneCam.stopStreaming();
    }

    public double getCurrentDist() {

        return distFromCenter;
    }

    public String relativePos() {

        String[] relPos = {"Right", "Left", "Middle"};

        return relPos[relativePosIndex];
    }

    class SamplePipeline extends OpenCvPipeline {
        /*
         * NOTE: if you wish to use additional Mat objects in your processing pipeline, it is
         * highly recommended to declare them here as instance variables and re-use them for
         * each invocation of processFrame(), rather than declaring them as new local variables
         * each time through processFrame(). This removes the danger of causing a memory leak
         * by forgetting to call mat.release(), and it also reduces memory pressure by not
         * constantly allocating and freeing large chunks of memory.
         */
        Rect rectCrop;
        Rect rectUnCrop;
        Mat cropped = new Mat();
        Mat unCropped = new Mat();
        Mat gray=new Mat();
        Mat thresh = new Mat();
        Mat rectThresh = new Mat();

        @Override
        public Mat processFrame(Mat input)
        {
            rectCrop = new Rect(new Point(130,400) , new Point(1030,550));
            //Rect regStoneCrop = new Rect(new Point(0, 300), new Point(300, 600));
            cropped = new Mat(input, rectCrop);

            Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);
            Mat croppedGrayScale = new Mat(gray, rectCrop);

            int index = -1;
            double segAvgMin = Double.MAX_VALUE;

            for (int s = 0; s < 3; s++) {

                double segmentAverage = 0;

                for (int i = (300 * s); i < (300 * s) + 300; i++) {
                    for (int j = 0; j < croppedGrayScale.rows(); j++) {

                        segmentAverage += croppedGrayScale.get(j,i)[0];
                    }
                }

                double segAvg = segmentAverage / (300 * croppedGrayScale.cols());

                if (segAvg < segAvgMin) {

                    segAvgMin = segAvg;
                    index = s;
                }
            }

            relativePosIndex = index;
//            double skystoneAverage = 0;
//
//            for (int i = 0; i < croppedGrayScale.rows(); i++) {
//                for (int j = 0; j < croppedGrayScale.cols(); j++) {
//
//                    skystoneAverage += croppedGrayScale.get(i, j)[0];
//                }
//            }
//
//            skystoneAverage /= (croppedGrayScale.cols() * croppedGrayScale.rows());
//
//
//            Mat croppedGrayScaleStone = new Mat(gray, regStoneCrop);
//
//            double stoneAverage = 0;
//
//            for (int i = 0; i < croppedGrayScaleStone.rows(); i++) {
//                for (int j = 0; j < croppedGrayScaleStone.cols(); j++) {
//
//                    stoneAverage += croppedGrayScaleStone.get(i, j)[0];
//                }
//            }
//
//            stoneAverage /= (croppedGrayScaleStone.cols() * croppedGrayScaleStone.rows());
//
//            opMode.telemetry.addData("Skystone Strip Average", skystoneAverage);
//            opMode.telemetry.addData("Stone, Strip Average", stoneAverage);

//            Imgproc.threshold(gray, thresh, segAvgMin, 255, THRESH_BINARY_INV);
//            cropped = new Mat(thresh, rectCrop);
//
//            double sum = 0;
//            double total = 0;
//
//            for(int i=0; i<cropped.rows(); i++){
//                for(int j=0; j<cropped.cols(); j++){
//                    if(cropped.get(i,j)[0]!=0){
//                        sum+=(1*j-cropped.cols()/2);
//                        total++;
//                    }
//                }
//            }
//
//            double weightedAvg = sum/(double)(total);
//            opMode.telemetry.addData("weighted skystone Average",weightedAvg);
//            opMode.telemetry.update();
//
//            distFromCenter = weightedAvg;

            Imgproc.rectangle(input, rectCrop, new Scalar(128), 3);
            //Imgproc.rectangle(thresh, regStoneCrop, new Scalar(128), 3);
            //rectUnCrop = new Rect(new Point(0,0) , new Point(319,239));
            //unCropped = new Mat(thresh, rectUnCrop);


            //cropped= thresh.submat(rectCrop);

            //Core.extractChannel(hsv, v, 2);
            /*
             * IMPORTANT NOTE: the input Mat that is passed in as a parameter to this method
             * will only dereference to the same image for the duration of this particular
             * invocation of this method. That is, if for some reason you'd like to save a copy
             * of this particular frame for later use, you will need to either clone it or copy
             * it to another Mat.
             */

            /*
             * Draw a simple box around the middle 1/2 of the entire frame
             */

            /**
             * NOTE: to see how to get data from your pipeline to your OpMode as well as how
             * to change which stage of the pipeline is rendered to the viewport when it is
             * tapped, please see {@link PipelineStageSwitchingExample}
             */

            return input;
        }
    }
}
