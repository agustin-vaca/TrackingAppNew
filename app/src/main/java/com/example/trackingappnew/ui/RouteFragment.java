package com.example.trackingappnew.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trackingappnew.R;
import com.example.trackingappnew.models.User;
import com.example.trackingappnew.models.UserRoute;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.squareup.okhttp.Route;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class RouteFragment extends Fragment {

    private RecyclerView mRouteRecyclerView;

    private ListenerRegistration mRouteEventListener;
    private FirebaseFirestore mDb;
    private User user;
    private ArrayList<User> mUserList = new ArrayList<>();
    private Long startDate;
    private Long endDate;
    private UserRoute mUserRoute;
    private ArrayList<UserRoute> mUserRoutes;
    public int numOfTrips;
    private String user_id;
    private List<String> routeDate;


    public RouteFragment() {
        // Required empty public constructor
    }


    public static RouteFragment newInstance() {
        return new RouteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("intent_user");
            mUserList = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
            startDate = getArguments().getLong("intent_startLong");
            endDate = getArguments().getLong("intent_endLong");
            mDb = FirebaseFirestore.getInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_route, container, false);
        mRouteRecyclerView = view.findViewById(R.id.route_list_recycler_view);
        Log.d("Route Fragment", "onCreateView: ");


        for (User user : mUserList){
            CompletableFuture.supplyAsync(() -> getNumOfTrips())
                    .thenAcceptAsync(numOfTrips -> loopOverRoutes(numOfTrips));

        }

//        final CollectionReference routeRef = mDb
//                .collection("User Routes");
//
//        mRouteEventListener = routeRef
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.e("Route Fragment", "onEvent: Listen failed.", e);
//                            return;
//                        }
//
//                        if (queryDocumentSnapshots != null) {
//
//                            // Clear the list and add all the users again
//                            mUserRoutes.clear();
//                            mUserRoutes = new ArrayList<>();
//
//                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                                DocumentReference userRef = mDb.collection("Users")
//                                        .document(doc.getId());
//
//                                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()){
//                                            if (task.getResult().toObject(User.class) != null){
//                                                numOfTrips = task.getResult().toObject(User.class).getTrips();
//                                            }
//                                        }
//                                    }
//                                });
//
//
//
//                            }
//
//                        }
//                    }
//                });
//

        return view;
    }

    private int getNumOfTrips(){
        //Get user document reference
        user_id = user.getUser_id();
        DocumentReference userRef = mDb.collection("Users")
                .document(user_id);

        // Get number of trips
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().toObject(User.class) != null){
                        numOfTrips = task.getResult().toObject(User.class).getTrips();
                        Log.d("Route Fragment", "onComplete: " +numOfTrips);
                        Log.d("Route Fragment", "onCreateView: numofTrips before"+numOfTrips);
                        for (int i = 1; i <= numOfTrips; i++){
                            Log.d("Route Fragment", "query: query first");
                            Query query = mDb.collection("User Routes").document(user_id).collection("Route "+ i)
                                    .orderBy("timestamp", Query.Direction.ASCENDING)
                                    .limit(1);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        routeDate = task.getResult().toObjects(String.class);
                                        Log.d("Route Fragment", "onComplete: query "+routeDate);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        Log.d("Route Fragment" +
                "", "getNumOfTrips: "+numOfTrips);
        return numOfTrips;
    }

    private void loopOverRoutes(int numOfTrips){
        //Loop over all Routes
        Log.d("Route Fragment", "onCreateView: numofTrips before"+numOfTrips);
        for (int i = 1; i <= numOfTrips; i++){
            Log.d("Route Fragment", "query: query first");
            Query query = mDb.collection("User Routes").document(user_id).collection("Route "+ i)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limit(1);
            query.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Route Fragment", "onFailure: failed");
                }
            });
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        routeDate = task.getResult().toObjects(String.class);
                        Log.d("Route Fragment", "onComplete: query "+routeDate);
                    }
                }
            });
        }
    }
}