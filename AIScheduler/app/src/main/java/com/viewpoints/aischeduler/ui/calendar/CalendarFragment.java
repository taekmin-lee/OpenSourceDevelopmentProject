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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {

    protected MaterialToolbar toolbar;

    protected CalendarView calendarView;

    protected RecyclerView recyclerView;
    protected ScheduleListAdapter scheduleListAdapter;

    protected FloatingActionButton addButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<Schedule> test = new ArrayList<Schedule>();

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        AppDatabase database = AppDatabase.getInstance(getActivity());
        LiveData<List<Schedule>> data = database.scheduleDao().getAll();
        data.observe(getViewLifecycleOwner(), this::filterSchedules);

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {
            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                toolbar.setTitle(calendar.getYear() + "년 " + calendar.getMonth() + "월");
            }
        });

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(calendarView.getCurYear() + "년 " + calendarView.getCurMonth() + "월");

        recyclerView = view.findViewById(R.id.scheduleList);

        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("date", LocalDate.of(calendarView.getSelectedCalendar().getYear(), calendarView.getSelectedCalendar().getMonth(), calendarView.getSelectedCalendar().getDay()));

            Navigation.findNavController(v).navigate(R.id.navigation_schedule_form, bundle);
        });

        filterSchedules(data.getValue());

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

            int year = calendarView.getCurYear();
            int month = calendarView.getCurMonth();
            YearMonth yearMonth = YearMonth.of(year, month);
            int day = calendarView.getCurDay();

            for (Schedule schedule : schedules) {
                if (YearMonth.from(schedule.getStart()).equals(yearMonth) || YearMonth.from(schedule.getEnd()).equals(yearMonth)) {
                    for (LocalDate d = schedule.getStart().toLocalDate(); d.compareTo(schedule.getEnd().toLocalDate()) <= 0; d = d.plusDays(1)) {
                        if (YearMonth.from(d).equals(yearMonth)) {
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
            }

            calendarView.setSchemeDate(monthly);

            scheduleListAdapter = new ScheduleListAdapter(daily);
            scheduleListAdapter.setOnClickListener((view, item) -> {
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.getId());

                Log.d("calendard", "selected id is " + item.getId());

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
