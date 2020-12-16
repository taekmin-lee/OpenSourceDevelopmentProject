package com.viewpoints.aischeduler.ui.calendar;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.openapi.kakao.KeywordSearchResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceSearchResultListAdapter extends RecyclerView.Adapter<PlaceSearchResultListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onLocationButtonClick(View view, KeywordSearchResult item);

        void onSelectButtonClick(View view, KeywordSearchResult item);
    }

    protected List<KeywordSearchResult> items;
    protected OnItemClickListener listener = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView nameText, categoryText, addressText;
        protected final Button locationButton, selectButton;

        public ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.name_text);
            categoryText = view.findViewById(R.id.category_text);
            addressText = view.findViewById(R.id.address_text);

            locationButton = view.findViewById(R.id.location_button);
            selectButton = view.findViewById(R.id.select_button);

            locationButton.setOnClickListener(v -> listener.onLocationButtonClick(v, items.get(getAdapterPosition())));
            selectButton.setOnClickListener(v -> listener.onSelectButtonClick(v, items.get(getAdapterPosition())));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(KeywordSearchResult item) {
            nameText.setText(item.getName());
            categoryText.setText(item.getCategory());
            addressText.setText(item.getAddress());
        }
    }

    public PlaceSearchResultListAdapter(List<KeywordSearchResult> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.place_search_result_item, viewGroup, false);
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
