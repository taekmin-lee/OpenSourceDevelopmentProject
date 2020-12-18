package com.viewpoints.aischeduler.ui.schedule;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.maps.model.DirectionsRoute;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.model.Schedule;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteDetailsFragment extends Fragment {
    protected Schedule schedule;
    protected DirectionsRoute route;

    protected Toolbar toolbar;
    protected NaverMap map;

    protected RecyclerView recyclerView;
    protected RouteStepListAdapter adapter;

    protected Marker origin, destination;
    protected PathOverlay lastPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        schedule = (Schedule) getArguments().getSerializable("schedule");
        route = (DirectionsRoute) getArguments().getSerializable("route");

        View view = inflater.inflate(R.layout.fragment_route_details, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recycler_view);

        FragmentManager manager = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            manager.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(map ->
        {
            this.map = map;
            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true);
            map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);
            map.setIndoorEnabled(true);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA);

            origin = new Marker();
            origin.setPosition(new LatLng(route.legs[0].startLocation.lat, route.legs[0].startLocation.lng));
            origin.setCaptionText("현재 위치");
            origin.setSubCaptionText(route.legs[0].departureTime.format(formatter));
            origin.setSubCaptionTextSize(10);
            origin.setSubCaptionColor(Color.DKGRAY);
            origin.setMap(map);

            destination = new Marker();
            destination.setPosition(new LatLng(route.legs[0].endLocation.lat, route.legs[0].endLocation.lng));
            destination.setCaptionText(schedule.getPlaceName());
            destination.setSubCaptionText(route.legs[0].arrivalTime.format(formatter));
            destination.setSubCaptionTextSize(10);
            destination.setSubCaptionColor(Color.DKGRAY);
            destination.setMap(map);

            {
                List<LatLng> coords = new ArrayList<>();

                for (com.google.maps.model.LatLng coord : route.overviewPolyline.decodePath()) {
                    coords.add(new LatLng(coord.lat, coord.lng));
                }

                PathOverlay path = new PathOverlay();
                path.setCoords(coords);
                path.setWidth(getResources().getDimensionPixelSize(R.dimen.path_overlay_width));
                path.setOutlineWidth(0);
                path.setColor(Color.BLUE);
                path.setPatternImage(OverlayImage.fromResource(R.drawable.path_pattern));
                path.setPatternInterval(getResources().getDimensionPixelSize(R.dimen.overlay_pattern_interval) * 2);

                path.setMap(map);
                lastPath = path;

                map.moveCamera(CameraUpdate.fitBounds(LatLngBounds.from(coords), getResources().getDimensionPixelSize(R.dimen.map_padding)).animate(CameraAnimation.Fly));
            }

            adapter = new RouteStepListAdapter(route.legs[0].steps, map);
            adapter.setOnClickListener((v, item) -> {
                lastPath.setMap(null);

                origin.setPosition(new LatLng(item.startLocation.lat, item.startLocation.lng));
                origin.setCaptionText(item.transitDetails != null ? item.transitDetails.departureStop.name + " 승차" : "도보 시작");
                origin.setSubCaptionText(item.transitDetails != null ? item.transitDetails.departureTime.format(formatter) : "");

                destination.setPosition(new LatLng(item.endLocation.lat, item.endLocation.lng));
                destination.setCaptionText(item.transitDetails != null ? item.transitDetails.arrivalStop.name + " 하차" : "도보 종료");
                destination.setSubCaptionText(item.transitDetails != null ? item.transitDetails.arrivalTime.format(formatter) : "");

                List<LatLng> coords = new ArrayList<>();

                for (com.google.maps.model.LatLng point : item.polyline.decodePath()) {
                    coords.add(new LatLng(point.lat, point.lng));
                }

                PathOverlay path = new PathOverlay();
                path.setCoords(coords);
                path.setWidth(getResources().getDimensionPixelSize(R.dimen.path_overlay_width));
                path.setOutlineWidth(0);
                path.setColor(Color.BLUE);
                path.setPatternImage(OverlayImage.fromResource(R.drawable.path_pattern));
                path.setPatternInterval(getResources().getDimensionPixelSize(R.dimen.overlay_pattern_interval) * 2);

                path.setMap(map);
                lastPath = path;

                map.moveCamera(CameraUpdate.fitBounds(LatLngBounds.from(coords), getResources().getDimensionPixelSize(R.dimen.map_padding)).animate(CameraAnimation.Fly));
            });

            recyclerView.setAdapter(adapter);
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
