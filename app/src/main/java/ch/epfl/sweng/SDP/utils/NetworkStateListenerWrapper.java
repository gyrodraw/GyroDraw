package ch.epfl.sweng.SDP.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;

public class NetworkStateListenerWrapper implements NetworkStateReceiverListener {

    private Dialog disconnectedDialog;
    private Context context;

    public NetworkStateListenerWrapper(final Context context) {
        this.context = context;
        disconnectedDialog = new Dialog(this.context);
        disconnectedDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void networkAvailable() {
        // Not needed
    }

    @Override
    public void networkUnavailable() {
        disconnectedDialog.setContentView(R.layout.disconnected_pop_up);
        disconnectedDialog.findViewById(R.id.okDisconnectedButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account.deleteAccount();
                context.startActivity(new Intent(context, MainActivity.class));
                disconnectedDialog.dismiss();
            }
        });
        disconnectedDialog.show();
    }
}
