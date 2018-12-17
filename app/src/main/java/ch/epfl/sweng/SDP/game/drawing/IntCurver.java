package ch.epfl.sweng.SDP.game.drawing;

import java.util.Arrays;

/**
 * This class helps smoothing new values by remembering older values and returning an average.
 */
class IntCurver {

    private int index;
    private int curveIntensity;
    private int sum;
    private int[] values;

    /**
     * Constructor initializes the array with a given size and fills it with a given value.
     *
     * @param curveIntensity amount of values that should be remembered
     * @param initValue      initial value of the curver
     */
    IntCurver(int curveIntensity, int initValue) {
        this.curveIntensity = curveIntensity;

        values = new int[curveIntensity];
        Arrays.fill(values, initValue);

        sum = curveIntensity * initValue;
        index = 0;
    }

    /**
     * Resets the values to a given point.
     */
    void setValue(int value) {
        Arrays.fill(values, value);
    }

    /**
     * Adds a value to the list of entries.
     */
    void addValue(int value) {
        sum += value - values[index];
        values[index] = value;
        index = (index + 1) % curveIntensity;
    }

    /**
     * Returns the average of the stored values.
     */
    int getValue() {
        return sum / curveIntensity;
    }
}
