package ch.epfl.sweng.SDP;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button gotoCreateAccount;
    private View.OnClickListener gotoCreateAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gotoCreateAccountClicked();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gotoCreateAccount = this.findViewById(R.id.mainGoButton);
        gotoCreateAccount.setOnClickListener(gotoCreateAccountListener);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button

        Intent intent = new Intent(this, GreetingActivity.class);
        startActivity(intent);
    }
    public void gotoCreateAccountClicked() {
        Intent i = new Intent(this, AccountCreation.class);
        startActivity(i);
    }
}
