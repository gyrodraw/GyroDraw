package ch.epfl.sweng.SDP.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Utility class that stores the different typefaces.
 */
public class TypefaceLibrary {
    private static Typeface typeMuro;
    private static Typeface typeOptimus;

    private TypefaceLibrary() {}

    /**
     * Instantiates the typefaces given the context.
     *
     * @param context the given context
     */
    public static void setContext(Context context) {
        typeMuro = Typeface.createFromAsset(context.getAssets(), "fonts/Muro.otf");
        typeOptimus = Typeface.createFromAsset(context.getAssets(), "fonts/Optimus.otf");
    }

    public static Typeface getTypeMuro() {
        return typeMuro;
    }

    public static Typeface getTypeOptimus() {
        return typeOptimus;
    }
}
