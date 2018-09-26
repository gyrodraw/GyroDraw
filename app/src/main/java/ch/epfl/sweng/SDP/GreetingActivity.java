package ch.epfl.sweng.SDP;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        TextView t = findViewById(R.id.greetingMessage);
        t.setText("Hello from my unit test!");
    }
}
