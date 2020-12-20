package com.viewpoints.aischeduler.ui.explore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.AppDatabase;
import com.viewpoints.aischeduler.data.UserLocationContext;
import com.viewpoints.aischeduler.data.model.PlaceType;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kakao.CategorySearchApiRequest;
import com.viewpoints.aischeduler.data.openapi.kakao.PlaceDetailsApiRequest;
import com.viewpoints.aischeduler.data.openapi.kakao.PlaceSearchResult;
import com.viewpoints.aischeduler.ui.schedule.NearbyPlaceListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    protected TextView categoryText;

    protected RecyclerView recyclerView;
    protected NearbyPlaceListAdapter adapter;

    protected Integer categoryId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        categoryText = view.findViewById(R.id.category_text);

        recyclerView = view.findViewById(R.id.recycler_view);

        Schedule schedule = AppDatabase.getInstance(getContext()).scheduleDao().getMostFrequentPlace();
        categoryId = schedule.getPlaceCategoryId();
        categoryText.setText(schedule.getPlaceCategoryName());

        loadPlaces();
        return view;
    }

    private void loadPlaces() {
        List<PlaceSearchResult> recommendations = new ArrayList<>();

        adapter = new NearbyPlaceListAdapter(recommendations);
        adapter.setOnClickListener(new NearbyPlaceListAdapter.OnClickListener() {
            @Override
            public void onPhoneClickButtonClick(View view, PlaceSearchResult item) {

            }

            @Override
            public void onItemClickButtonClick(View view, PlaceSearchResult item) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://place?id=" + item.getId()));
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        //for (int p = 1; p <= 10; p++)
        {
            OpenApiContext.getInstance(getContext()).getRequestQueue().add(new CategorySearchApiRequest(PlaceType.RESTAURANT, UserLocationContext.getInstance(getContext()).getLocation(), 10 * 1000,
                    response -> {
                        for (PlaceSearchResult result : response) {
                            OpenApiContext.getInstance(getContext()).getRequestQueue().add(new PlaceDetailsApiRequest(result.getId(), response1 -> {
                                if (response1.getCategoryId().equals(categoryId)) {
                                    recommendations.add(result);
                                    adapter.notifyDataSetChanged();
                                }
                            }, error -> {
                            }));
                        }

                        Log.d("NearbyPlace", "success1");
                    },
                    error -> {
                        Log.d("NearbyPlace", "error1 " + error);
                    }
            ));
        }
    }
}
