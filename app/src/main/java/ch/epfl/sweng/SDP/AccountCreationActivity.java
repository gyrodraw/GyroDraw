package ch.epfl.sweng.SDP;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

public class AccountCreationActivity extends AppCompatActivity {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userId;
    private TextInputLayout usernameInput;
    private Button createAcc;
    private TextView usernameTaken;
    private String username;
    private Account account;
    private View.OnClickListener createAccListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
        usernameTaken = this.findViewById(R.id.usernameTaken);
        userId = currentUser.getUid();
    }

    private void createAccClicked() {
        username = usernameInput.getEditText().getText().toString();
        Constants.usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    usernameTaken.setText("Username is already taken.");
                }
                else {
                    account = new Account(username);
                    Constants.usersRef.child(userId).setValue(account, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                usernameTaken.setText("Failed to write data to database.");
                            }
                            else {
                                gotoHome();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                usernameTaken.setText("An error occurred, please retry.");
            }
        });
    }

    private void gotoHome() {
        account.addStars(500);
        account.subtractStars(200);
        account.changeUsername("wierdestErrorEver");
        account.changeTrophies(300);
        account.addFriend("imaginary");
        account.addFriend("toBeRemoved");
        account.removeFriend("toBeRemoved");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("account", this.account);
        startActivity(intent);
    }
}