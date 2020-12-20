package com.viewpoints.aischeduler.ui.calendar;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.AppDatabase;
import com.viewpoints.aischeduler.data.model.PlaceType;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.model.VehicleType;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kakao.PlaceDetailsApiRequest;
import com.viewpoints.aischeduler.data.openapi.kakao.PlaceSearchResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ScheduleFormFragment extends Fragment {

    protected MaterialToolbar toolbar;

    protected EditText nameText, memoText;
    protected SwitchMaterial allDaySwitch;
    protected ConstraintLayout endLayout;
    protected TextView startDateText, startTimeText, endDateText, endTimeText, placeText;
    protected ImageButton removePlaceButton;
    protected Chip carChip, transitChip;

    protected Schedule schedule;
    protected LocalDate startDate, endDate;
    protected LocalTime startTime, endTime;

    protected String placeName;
    protected Integer placeId;
    protected PlaceType placeType;
    protected Integer placeCategoryId;
    protected String placeCategoryName;
    protected Double placeLongitude, placeLatitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_form, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        nameText = view.findViewById(R.id.name_text);

        allDaySwitch = view.findViewById(R.id.allday_switch);
        endLayout = view.findViewById(R.id.end_layout);

        allDaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            startTimeText.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            endLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        startDateText = view.findViewById(R.id.start_date_text);
        startDateText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setSelection(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()).setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR).build();
            datePicker.show(requireFragmentManager(), "fragment_tag");

            datePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
                startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(selection), ZoneId.systemDefault()).toLocalDate();
                updateDate(startDate, startDateText);
            });
        });

        startTimeText = view.findViewById(R.id.start_time_text);
        startTimeText.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(startTime.getHour()).setMinute(startTime.getMinute()).build();
            timePicker.show(requireFragmentManager(), "fragment_tag");

            timePicker.addOnPositiveButtonClickListener(d -> {
                startTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                updateTime(startTime, startTimeText);
            });
        });

        endDateText = view.findViewById(R.id.end_date_text);
        endDateText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setSelection(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()).setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR).build();
            datePicker.show(requireFragmentManager(), "fragment_tag");

            datePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
                endDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(selection), ZoneId.systemDefault()).toLocalDate();
                updateDate(endDate, endDateText);
            });
        });

        endTimeText = view.findViewById(R.id.end_time_text);
        endTimeText.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(endTime.getHour()).setMinute(endTime.getMinute()).build();
            timePicker.show(requireFragmentManager(), "fragment_tag");

            timePicker.addOnPositiveButtonClickListener(d -> {
                endTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                updateTime(endTime, endTimeText);
            });
        });

        getParentFragmentManager().setFragmentResultListener("placeSearch", this, (requestKey, bundle) -> {
            PlaceSearchResult place = (PlaceSearchResult) bundle.getSerializable("place");

            placeName = place.getName();
            placeId = place.getId();
            placeType = PlaceType.get(place.getCategoryCode());
            placeLongitude = place.getLongitude();
            placeLatitude = place.getLatitude();

            placeText.setText(place.getName());
            placeText.setTextColor(0xFF000000);
            removePlaceButton.setVisibility(View.VISIBLE);

            OpenApiContext.getInstance(getContext()).getRequestQueue().add(new PlaceDetailsApiRequest(place.getId(), response -> {
                placeCategoryId = response.getCategoryId();
                placeCategoryName = response.getCategoryName();
            }, error -> {
            }));
        });

        placeText = view.findViewById(R.id.place_text);
        placeText.setOnClickListener(v ->
        {
            NavController controller = NavHostFragment.findNavController(this);
            controller.navigate(R.id.navigation_place_dialog);
        });

        removePlaceButton = view.findViewById(R.id.remove_place_button);
        removePlaceButton.setOnClickListener(v -> {
            placeName = null;
            placeId = null;
            placeType = null;
            placeCategoryId = null;
            placeCategoryName = null;
            placeLongitude = placeLatitude = null;

            placeText.setText("장소");
            placeText.setTextColor(0x99000000);
            removePlaceButton.setVisibility(View.GONE);
        });

        carChip = view.findViewById(R.id.car_chip);
        transitChip = view.findViewById(R.id.transit_chip);

        memoText = view.findViewById(R.id.memo_text);

        loadData(getArguments());

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void updateDate(LocalDate date, TextView view) {
        view.setText(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void updateTime(LocalTime time, TextView view) {
        view.setText(time.format(DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void loadData(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey("schedule")) {
                Schedule schedule = (Schedule) bundle.getSerializable("schedule");

                nameText.setText(schedule.getName());
                allDaySwitch.setChecked(schedule.isAllDay());
                carChip.setChecked(schedule.getVehicleType() == VehicleType.CAR);
                transitChip.setChecked(schedule.getVehicleType() == VehicleType.TRANSIT);
                memoText.setText(schedule.getMemo());

                startDate = schedule.getStart().toLocalDate();
                startTime = schedule.getStart().toLocalTime();
                endDate = schedule.getEnd().toLocalDate();
                endTime = schedule.getEnd().toLocalTime();

                placeId = schedule.getPlaceId();

                if (placeId != null) {
                    placeName = schedule.getPlaceName();
                    placeType = schedule.getPlaceType();
                    placeCategoryId = schedule.getPlaceCategoryId();
                    placeCategoryName = schedule.getPlaceCategoryName();
                    placeLongitude = schedule.getPlaceLongitude();
                    placeLatitude = schedule.getPlaceLatitude();

                    placeText.setText(placeName);
                    placeText.setTextColor(0xFF000000);
                    removePlaceButton.setVisibility(View.VISIBLE);
                }

                toolbar.setTitle("일정 수정");
            } else if (bundle.containsKey("date")) {
                startDate = (LocalDate) bundle.getSerializable("date");

                LocalDateTime temp = LocalDateTime.now().plusHours(1);
                startTime = LocalTime.of(temp.getHour(), 0);

                temp = startDate.atTime(startTime).plusHours(1);

                endDate = temp.toLocalDate();
                endTime = temp.toLocalTime();

                toolbar.setTitle("일정 추가");
            }

            updateDate(startDate, startDateText);
            updateTime(startTime, startTimeText);
            updateDate(endDate, endDateText);
            updateTime(endTime, endTimeText);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_form_top_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            AppDatabase database = AppDatabase.getInstance(getActivity());

            Schedule schedule = new Schedule();

            schedule.setName(nameText.getText().toString());
            schedule.setStart(startDate.atTime(startTime));
            schedule.setEnd(endDate.atTime(endTime));
            schedule.setAllDay(allDaySwitch.isChecked());

            schedule.setPlaceName(placeName);
            schedule.setPlaceId(placeId);
            schedule.setPlaceType(placeType);
            schedule.setPlaceCategoryId(placeCategoryId);
            schedule.setPlaceCategoryName(placeCategoryName);
            schedule.setPlaceLongitude(placeLongitude);
            schedule.setPlaceLatitude(placeLatitude);

            schedule.setVehicleType(carChip.isChecked() ? VehicleType.CAR : VehicleType.TRANSIT);
            schedule.setMemo(memoText.getText().toString());

            database.scheduleDao().insert(schedule);
        }

        getActivity().onBackPressed();
        return true;
    }

}
