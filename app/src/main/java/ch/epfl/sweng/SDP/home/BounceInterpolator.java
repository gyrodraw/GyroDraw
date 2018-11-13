package ch.epfl.sweng.SDP.home;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

class BounceInterpolator implements android.view.animation.Interpolator {

    private final double amplitude;
    private final double frequency;

    BounceInterpolator(double amplitude, double frequency) {
        checkPrecondition(amplitude != 0,
                "Amplitude should be different from 0");

        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public float getInterpolation(float time) {
        checkPrecondition(0 <= time && time <= 1.0,
                "Time should be between 0 and 1.0");

        return (float) (-1 * Math.pow(Math.E, -time / amplitude) * Math.cos(frequency * time) + 1);
    }
}