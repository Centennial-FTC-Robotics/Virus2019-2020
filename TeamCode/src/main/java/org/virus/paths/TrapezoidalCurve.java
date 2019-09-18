package org.virus.paths;

public class TrapezoidalCurve extends MotionCurve {
     private float upperLength;
     private float upSlope;
     private float bias;
    //gen motion curves based on max accel

    TrapezoidalCurve(float b1, float b2, float h, float minBias){
        maxVal=h;
        length=b1;
        upperLength=b2;
        float slopeLength=(length-upperLength)/2f;
        upSlope = maxVal/slopeLength;
        bias = minBias;
    }

    TrapezoidalCurve(float upSlope, float downSlope, int distance, float max, float minBias){
        this.upSlope = upSlope;
        maxVal = max;
        length = distance;
        upperLength = length - (maxVal/upSlope) - (maxVal/downSlope);
        bias = minBias;
    }

    TrapezoidalCurve(float b1, float b2, float h, int leftRight){
        maxVal=h;
        length=b1;
        float slopeLength=(length-upperLength)/2f;
        upSlope = maxVal/slopeLength;
        //wip code for left/right centered trapezoidal curves
    }


    @Override
    public float getValue(float x) {
        if(x < maxVal/upSlope){
            if(x * upSlope > bias){
                return x * upSlope;
            }
            return bias;

        }
        else if(x < maxVal/upSlope + upperLength){
            return  maxVal;
        }
        else{
           // if(maxVal - x * slope > bias){
            float downSlope = maxVal/ (length - (maxVal/upSlope + upperLength));

            return  maxVal - (downSlope * (x - (maxVal/upSlope + upperLength)));
          //  }

            //return bias;
        }
    }
}
