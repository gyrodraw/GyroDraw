package ch.epfl.sweng.SDP.utils;

public class BooleanVariableListener {
    private boolean bool;
    private ChangeListener listener;

    public BooleanVariableListener() {
        bool = false;
    }

    public boolean getBoo() {
        return bool;
    }

    /**
     * Set our boolean variable and if a listener is attached
     * call the respective onChange method.
     * @param bool Value to be set.
     */
    public void setBoo(boolean bool) {
        this.bool = bool;
        if (listener != null) {
            listener.onChange();
        }
    }

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
