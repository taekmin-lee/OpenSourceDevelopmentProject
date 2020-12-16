package com.viewpoints.aischeduler.ui.schedule;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.AppDatabase;
import com.viewpoints.aischeduler.data.model.Schedule;

import java.io.IOException;

public class DirectionsTabFragment extends Fragment {
    protected Schedule schedule;

    protected Chip originChip, destinationChip;

    protected RecyclerView recyclerView;
    protected DirectionsListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        schedule = AppDatabase.getInstance(getActivity()).scheduleDao().get(getArguments().getInt("id"));

        View view = inflater.inflate(R.layout.fragment_directions_tab, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyCsrNVHJsJvsHR75IzPSn1lEF0c4T-pplI").build();

        try {
            DirectionsResult result = DirectionsApi.getDirections(context, "오송역", "충북대학교").mode(TravelMode.TRANSIT).alternatives(true).await();
            adapter = new DirectionsListAdapter(result.routes);
            recyclerView.setAdapter(adapter);

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

}
