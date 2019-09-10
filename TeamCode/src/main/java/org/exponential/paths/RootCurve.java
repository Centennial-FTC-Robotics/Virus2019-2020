package org.exponential.paths;

public class RootCurve extends MotionCurve {
     private float accel;
     private float deaccel;
    private float bias;
    //gen motion curves based on max accel

    RootCurve(float a1, float a2, float max, int distance, float minBias){
        maxVal=max;
        length=distance;
        accel = a1;
        deaccel = a2;
        bias = minBias;
    }


    @Override
    public float getValue(float x) {
        if(x < 1/accel){
            float val = (float) Math.sqrt(accel * x);
            if(val > bias){
                return val;
            }
            return bias;

        }
        else if(x < length - 1/deaccel){
            return  maxVal;
        }
        else if (x<length){
           // if(maxVal - x * slope > bias){
            //float downSlope = maxVal/ (length - (maxVal/upSlope + upperLength));

            return  (float) Math.sqrt(deaccel * (length - x));
          //  }

            //return bias;
        }
        else{
            return -(float) Math.sqrt(-(length - x));
        }
    }
}
