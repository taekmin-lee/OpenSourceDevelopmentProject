package com.viewpoints.aischeduler.ui.schedule;

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
import com.viewpoints.aischeduler.data.AppDatabase;
import com.viewpoints.aischeduler.data.WGS84Coordinate;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kma.TownWeatherForecastApiRequest;
import com.viewpoints.aischeduler.data.openapi.kma.UltrashortWeatherForecast;
import com.viewpoints.aischeduler.data.openapi.kma.UltrashortWeatherForecastApiRequest;

import java.util.ArrayList;
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
        schedule = AppDatabase.getInstance(getActivity()).scheduleDao().get(getArguments().getInt("id"));

        View view = inflater.inflate(R.layout.fragment_weather_tab, container, false);

        originChip = view.findViewById(R.id.origin_chip);
        destinationChip = view.findViewById(R.id.destination_chip);

        if (schedule.getLongitude() == null && schedule.getLatitude() == null) {
            destinationChip.setEnabled(false);
        }

        recyclerView = view.findViewById(R.id.recycler_view);

        List<UltrashortWeatherForecast> items = new ArrayList<>();

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new UltrashortWeatherForecastApiRequest(new WGS84Coordinate(127.45650111773608, 36.62941283907122),
                response -> {
                    items.addAll(response);
                    done1 = true;

                    if (done1 && done2) {
                        adapter = new WeatherForecastListAdapter(items);
                        recyclerView.setAdapter(adapter);
                    }

                    Log.d("Ultrashort", "success1");
                },
                error -> {
                    Log.d("Ultrashort", "error1 " + error);
                }
        ));

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new TownWeatherForecastApiRequest(new WGS84Coordinate(127.45650111773608, 36.62941283907122),
                response -> {
                    items.addAll(response);
                    done2 = true;

                    if (done1 && done2) {
                        adapter = new WeatherForecastListAdapter(items);
                        recyclerView.setAdapter(adapter);
                    }

                    Log.d("Ultrashort", "success2");
                },
                error -> {
                    Log.d("Ultrashort", "error2 " + error);
                }
        ));

        Log.d("Ultrashort", "it is running");

        Log.d("ultrashort", "count: " + items.size());


        return view;
    }

}
