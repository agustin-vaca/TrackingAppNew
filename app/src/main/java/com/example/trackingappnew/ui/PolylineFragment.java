package com.example.trackingappnew.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackingappnew.R;
import com.example.trackingappnew.models.UserRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

public class PolylineFragment extends Fragment {

    private UserRoute mUserRoute;
    private LatLngBounds mMapBoundary;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            PolylineOptions options = new PolylineOptions();
            for (GeoPoint point : mUserRoute.getTripCoordinates()) {
                LatLng coord = new LatLng(point.getLatitude(), point.getLongitude());
                LatLng pastCoord = new LatLng(0, 0);
                if (coord == pastCoord) {
                    break;
                }
                Log.d("onMapReady Poly", "onMapReady: " + point.getLatitude() + "  " + point.getLongitude());
                options.add(coord);
                pastCoord = coord;
            }
            options.width(5).color(Color.RED);
            Polyline line = googleMap.addPolyline(options);

            double bottomBoundary = mUserRoute.getTripCoordinates().get(0).getLatitude() - .01;
            double leftBoundary = mUserRoute.getTripCoordinates().get(0).getLongitude() - .01;
            double topBoundary = mUserRoute.getTripCoordinates().get(0).getLatitude() + .01;
            double rightBoundary = mUserRoute.getTripCoordinates().get(0).getLongitude() + .01;

            mMapBoundary = new LatLngBounds(
                    new LatLng(bottomBoundary, leftBoundary),
                    new LatLng(topBoundary, rightBoundary)
            );

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
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

    private void setCameraView() {


    }
}