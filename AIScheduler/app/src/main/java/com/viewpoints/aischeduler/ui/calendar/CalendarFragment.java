package com.viewpoints.aischeduler.ui.calendar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.AppDatabase;
import com.viewpoints.aischeduler.data.model.Schedule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {

    protected MaterialToolbar toolbar;

    protected CalendarView calendarView;

    protected RecyclerView recyclerView;
    protected ScheduleListAdapter scheduleListAdapter;

    protected TextView dateText;

    protected FloatingActionButton createButton;

    protected LiveData<List<Schedule>> schedules;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        AppDatabase database = AppDatabase.getInstance(getActivity());
        schedules = database.scheduleDao().getAll();
        schedules.observe(getViewLifecycleOwner(), this::filterSchedules);

        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {
            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                toolbar.setTitle(String.format("%d년 %d월", calendar.getYear(), calendar.getMonth()));
                dateText.setText(String.format("%d년 %d월 %d일", calendar.getYear(), calendar.getMonth(), calendar.getDay()));

                filterSchedules(schedules.getValue());
            }
        });

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(calendarView.getCurYear() + "년 " + calendarView.getCurMonth() + "월");

        recyclerView = view.findViewById(R.id.recycler_view);

        dateText = view.findViewById(R.id.date_text);
        dateText.setText(calendarView.getCurYear() + "년 " + calendarView.getCurMonth() + "월 " + calendarView.getCurDay() + "일");

        createButton = view.findViewById(R.id.create_button);
        createButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("date", LocalDate.of(calendarView.getSelectedCalendar().getYear(), calendarView.getSelectedCalendar().getMonth(), calendarView.getSelectedCalendar().getDay()));

            Navigation.findNavController(v).navigate(R.id.navigation_schedule_form, bundle);
        });

        filterSchedules(schedules.getValue());

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void filterSchedules(List<Schedule> schedules) {
        if (schedules != null) {
            Map<String, Calendar> monthly = new HashMap<>();
            List<Schedule> daily = new ArrayList<>();

            int year = calendarView.getSelectedCalendar().getYear();
            int month = calendarView.getSelectedCalendar().getMonth();
            int day = calendarView.getSelectedCalendar().getDay();

            Log.d("test", day + "일 일정");

            for (Schedule schedule : schedules) {
                for (LocalDate d = schedule.getStart().toLocalDate(); d.compareTo(schedule.getEnd().toLocalDate()) <= 0; d = d.plusDays(1)) {
                    if (d.getYear() == year && d.getMonthValue() == month) {
                        Calendar calendar = new Calendar();
                        calendar.setYear(year);
                        calendar.setMonth(month);
                        calendar.setDay(d.getDayOfMonth());
                        calendar.setSchemeColor(Color.BLUE);

                        monthly.put(calendar.toString(), calendar);

                        if (d.getDayOfMonth() == day) {
                            daily.add(schedule);
                        }
                    }
                }
            }

            calendarView.setSchemeDate(monthly);

            scheduleListAdapter = new ScheduleListAdapter(daily);
            scheduleListAdapter.setOnClickListener((view, item) -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("schedule", item);

                Navigation.findNavController(view).navigate(R.id.navigation_schedule_details, bundle);
            });

            recyclerView.setAdapter(scheduleListAdapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calendar_top_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Log.d("TEST", "detected!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
