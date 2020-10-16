package com.example.trackingappnew.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackingappnew.R;
import com.example.trackingappnew.services.LocationService;

import static com.example.trackingappnew.Constants.isLocationActivated;

public class ActivateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);

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
            Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);
            stopService(serviceIntent);
            Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}