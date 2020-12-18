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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.UserLocationContext;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kakao.CoordToAddressApiRequest;
import com.viewpoints.aischeduler.data.openapi.kma.TownWeatherForecastApiRequest;
import com.viewpoints.aischeduler.data.openapi.kma.UltrashortWeatherForecast;
import com.viewpoints.aischeduler.data.openapi.kma.UltrashortWeatherForecastApiRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeatherTabFragment extends Fragment {
    protected Schedule schedule;

    protected Chip originChip, destinationChip;

    protected RecyclerView recyclerView;
    protected WeatherForecastListAdapter adapter;

    protected boolean done1 = false, done2 = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        schedule = (Schedule) getArguments().getSerializable("schedule");

        View view = inflater.inflate(R.layout.fragment_weather_tab, container, false);

        originChip = view.findViewById(R.id.origin_chip);
        destinationChip = view.findViewById(R.id.destination_chip);

        recyclerView = view.findViewById(R.id.recycler_view);

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new CoordToAddressApiRequest(UserLocationContext.getInstance(getContext()).getLocation(),
                response -> originChip.setText("출발지: " + response),
                error -> {
                }
        ));

        Location location = new Location("dummy");
        location.setLatitude(schedule.getPlaceLatitude());
        location.setLongitude(schedule.getPlaceLongitude());

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new CoordToAddressApiRequest(location,
                response -> destinationChip.setText("도착지: " + response),
                error -> {
                }
        ));

        originChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                loadWeather();
            }
        });

        destinationChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                loadWeather();
            }
        });

        originChip.setOnClickListener(v -> {
            if (!originChip.isChecked() && !destinationChip.isChecked()) {
                originChip.setChecked(true);
            }
        });

        destinationChip.setOnClickListener(v -> {
            if (!originChip.isChecked() && !destinationChip.isChecked()) {
                destinationChip.setChecked(true);
            }
        });

        if (schedule.getPlaceId() == null) {
            destinationChip.setEnabled(false);
        }

        loadWeather();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadWeather() {
        List<UltrashortWeatherForecast> items = new ArrayList<>();

        Location location;

        if (originChip.isChecked()) {
            location = UserLocationContext.getInstance(getContext()).getLocation();
        } else {
            location = new Location("dummy");
            location.setLatitude(schedule.getPlaceLatitude());
            location.setLongitude(schedule.getPlaceLongitude());
        }

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new UltrashortWeatherForecastApiRequest(location,
                response -> {
                    items.addAll(response);
                    Collections.sort(items);

                    done1 = true;

                    if (done1 && done2) {
                        adapter = new WeatherForecastListAdapter(items);
                        recyclerView.setAdapter(adapter);
                    }

                    Log.d("Ultrashort weather", "success1");
                },
                error -> {
                    Log.d("Ultrashort weather", "error1 " + error);
                }
        ));

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new TownWeatherForecastApiRequest(location,
                response -> {
                    items.addAll(response);
                    Collections.sort(items);

                    done2 = true;

                    if (done1 && done2) {
                        adapter = new WeatherForecastListAdapter(items);
                        recyclerView.setAdapter(adapter);
                    }

                    Log.d("Town weather", "success2");
                },
                error -> {
                    Log.d("Town weather", "error2 " + error);
                }
        ));
    }

}
