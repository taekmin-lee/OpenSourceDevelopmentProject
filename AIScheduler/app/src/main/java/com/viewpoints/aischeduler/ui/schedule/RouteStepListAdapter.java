package com.viewpoints.aischeduler.ui.schedule;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;
import com.naver.maps.map.NaverMap;
import com.viewpoints.aischeduler.R;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RouteStepListAdapter extends RecyclerView.Adapter<RouteStepListAdapter.ViewHolder> {

    public interface OnClickListener {
        void onItemClick(View view, DirectionsStep item);
    }

    protected DirectionsStep[] items;
    protected RouteStepListAdapter.OnClickListener listener = null;

    protected NaverMap map;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected final ImageView vehicleIcon;
        protected final TextView durationText, startText, startTimeText, detailsText, endText, endTimeText;

        public ViewHolder(View view) {
            super(view);

            vehicleIcon = view.findViewById(R.id.vehicle_icon);
            durationText = view.findViewById(R.id.duration_text);
            startText = view.findViewById(R.id.start_text);
            startTimeText = view.findViewById(R.id.start_time_text);
            detailsText = view.findViewById(R.id.details_text);
            endText = view.findViewById(R.id.end_text);
            endTimeText = view.findViewById(R.id.end_time_text);

            view.setOnClickListener(v -> listener.onItemClick(v, items[getAdapterPosition()]));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(DirectionsStep item) {
            if (item.travelMode == TravelMode.WALKING) {
                vehicleIcon.setImageResource(R.drawable.ic_round_directions_walk);
                startText.setText("도보");
                startTimeText.setVisibility(View.GONE);
                detailsText.setText(item.distance.humanReadable);
                endText.setVisibility(View.GONE);
                endTimeText.setVisibility(View.GONE);

            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA);
                startTimeText.setText(item.transitDetails.departureTime.format(formatter));
                endTimeText.setText(item.transitDetails.arrivalTime.format(formatter));
                startText.setText(item.transitDetails.departureStop.name + " 승차");
                endText.setText(item.transitDetails.arrivalStop.name + " 하차");

                switch (item.transitDetails.line.vehicle.type) {
                    case BUS:
                        vehicleIcon.setImageResource(R.drawable.ic_round_directions_bus);
                        detailsText.setText(item.transitDetails.line.shortName);
                        break;
                    case SUBWAY:
                        vehicleIcon.setImageResource(R.drawable.ic_round_directions_subway);
                        detailsText.setText(item.transitDetails.line.shortName);
                        break;
                    case HEAVY_RAIL:
                        vehicleIcon.setImageResource(R.drawable.ic_round_directions_railway);
                        detailsText.setText(item.transitDetails.line.name);
                        break;
                }
            }

            durationText.setText(item.duration.humanReadable);
        }
    }

    public RouteStepListAdapter(DirectionsStep[] items, NaverMap map) {
        this.items = items;
        this.map = map;
    }

    public void setOnClickListener(RouteStepListAdapter.OnClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public RouteStepListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_step_item, viewGroup, false);
        return new RouteStepListAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(RouteStepListAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.bind(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
