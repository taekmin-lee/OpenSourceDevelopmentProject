package com.viewpoints.aischeduler.ui.calendar;

import android.location.Location;
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

import com.android.volley.Response;
import com.google.android.material.appbar.MaterialToolbar;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.UserLocationContext;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kakao.KeywordSearchApiRequest;
import com.viewpoints.aischeduler.data.openapi.kakao.PlaceSearchResult;

import java.util.ArrayList;
import java.util.List;

public class PlaceDialogFragment extends Fragment {

    public interface OnClickListener {
        void addOnPositiveButtonClickListener(PlaceSearchResult result);
    }

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

        FragmentManager manager = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            manager.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(map ->
        {
            this.map = map;

            Location location = UserLocationContext.getInstance(getActivity()).getLocation();
            map.setCameraPosition(new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 14));
        });

        toolbar = view.findViewById(R.id.toolbar);

        searchText = view.findViewById(R.id.search_text);
        searchButton = view.findViewById(R.id.search_button);
        recyclerView = view.findViewById(R.id.search_list);

        searchButton.setOnClickListener(v -> {
            OpenApiContext.getInstance(view.getContext()).getRequestQueue().add(new KeywordSearchApiRequest(searchText.getText().toString(),
                    UserLocationContext.getInstance(getActivity()).getLocation(),
                    (Response.Listener<List<PlaceSearchResult>>) response -> {
                        for (Marker marker : markers) {
                            marker.setMap(null);
                        }

                        markers.clear();

                        adapter = new PlaceSearchResultListAdapter(response);

                        List<LatLng> coords = new ArrayList<>();

                        for (PlaceSearchResult item : response) {
                            Marker marker = new Marker();
                            LatLng coord = new LatLng(item.getLatitude(), item.getLongitude());
                            marker.setPosition(coord);
                            marker.setCaptionText(item.getName());

                            marker.setMap(map);

                            markers.add(marker);
                            coords.add(coord);
                        }

                        if (response.size() > 0) {
                            map.moveCamera(CameraUpdate.fitBounds(LatLngBounds.from(coords), getResources().getDimensionPixelSize(R.dimen.map_padding)).animate(CameraAnimation.Easing));
                        }

                        adapter.setOnClickListener(new PlaceSearchResultListAdapter.OnClickListener() {
                            @Override
                            public void onLocationButtonClick(View v, PlaceSearchResult item) {
                                map.moveCamera(CameraUpdate.scrollAndZoomTo(new LatLng(item.getLatitude(), item.getLongitude()), 14).animate(CameraAnimation.Fly));
                            }

                            @Override
                            public void onSelectButtonClick(View v, PlaceSearchResult item) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("place", item);

                                getParentFragmentManager().setFragmentResult("placeSearch", bundle);
                                getActivity().onBackPressed();
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
