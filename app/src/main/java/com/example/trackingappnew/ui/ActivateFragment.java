package com.example.trackingappnew.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.trackingappnew.R;
import com.example.trackingappnew.UserClient;
import com.example.trackingappnew.models.User;
import com.example.trackingappnew.services.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.trackingappnew.Constants.isLocationActivated;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivateFragment extends Fragment {

    Button activateButton;

    public ActivateFragment() {
        // Required empty public constructor
    }


    public static ActivateFragment newInstance() {
        ActivateFragment fragment = new ActivateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activateButton = (Button) getView().findViewById(R.id.button);
        activateButton.setText("Activar");
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
            getFragmentManager().popBackStackImmediate();
            activateButton.setText("Desactivar");
        }
        else {
            Log.d("Button Boolean", "activateButton: true to false");
            isLocationActivated = false;
            increaseTrips();
            getFragmentManager().popBackStackImmediate();
            activateButton.setText("Activar");
        }
    }

    private void increaseTrips(){
        User user = ((UserClient) (getContext())).getUser();
        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("Users").document(FirebaseAuth.getInstance().getUid());

        userRef.update("trips", FieldValue.increment(1));
        ((UserClient) getContext()).setUser(user);
    }
}