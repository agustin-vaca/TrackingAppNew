package com.example.trackingappnew.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackingappnew.R;
import com.example.trackingappnew.models.UserRoute;
import com.example.trackingappnew.util.RouteRecyclerClickListener;
import com.example.trackingappnew.util.UserRecyclerClickListener;

import java.util.ArrayList;

public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.ViewHolder>{

    private RouteRecyclerClickListener listener;

    public void setListener(RouteRecyclerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View routeView = inflater.inflate(R.layout.layout_route_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(routeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        final UserRoute route = mUserRoutes.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onRouteRecyclerClick(route);
                }
            }
        });

        // Set item views based on your views and data model
        TextView username = holder.nameTextView;
        username.setText(route.getUserName());
        TextView start = holder.startTextView;
        start.setText(route.getStartTime().toString());
        TextView end = holder.endTextView;
        end.setText(route.getEndTime().toString());
    }

    @Override
    public int getItemCount() {
        return mUserRoutes.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView startTextView;
        public TextView endTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.user_name);
            startTextView = (TextView) itemView.findViewById(R.id.start_date);
            endTextView = (TextView) itemView.findViewById(R.id.end_date);
        }
    }

    // Store a member variable for the contacts
    private ArrayList<UserRoute> mUserRoutes;

    // Pass in the contact array into the constructor
    public RouteRecyclerAdapter(ArrayList<UserRoute> routes) {
        mUserRoutes = routes;
    }
}