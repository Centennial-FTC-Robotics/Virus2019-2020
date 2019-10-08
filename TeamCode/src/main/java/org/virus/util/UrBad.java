package org.virus.util;

public class UrBad extends Exception {

    public UrBad(Throwable origin) {

        super("You've committed a bad time. The code that you screwed over is", origin);
    }
}
