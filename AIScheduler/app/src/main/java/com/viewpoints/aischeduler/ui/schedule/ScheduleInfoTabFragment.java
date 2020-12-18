package com.viewpoints.aischeduler.ui.schedule;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.model.Schedule;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ScheduleInfoTabFragment extends Fragment {
    protected Schedule schedule;

    protected NaverMap map;

    protected TextView nameText, startDateText, startTimeText, endDateText, endTimeText, placeText, vehicleText, memoText;
    protected View placeDivider;
    protected LinearLayout placeLayout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        schedule = (Schedule) getArguments().getSerializable("schedule");

        Log.d("test", "created view");

        View view = inflater.inflate(R.layout.fragment_schedule_info_tab, container, false);

        FragmentManager manager = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)manager.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            manager.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(map ->
        {
            this.map = map;

            map.setCameraPosition(new CameraPosition( new LatLng(schedule.getPlaceLatitude(), schedule.getPlaceLongitude()), 14));
            loadMarker();
        });

        nameText = view.findViewById(R.id.name_text);

        startDateText = view.findViewById(R.id.start_date_text);
        startTimeText = view.findViewById(R.id.start_time_text);
        endDateText = view.findViewById(R.id.end_date_text);
        endTimeText = view.findViewById(R.id.end_time_text);

        placeDivider = view.findViewById(R.id.place_divider);
        placeLayout = view.findViewById(R.id.place_layout);
        placeText = view.findViewById(R.id.place_text);

        vehicleText = view.findViewById(R.id.vehicle_text);
        memoText = view.findViewById(R.id.memo_text);

        nameText.setText(schedule.getName());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA);

        startDateText.setText(schedule.getStart().format(dateFormatter));
        startTimeText.setText(schedule.getStart().format(timeFormatter));
        endDateText.setText(schedule.getEnd().format(dateFormatter));
        endTimeText.setText(schedule.getEnd().format(timeFormatter));

        if (schedule.getPlaceId() != null) {
            placeDivider.setVisibility(View.VISIBLE);
            placeLayout.setVisibility(View.VISIBLE);
            placeText.setText(schedule.getPlaceName());
        }

        vehicleText.setText(schedule.getVehicleType().toString());
        memoText.setText(schedule.getMemo());

        return view;
    }

    private void loadMarker()
    {
        Marker marker = new Marker();
        marker.setPosition(new LatLng(schedule.getPlaceLatitude(), schedule.getPlaceLongitude()));
        marker.setCaptionText(schedule.getPlaceName());

        marker.setMap(map);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMarker();
    }
}
