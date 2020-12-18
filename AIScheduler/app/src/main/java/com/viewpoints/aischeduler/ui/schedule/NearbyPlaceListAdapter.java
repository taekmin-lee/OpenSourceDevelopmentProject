package com.viewpoints.aischeduler.ui.schedule;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kakao.PlaceDetailsApiRequest;
import com.viewpoints.aischeduler.data.openapi.kakao.PlaceSearchResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class NearbyPlaceListAdapter extends RecyclerView.Adapter<NearbyPlaceListAdapter.ViewHolder> {

    public interface OnClickListener {
        void onPhoneClickButtonClick(View view, PlaceSearchResult item);

        void onItemClickButtonClick(View view, PlaceSearchResult item);
    }

    protected List<PlaceSearchResult> items;
    protected OnClickListener listener = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected final ImageView image;
        protected final TextView nameText, categoryText, phoneText, distanceText, detailsNameText, detailsText, openHourNameText, openHourText;

        protected final View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            image = view.findViewById(R.id.image);

            nameText = view.findViewById(R.id.name_text);
            categoryText = view.findViewById(R.id.category_text);
            phoneText = view.findViewById(R.id.phone_text);
            distanceText = view.findViewById(R.id.distance_text);
            detailsNameText = view.findViewById(R.id.details_name_text);
            detailsText = view.findViewById(R.id.details_text);
            openHourNameText = view.findViewById(R.id.open_hour_name_text);
            openHourText = view.findViewById(R.id.open_hour_text);

            view.setOnClickListener(v -> listener.onItemClickButtonClick(v, items.get(getAdapterPosition())));
            phoneText.setOnClickListener(v -> listener.onPhoneClickButtonClick(v, items.get(getAdapterPosition())));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(PlaceSearchResult item) {
            nameText.setText(item.getName());
            categoryText.setText(item.getCategoryText());
            phoneText.setText(item.getPhone());
            distanceText.setText(String.format("%.1f㎞", item.getDistance() / 1000.0));

            OpenApiContext.getInstance(null).getRequestQueue().add(new PlaceDetailsApiRequest(item.getId(),
                    response -> {
                        if (response.getPhotoUrl() != null) {
                            Glide.with(view).load(response.getPhotoUrl()).into(image);
                        }

                        categoryText.setText(response.getCategoryName());

                        if (response.getOpenHours().size() > 0) {
                            openHourNameText.setVisibility(View.VISIBLE);

                            String openHours = "";

                            for (Map.Entry<String, String> time : response.getOpenHours().entrySet()) {
                                if (openHours.length() > 0) {
                                    openHours += ", ";
                                }

                                openHours += String.format(time.getKey() + ": " + time.getValue());
                            }

                            openHourText.setText(openHours);
                            openHourText.setVisibility(View.VISIBLE);
                        }

                        if (response.getMenus().size() > 0) {
                            detailsNameText.setText(item.getCategoryCode().equals("FD6") ? "메뉴" : "가격");
                            detailsNameText.setVisibility(View.VISIBLE);

                            String details = "";
                            int count = 0;

                            for (Map.Entry<String, String> menu : response.getMenus().entrySet()) {
                                if (++count > 3) {
                                    details += " 등";
                                    break;
                                }

                                if (details.length() > 0) {
                                    details += ", ";
                                }

                                details += String.format("%s(%s)", menu.getKey(), menu.getValue());
                            }

                            detailsText.setText(details);
                            detailsText.setVisibility(View.VISIBLE);
                        }
                        Log.d("place details", "suceess");
                    },
                    error -> {
                        Log.d("place details", error.getMessage());
                        error.printStackTrace();
                    }
            ));
        }
    }

    public NearbyPlaceListAdapter(List<PlaceSearchResult> items) {
        this.items = items;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nearby_place_item, viewGroup, false);
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
