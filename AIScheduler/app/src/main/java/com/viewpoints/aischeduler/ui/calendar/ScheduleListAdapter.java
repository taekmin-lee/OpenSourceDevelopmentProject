package com.viewpoints.aischeduler.ui.calendar;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.model.Schedule;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {

    public interface OnClickListener {
        void onScheduleClick(View view, Schedule item);
    }

    protected List<Schedule> items;
    protected ScheduleListAdapter.OnClickListener listener = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView nameText, timeText, locationText;

        public ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.name_text);
            timeText = view.findViewById(R.id.time_text);
            locationText = view.findViewById(R.id.location_text);

            view.setOnClickListener(v -> listener.onScheduleClick(v, items.get(getAdapterPosition())));
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

            locationText.setText("Test Location");
        }
    }

    public ScheduleListAdapter(List<Schedule> items) {
        this.items = items;
    }

    public void setOnClickListener(ScheduleListAdapter.OnClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_item, viewGroup, false);
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
