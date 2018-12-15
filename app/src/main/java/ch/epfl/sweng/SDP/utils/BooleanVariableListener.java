package ch.epfl.sweng.SDP.utils;

import android.support.annotation.VisibleForTesting;

/**
 * Class representing a listener for boolean variables.
 */
public class BooleanVariableListener {
    private boolean bool;
    private ChangeListener listener;

    public BooleanVariableListener() {
        bool = false;
    }

    public boolean getBool() {
        return bool;
    }

    /**
     * Sets our boolean variable and if a listener is attached
     * calls the respective onChange method.
     *
     * @param bool Value to be set.
     */
    public void setBool(boolean bool) {
        this.bool = bool;
        if (listener != null) {
            listener.onChange();
        }
    }

    @VisibleForTesting
    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}