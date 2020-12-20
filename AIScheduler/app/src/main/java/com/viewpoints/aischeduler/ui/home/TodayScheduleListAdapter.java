package com.viewpoints.aischeduler.ui.home;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.model.VehicleType;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TodayScheduleListAdapter extends RecyclerView.Adapter<TodayScheduleListAdapter.ViewHolder> {

    public interface OnClickListener {
        void onScheduleClick(View view, Schedule item);

        void onRouteButtonClick(View view, Schedule item);

        void onNavigateButtonClick(View view, Schedule item);
    }

    protected List<Schedule> items;
    protected OnClickListener listener = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView nameText, timeText, locationText;
        protected ImageView locationIcon, vehicleIcon;
        protected Button detailsButton, routeButton, navigateButton;

        public ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.name_text);
            timeText = view.findViewById(R.id.time_text);
            locationIcon = view.findViewById(R.id.location_icon);
            locationText = view.findViewById(R.id.location_text);
            vehicleIcon = view.findViewById(R.id.vehicle_icon);

            detailsButton = view.findViewById(R.id.details_button);
            routeButton = view.findViewById(R.id.route_button);
            navigateButton = view.findViewById(R.id.navigate_button);

            detailsButton.setOnClickListener(v -> listener.onScheduleClick(v, items.get(getAdapterPosition())));
            routeButton.setOnClickListener(v -> listener.onRouteButtonClick(v, items.get(getAdapterPosition())));
            navigateButton.setOnClickListener(v -> listener.onNavigateButtonClick(v, items.get(getAdapterPosition())));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(Schedule item) {
            nameText.setText(item.getName());

            if (item.isAllDay()) {
                timeText.setText("종일");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA);
                timeText.setText(item.getStart().format(formatter) + " ~ " + item.getEnd().format(formatter));
            }

            if (item.getPlaceId() != null) {
                locationIcon.setVisibility(View.VISIBLE);
                locationText.setText(item.getPlaceName());
                locationText.setVisibility(View.VISIBLE);
            }

            vehicleIcon.setImageResource(item.getVehicleType() == VehicleType.CAR ? R.drawable.ic_round_directions_car : R.drawable.ic_round_directions_bus);

            routeButton.setVisibility(item.getPlaceId() != null ? View.VISIBLE : View.GONE);
            navigateButton.setVisibility(item.getPlaceId() != null && item.getVehicleType() == VehicleType.CAR ? View.VISIBLE : View.GONE);
        }
    }

    public TodayScheduleListAdapter(List<Schedule> items) {
        this.items = items;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
