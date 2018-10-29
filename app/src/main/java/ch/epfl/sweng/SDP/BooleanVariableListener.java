package ch.epfl.sweng.SDP;

public class BooleanVariableListener {
    private boolean boo;
    private ChangeListener listener;

    public BooleanVariableListener() {
        boo = false;
    }

    public boolean getBoo() {
        return boo;
    }

    /**
     * Set our boolean variable and if a listener is attached
     * call the respective onChange method.
     * @param boo Value to be set.
     */
    public void setBoo(boolean boo) {
        this.boo = boo;
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
