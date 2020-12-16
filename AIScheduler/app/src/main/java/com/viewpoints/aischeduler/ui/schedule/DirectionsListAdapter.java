package com.viewpoints.aischeduler.ui.schedule;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TransitLine;
import com.google.maps.model.TravelMode;
import com.viewpoints.aischeduler.R;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DirectionsListAdapter extends RecyclerView.Adapter<DirectionsListAdapter.ViewHolder> {
    protected DirectionsRoute[] items;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView optimalText, durationText, timeText, busLineText, busStopText, destinationStopText;

        public ViewHolder(View view) {
            super(view);
            optimalText = view.findViewById(R.id.optimal_text);
            durationText = view.findViewById(R.id.duration_text);
            timeText = view.findViewById(R.id.time_text);

            busLineText = view.findViewById(R.id.bus_line_text);
            busStopText = view.findViewById(R.id.bus_stop_text);
            destinationStopText = view.findViewById(R.id.destination_stop_text);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(DirectionsRoute item) {
            optimalText.setVisibility(getAdapterPosition() == 0 ? View.VISIBLE : View.GONE);
            durationText.setText(item.legs[0].duration.humanReadable);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA);
            timeText.setText(item.legs[0].departureTime.format(formatter) + " ~ " + item.legs[0].arrivalTime.format(formatter));

            boolean isFirst = true;
            String lastStop = "";

            for (DirectionsStep step : item.legs[0].steps) {
                if (step.travelMode == TravelMode.TRANSIT) {
                    if (isFirst) {
                        TransitLine line = step.transitDetails.line;
                        busLineText.setText(line.shortName != null ? line.shortName : line.name);
                        busStopText.setText(step.transitDetails.departureStop.name);
                    }

                    lastStop = step.transitDetails.arrivalStop.name;
                }
            }

            destinationStopText.setText(lastStop);
        }
    }

    public DirectionsListAdapter(DirectionsRoute[] items) {
        this.items = items;
    }

    @NotNull
    @Override
    public DirectionsListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.directions_summary_item, viewGroup, false);
        return new DirectionsListAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(DirectionsListAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.bind(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
