package com.viewpoints.aischeduler.ui.schedule;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.AppDatabase;
import com.viewpoints.aischeduler.data.model.Schedule;

import java.time.format.DateTimeFormatter;

public class ScheduleInfoTabFragment extends Fragment {
    protected Schedule schedule;

    protected TextView nameText, startDateText, startTimeText, endDateText, endTimeText, placeText, vehicleText, memoText;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        schedule = AppDatabase.getInstance(getActivity()).scheduleDao().get(getArguments().getInt("id"));

        View view = inflater.inflate(R.layout.fragment_schedule_info_tab, container, false);

        nameText = view.findViewById(R.id.name_text);

        startDateText = view.findViewById(R.id.start_date_text);
        startTimeText = view.findViewById(R.id.start_time_text);
        endDateText = view.findViewById(R.id.end_date_text);
        endTimeText = view.findViewById(R.id.end_time_text);

        placeText = view.findViewById(R.id.place_text);
        vehicleText = view.findViewById(R.id.vehicle_text);
        memoText = view.findViewById(R.id.memo_text);

        nameText.setText(schedule.getName());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("a h:mm");

        startDateText.setText(schedule.getStart().format(dateFormatter));
        startTimeText.setText(schedule.getStart().format(timeFormatter));
        endDateText.setText(schedule.getEnd().format(dateFormatter));
        endTimeText.setText(schedule.getEnd().format(timeFormatter));

        vehicleText.setText(schedule.getVehicleType().toString());
        memoText.setText(schedule.getMemo());

        return view;
    }
}
