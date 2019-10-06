package org.virus.util;
import java.util.Collection;

public class Three_Tuple<T1, T2, T3> {

    private T1 first;
    private T2 second;
    private T3 third;

    Three_Tuple(T1 newFirst, T2 newSecond, T3 newThird) {

        first = newFirst;
        second = newSecond;
        third = newThird;
    }

    public T1 get1() {

        return first;
    }

    public T2 get2() {

        return second;
    }

    public T3 get3() {

        return third;
    }

    public void setT1(T1 newFirst) {

        first = newFirst;
    }

    public void setT2(T2 newSecond) {

        second = newSecond;
    }

    public void setT3(T3 newThird) {

        third = newThird;
    }
}
