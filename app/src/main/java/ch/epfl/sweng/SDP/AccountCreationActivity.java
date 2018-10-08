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
    private String userID = currentUser.getUid();
    private TextInputLayout usernameInput;
    private Button createAcc;
    private TextView usernameTaken;
    private String username;
    private Account account;
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
        usernameTaken = this.findViewById(R.id.usernameTaken);
    }

    private void createAccClicked() {
        username = usernameInput.getEditText().getText().toString();
        Constants.databaseRef.child("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    //display a Text that the user name is already taken
                    usernameTaken.setText("That username is already taken.");
                }
                else {
                    account = new Account(username);
                    Constants.databaseRef.child("users").child(userID).setValue(account, new DatabaseReference.CompletionListener() {
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

            }
        });
    }

    private void gotoHome() {
        //account.changeTrophies(200);
        //account.addStars(200);
        //account.changeUsername("other name");
        account.addFriend("asljdf5i");
        account.removeFriend("asljdf5i");
        account.removeFriend("notExistingFriend");
        account.addFriend("imaginary");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("account", this.account);
        startActivity(intent);
    }
}