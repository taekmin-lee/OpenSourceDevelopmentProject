package com.viewpoints.aischeduler.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kakao.KeywordSearchApiRequest;
import com.viewpoints.aischeduler.data.openapi.kakao.KeywordSearchResult;

import java.util.ArrayList;
import java.util.List;

public class PlaceDialogFragment extends Fragment {

    protected MaterialToolbar toolbar;

    protected NaverMap map;

    protected EditText searchText;
    protected ImageButton searchButton;

    protected RecyclerView recyclerView;
    protected PlaceSearchResultListAdapter adapter;

    protected List<Marker> markers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_dialog, container, false);

        FragmentManager manager = getActivity().getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            manager.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(m -> map = m);

        toolbar = view.findViewById(R.id.toolbar);

        searchText = view.findViewById(R.id.search_text);
        searchButton = view.findViewById(R.id.search_button);
        recyclerView = view.findViewById(R.id.search_list);

        searchButton.setOnClickListener(v -> {
            OpenApiContext.getInstance(view.getContext()).getRequestQueue().add(new KeywordSearchApiRequest(searchText.getText().toString(),
                    r -> {
                        for (Marker marker : markers) {
                            marker.setMap(null);
                        }

                        markers.clear();

                        adapter = new PlaceSearchResultListAdapter(r);

                        for (KeywordSearchResult result : r) {
                            Marker marker = new Marker();
                            marker.setPosition(new LatLng(result.getLatitude(), result.getLongitude()));
                            marker.setCaptionText(result.getName());
                            marker.setMap(map);

                            markers.add(marker);
                        }

                        adapter.setOnItemClickListener(new PlaceSearchResultListAdapter.OnItemClickListener() {
                            @Override
                            public void onLocationButtonClick(View view, KeywordSearchResult item) {
                                map.moveCamera(CameraUpdate.scrollTo(new LatLng(item.getLatitude(), item.getLongitude())).animate(CameraAnimation.Fly));
                            }

                            @Override
                            public void onSelectButtonClick(View view, KeywordSearchResult item) {

                            }
                        });

                        recyclerView.setAdapter(adapter);
                    },
                    l -> {
                    }
            ));
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return true;
    }

}
