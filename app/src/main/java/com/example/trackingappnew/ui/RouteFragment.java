package com.example.trackingappnew.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingappnew.R;
import com.example.trackingappnew.adapters.RouteRecyclerAdapter;
import com.example.trackingappnew.models.User;
import com.example.trackingappnew.models.UserLocation;
import com.example.trackingappnew.models.UserRoute;
import com.example.trackingappnew.util.RouteRecyclerClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class RouteFragment extends Fragment implements
        RouteRecyclerClickListener {

    //Widgets
    private RecyclerView mRouteRecyclerView;

    //vars
    private RouteRecyclerAdapter mRouteRecyclerAdapter;
    private FirebaseFirestore mDb;
    private User user;
    private Long startDate;
    private Long endDate;
    private UserRoute mUserRoute;
    private final ArrayList<UserRoute> mUserRoutes = new ArrayList<>();
    private ArrayList<GeoPoint> userCoordinates;
    public int numOfTrips;
    private String user_id;
    private List<UserLocation> routeObjects;
    private Long firebaseStartDate;



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
        mRouteRecyclerAdapter = new RouteRecyclerAdapter(mUserRoutes);
        mRouteRecyclerAdapter.setListener(this);
        mRouteRecyclerView.setAdapter(mRouteRecyclerAdapter);
        mRouteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getRoutes(user);
    }

    private void getRoutes(User user){
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
                        final int[] counter = {0};
                        Log.d("Route Fragment", "onCreateView: numofTrips before"+numOfTrips);

                        // Check each trip if it matches time constraint
                        for (int k = 1; k <= numOfTrips; k++){
                            Log.d("TimeConstraint", "onComplete: ");
                            Query query = mDb.collection("User Routes").document(user_id).collection("Route " + k)
                                    .orderBy("timestamp", Query.Direction.ASCENDING);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){

                                        // Get list of UserLocation objects of size 1
                                        routeObjects = task.getResult().toObjects(UserLocation.class);
                                        Log.d("Route Fragment", "onComplete: "+routeObjects.size());
                                        if (routeObjects.size() > 0) {
                                            firebaseStartDate = routeObjects.get(0).getTimestamp().getTime();
                                            Log.d("routeObject", "onComplete: " + routeObjects.get(0));
                                            //If Route date is in between constraints, build UserRoute Object
                                            if (firebaseStartDate >= startDate && firebaseStartDate <= endDate) {
                                                counter[0]++;
                                                mUserRoute = new UserRoute();
                                                userCoordinates = new ArrayList<>();
                                                //Write userName
                                                mUserRoute.setUserName(routeObjects.get(0).getUser().getUsername());
                                                //Write startTime
                                                mUserRoute.setStartTime(new Date(firebaseStartDate));
                                                Log.d("starttime", "onComplete: " + mUserRoute.getStartTime());
                                                //Write tripCoordinates
                                                for (UserLocation object : routeObjects) {
                                                    userCoordinates.add(object.getGeo_point());
                                                }
                                                mUserRoute.setTripCoordinates(userCoordinates);
                                                Log.d("mUserRoute", "rightbefore" + mUserRoute.getStartTime());
                                                mUserRoutes.add(mUserRoute);
                                                Log.d("mUserRoute", "rightAfter" + mUserRoutes.size());
                                                if (mUserRoutes.size() == numOfTrips) {
                                                    for (UserRoute route : mUserRoutes) {
                                                        Log.d("UserRoutes", "onComplete: mUserRoutes = " + mUserRoutes.size());
                                                        Log.d("User Routes", "" + route.getUserName() + " " + route.getStartTime());
                                                    }

                                                }
                                            }
                                            if (counter[0] == mUserRoutes.size()) {
                                                Collections.sort(mUserRoutes);
                                                populateRecyclerView();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }

                }
            }
        });
    }

    private void populateRecyclerView() {
        mRouteRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRouteRecyclerClick(UserRoute route) {
        sendPolyline(route);
    }

    private void sendPolyline(UserRoute route) {
        PolylineFragment fragment = PolylineFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("intent_route", route);
        fragment.setArguments(bundle);

        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.user_list_container, fragment, "Poly");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}