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
