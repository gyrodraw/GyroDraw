package ch.epfl.sweng.SDP;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

public class Constants {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
    public static final DatabaseReference databaseRef = database.getReference();
    public static final DatabaseReference usersRef= databaseRef.child("users");

    private Constants() {
    }
}
