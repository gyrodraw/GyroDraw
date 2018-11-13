package ch.epfl.sweng.SDP.utils;

public class AnimUtils {

    private static final int MAIN_FREQUENCY = 10;
    private static final double MAIN_AMPLITUDE = 0.1;

    private AnimUtils() {}

    public static int getMainFrequency() {
        return MAIN_FREQUENCY;
    }

    public static double getMainAmplitude() {
        return MAIN_AMPLITUDE;
    }
}
