package ch.epfl.sweng.SDP;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

public class AccountCreationActivity extends AppCompatActivity {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = currentUser.getUid();
    private TextInputLayout usernameInput;
    private Button createAcc;

    //testing stuff
    private TextView t1;
    private TextView t2;

    private View.OnClickListener createAccListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createAccClicked();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);
        usernameInput = this.findViewById(R.id.usernameInput);
        createAcc = this.findViewById(R.id.createAcc);
        createAcc.setOnClickListener(createAccListener);

        t1 = this.findViewById(R.id.textView);
        t2 = this.findViewById(R.id.textView2);
    }

    private void createAccClicked() {
        t1.setText("clicked");
        t2.setText(Constants.databaseRef.toString());
        String username = usernameInput.getEditText().getText().toString();
        Constants.databaseRef.child("users").orderByChild("username").equalTo(username).once("value", snapshot => {
        if(snapshot.exists())  {

        }
            else {

            }
        });
        Query query = Constants.databaseRef.child("$uid").child("username").equalTo(username);
        Account acc = new Account(username);
        Constants.databaseRef.child("users").child(userID).setValue(acc);
    }
}