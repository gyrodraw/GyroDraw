package ch.epfl.sweng.SDP;

import android.graphics.Path;

public class FingerPath {

    private int color;
    private boolean emboss;
    private boolean blur;
    private int strokeWidth;
    private Path path;

    public FingerPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setEmboss(boolean emboss) {
        this.emboss = emboss;
    }

    public void setBlur(boolean blur) {
        this.blur = blur;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return this.color;
    }

    public boolean getEmboss() {
        return this.emboss;
    }

    public boolean getBlur() {
        return this.blur;
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public Path getPath() {
        return this.path;
    }

}
