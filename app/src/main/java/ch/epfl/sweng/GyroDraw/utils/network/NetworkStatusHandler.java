package ch.epfl.sweng.GyroDraw.utils.network;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import ch.epfl.sweng.GyroDraw.MainActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;

/**
 * Class that implements the interface for our network listener. It defines the methods
 * to be called when the internet connection changes.
 */
public final class NetworkStatusHandler implements NetworkStateReceiverListener {

    private final Dialog disconnectedDialog;
    private final Context context;
    private static boolean hasLeft = false;

    /**
     * This constructor initializes the dialog that pops up when disconnected.
     * @param context Context of the activity
     */
    NetworkStatusHandler(final Context context) {
        this.context = context;
        disconnectedDialog = new Dialog(this.context);
        disconnectedDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void networkAvailable() {
        // Not needed
    }

    /**
     * Displays the disconnected popup dialog.
     */
    @Override
    public void networkUnavailable() {
        setHasLeft(true);
        disconnectedDialog.setContentView(R.layout.disconnected_pop_up);
        disconnectedDialog.findViewById(R.id.okDisconnectedButton)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account.deleteAccount();
                context.startActivity(new Intent(context, MainActivity.class));
                disconnectedDialog.dismiss();
            }
        });
        disconnectedDialog.show();
    }

    public static boolean getHasLeft() {
        return hasLeft;
    }

    public static void setHasLeft(boolean hasLeftBool) {
        hasLeft = hasLeftBool;
    }
}
