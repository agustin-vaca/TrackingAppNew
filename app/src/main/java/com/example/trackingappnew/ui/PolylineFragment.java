package com.example.trackingappnew.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.trackingappnew.R;
import com.example.trackingappnew.models.UserRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class PolylineFragment extends Fragment {

    androidx.appcompat.widget.Toolbar toolbar;

    private UserRoute mUserRoute;
    private LatLngBounds mMapBoundary;
    private ArrayList<LatLng> debugList = new ArrayList<>();
    private Polyline line;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            PolylineOptions options = new PolylineOptions();
            LatLng pastCoord = new LatLng(0, 0);


            double bottomBoundary = mUserRoute.getTripCoordinates().get(0).getLatitude();
            double leftBoundary = mUserRoute.getTripCoordinates().get(0).getLongitude();
            double topBoundary = mUserRoute.getTripCoordinates().get(0).getLatitude();
            double rightBoundary = mUserRoute.getTripCoordinates().get(0).getLongitude();

            //Calculate points for polyline
            for (GeoPoint point : mUserRoute.getTripCoordinates()) {
                //Calculate coord
                LatLng coord = new LatLng(point.getLatitude(), point.getLongitude());
                if (coord.latitude == pastCoord.latitude && coord.longitude == pastCoord.longitude) {
                    continue;
                }
                Log.d("onMapReady Poly", "onMapReady: " + point.getLatitude() + "  " + point.getLongitude());
                options.add(coord);
                debugList.add(coord);
                pastCoord = coord;

                //Calculate boundaries
                if (coord.latitude < bottomBoundary){
                    bottomBoundary = coord.latitude;
                }
                if (coord.longitude < leftBoundary){
                    leftBoundary = coord.longitude;
                }
                if (coord.latitude > topBoundary){
                    topBoundary = coord.latitude;
                }
                if (coord.longitude > rightBoundary){
                    rightBoundary = coord.longitude;
                }
            }

            //Set start end markers
            GeoPoint starts = mUserRoute.getTripCoordinates().get(0);
            GeoPoint ends = mUserRoute.getTripCoordinates().get(mUserRoute.getTripCoordinates().size()-1);


            LatLng start = new LatLng(starts.getLatitude(), starts.getLongitude());
            LatLng end = new LatLng(ends.getLatitude(), ends.getLongitude());

            options.width(5).color(Color.RED);
            googleMap.clear();
            line = googleMap.addPolyline(options);
            Log.d("Polyline Fragment", "LIST: " + debugList);

            bottomBoundary = bottomBoundary - .005;
            leftBoundary = leftBoundary - .005;
            topBoundary = topBoundary + .005;
            rightBoundary = rightBoundary + .005;

            mMapBoundary = new LatLngBounds(
                    new LatLng(bottomBoundary, leftBoundary),
                    new LatLng(topBoundary, rightBoundary)
            );

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));

            googleMap.addMarker(new MarkerOptions()
                    .position(start)
                    .title("Inicio"));

            googleMap.addMarker(new MarkerOptions()
                    .position(end)
                    .title("Final"));

        }
    };

    public PolylineFragment() {
        // Required empty constructor
    }

    public static PolylineFragment newInstance() {
        return new PolylineFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserRoute = getArguments().getParcelable("intent_route");
            Log.d("PolyLine", "onCreate: "+mUserRoute.getTripCoordinates());
            Log.d("PolyLine", "onCreate: "+mUserRoute.getStartTime());

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polyline, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}