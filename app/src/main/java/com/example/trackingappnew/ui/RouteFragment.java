package com.example.trackingappnew.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class RouteFragment extends Fragment {

    //Widgets
    private RecyclerView mRouteRecyclerView;

    //vars
    private RouteRecyclerAdapter mRouteRecyclerAdapter;
    private ListenerRegistration mRouteEventListener;
    private FirebaseFirestore mDb;
    private User user;
    private Long startDate;
    private Long endDate;
    private UserRoute mUserRoute;
    private ArrayList<UserRoute> mUserRoutes = new ArrayList<>();
    private ArrayList<GeoPoint> userCoordinates = new ArrayList<>();
    public int numOfTrips;
    private String user_id;
    private List<UserLocation> routeObjects;
    private List<UserLocation> endDateObjects;
    private List<UserLocation> coordinateObjects;
    private Long firebaseStartDate;
    private Long firebaseEndDate;



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
                        Log.d("Route Fragment", "onCreateView: numofTrips before"+numOfTrips);

                        // Check each trip if it matches time constraint
                        for (int k = 1; k <= numOfTrips; k++){
                            Log.d("TimeConstraint", "onComplete: ");
                            Query query = mDb.collection("User Routes").document(user_id).collection("Route "+ k)
                                    .orderBy("timestamp", Query.Direction.ASCENDING)
                                    .limit(1);
                            int finalK = k;
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){

                                        // Get list of UserLocation objects of size 1
                                        routeObjects = task.getResult().toObjects(UserLocation.class);
                                        Log.d("Route Fragment", "onComplete: "+routeObjects.size());
                                        for (int i = 0; i<routeObjects.size(); i++){
                                            firebaseStartDate = routeObjects.get(0).getTimestamp().getTime();
                                            Log.d("routeObject", "onComplete: "+routeObjects.get(0));
                                            //If Route date is in between constraints, build UserRoute Object
                                            if (firebaseStartDate >= startDate && firebaseStartDate <= endDate ){
                                                if (mUserRoute == null){
                                                    mUserRoute = new UserRoute();
                                                }
                                                //Write userName
                                                mUserRoute.setUserName(routeObjects.get(i).getUser().getUsername());
                                                //Write startTime
                                                mUserRoute.setStartTime(new Date(firebaseStartDate));
                                                Log.d("starttime", "onComplete: "+ mUserRoute.getStartTime());
                                                //Write tripCoordinates
                                                Query queryCoord = mDb.collection("User Routes").document(user_id).collection("Route "+ finalK)
                                                        .orderBy("timestamp", Query.Direction.ASCENDING);
                                                queryCoord.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            coordinateObjects = task.getResult().toObjects(UserLocation.class);
                                                            for (int i = 0; i<coordinateObjects.size(); i++){
                                                                userCoordinates.add(coordinateObjects.get(i).getGeo_point());
                                                            }
                                                            mUserRoute.setTripCoordinates(userCoordinates);
                                                            Log.d("mUserRoute", "rightbefore"+mUserRoute.getStartTime());
                                                            mUserRoutes.add(mUserRoute);
                                                            if (mUserRoutes.size() == numOfTrips){
                                                                for (UserRoute route : mUserRoutes){
                                                                    Log.d("UserRoutes", "onComplete: mUserRoutes = "+mUserRoutes.size());
                                                                    Log.d("User Routes", ""+route.getUserName() + " "+ route.getStartTime());
                                                                }
                                                                populateRecyclerView(mUserRoutes);
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
                    }

                }
            }
        });
    }

    private void populateRecyclerView(ArrayList<UserRoute> userRoutes){
        mRouteRecyclerAdapter.notifyItemRangeInserted(0, mUserRoutes.size());
    }


}