package com.example.trackingappnew.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.trackingappnew.R;
import com.example.trackingappnew.UserClient;
import com.example.trackingappnew.models.User;
import com.example.trackingappnew.services.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.trackingappnew.Constants.isLocationActivated;

public class ActivateActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);

        toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        final Button activateButton = (Button) findViewById(R.id.button);
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateButton();
            }
        });
    }

    private void activateButton(){
        if (!isLocationActivated){
            Log.d("Button Boolean", "activateButton: false to true");
            isLocationActivated = true;
            Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Log.d("Button Boolean", "activateButton: true to false");
            isLocationActivated = false;
            increaseTrips();
            Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);
            stopService(serviceIntent);
            Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void increaseTrips(){
        User user = ((UserClient) (getApplicationContext())).getUser();
        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("Users").document(FirebaseAuth.getInstance().getUid());

        userRef.update("trips", FieldValue.increment(1));
        ((UserClient) getApplicationContext()).setUser(user);
    }
}