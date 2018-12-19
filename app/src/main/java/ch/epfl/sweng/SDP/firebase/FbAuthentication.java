package ch.epfl.sweng.SDP.firebase;

import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;

/**
 * Utility wrapper class over {@link FirebaseAuth} and {@link AuthUI}.
 */
public final class FbAuthentication {

    private FbAuthentication() {
    }

    /**
     * Starts the sign in flow.
     *
     * @param activity the activity calling the method
     * @param requestCode the code denoting the request
     */
    public static void signIn(BaseActivity activity, int requestCode) {
        final List<IdpConfig> providers = Collections.singletonList(new GoogleBuilder().build());
        activity.startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.mipmap.ic_launcher_round)
                .build(), requestCode);
    }

    /**
     * Signs the user out.
     *
     * @param context the context calling the method
     * @param onCompleteListener the {@link OnCompleteListener} to be added to the task
     */
    public static void signOut(Context context, OnCompleteListener<Void> onCompleteListener) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener(onCompleteListener);
    }
}
