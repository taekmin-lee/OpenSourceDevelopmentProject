package com.viewpoints.aischeduler.ui.schedule;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.UserLocationContext;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

public class DirectionsTabFragment extends Fragment {
    protected Schedule schedule;

    protected Chip directions1Chip, directions2Chip;

    protected RecyclerView recyclerView;
    protected RouteListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        schedule = (Schedule) getArguments().getSerializable("schedule");

        View view = inflater.inflate(R.layout.fragment_directions_tab, container, false);

        directions1Chip = view.findViewById(R.id.directions1_chip);
        directions2Chip = view.findViewById(R.id.directions2_chip);

        recyclerView = view.findViewById(R.id.recycler_view);

        directions1Chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadDirections();
            }
        });

        directions2Chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadDirections();
            }
        });

        directions1Chip.setOnClickListener(v -> {
            if (!directions1Chip.isChecked() && !directions2Chip.isChecked()) {
                directions1Chip.setChecked(true);
            }
        });

        directions2Chip.setOnClickListener(v -> {
            if (!directions1Chip.isChecked() && !directions2Chip.isChecked()) {
                directions2Chip.setChecked(true);
            }
        });

        loadDirections();
        return view;
    }

    private void loadDirections() {
        GeoApiContext context = OpenApiContext.getInstance(getContext()).getGeoApiContext();

        Location location = UserLocationContext.getInstance(getActivity()).getLocation();
        String origin, destination;

        if (directions1Chip.isChecked()) {
            origin = location.getLatitude() + "," + location.getLongitude();
            destination = schedule.getPlaceLatitude() + "," + schedule.getPlaceLongitude();
        } else {
            origin = schedule.getPlaceLatitude() + "," + schedule.getPlaceLongitude();
            destination = location.getLatitude() + "," + location.getLongitude();
        }

        DirectionsApi.getDirections(context, origin, destination).mode(TravelMode.TRANSIT).alternatives(true).language("ko").setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                adapter = new RouteListAdapter(result.routes);
                adapter.setOnClickListener((view, item) -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("schedule", schedule);
                    bundle.putSerializable("route", item);

                    Navigation.findNavController(view).navigate(R.id.navigation_route_details, bundle);
                });

                getActivity().runOnUiThread(() -> recyclerView.setAdapter(adapter));
                Log.d("Directions result", result.routes.length + " results");
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d("Directions failed", e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
