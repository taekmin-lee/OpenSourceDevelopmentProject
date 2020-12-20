package com.viewpoints.aischeduler.ui.home;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.kakao.sdk.navi.NaviClient;
import com.kakao.sdk.navi.model.CoordType;
import com.kakao.sdk.navi.model.NaviOption;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.AppDatabase;
import com.viewpoints.aischeduler.data.UserLocationContext;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.airkorea.NearbyStationApiRequest;
import com.viewpoints.aischeduler.data.openapi.airkorea.RealtimeAirPollutionApiRequest;
import com.viewpoints.aischeduler.data.openapi.airkorea.RealtimeAirPollutionResult;
import com.viewpoints.aischeduler.data.openapi.kakao.CoordToAddressApiRequest;
import com.viewpoints.aischeduler.data.openapi.kma.CurrentWeatherApiRequest;
import com.viewpoints.aischeduler.data.openapi.kma.PrecipitationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    protected TextView addressText, temperatureText, precipitationText, humidityText, windText, pm10Text, pm25Text;

    protected NaverMap map;

    protected RecyclerView recyclerView;
    protected TodayScheduleListAdapter adapter;

    protected PathOverlay lastPath;

    protected LiveData<List<Schedule>> schedules;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addressText = view.findViewById(R.id.address_text);
        temperatureText = view.findViewById(R.id.temperature_text);
        precipitationText = view.findViewById(R.id.precipitation_text);
        humidityText = view.findViewById(R.id.humidity_text);
        windText = view.findViewById(R.id.wind_text);
        pm10Text = view.findViewById(R.id.pm10_text);
        pm25Text = view.findViewById(R.id.pm25_text);

        recyclerView = view.findViewById(R.id.recycler_view);

        AppDatabase database = AppDatabase.getInstance(getActivity());

        schedules = database.scheduleDao().getAll();
        schedules.observe(getViewLifecycleOwner(), schedules -> filterSchedules());

        Location location = UserLocationContext.getInstance(getContext()).getLocation();

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new CoordToAddressApiRequest(location,
                response -> addressText.setText(response),
                error -> {
                }
        ));

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new CurrentWeatherApiRequest(location, response -> {
            temperatureText.setText(String.format("%.1f℃", response.getTemperature()));

            if (response.getPrecipitationType() != PrecipitationType.NONE) {
                precipitationText.setText(String.format("%s %.1f㎜", response.getPrecipitationType(), response.getPrecipitationAmount()));
            } else {
                precipitationText.setText("없음");
            }

            humidityText.setText(response.getHumidity() + "%");
            windText.setText(String.format("%s %.0fmph", response.getWindDirectionType(), response.getWindSpeed()));
        }, error -> {
            Log.d("CurrentWeather", "error");
            error.printStackTrace();
        }));

        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new NearbyStationApiRequest(location, response -> {
            if (response.size() > 0) {
                OpenApiContext.getInstance(getContext()).getRequestQueue().add(new RealtimeAirPollutionApiRequest(response.get(0), (Response.Listener<RealtimeAirPollutionResult>) response1 -> {
                    pm10Text.setText(response1.getPm10() + "ppm");
                    pm25Text.setText(response1.getPm25() + "ppm");
                }, (Response.ErrorListener) error -> {
                    Log.d("RealtimeAirPollution", "error");
                    error.printStackTrace();
                }));
            }
        }, error -> {
            Log.d("NearbyStation", "error");
            error.printStackTrace();
        }));

        FragmentManager manager = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            manager.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(map ->
        {
            this.map = map;

            map.setCameraPosition(new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 14));
            filterSchedules();
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterSchedules() {
        Location location = UserLocationContext.getInstance(getContext()).getLocation();

        LocalDateTime now = LocalDateTime.now();
        List<Schedule> today = new ArrayList<>();

        for (Schedule schedule : schedules.getValue()) {
            for (LocalDate d = schedule.getStart().toLocalDate(); d.compareTo(schedule.getEnd().toLocalDate()) <= 0; d = d.plusDays(1)) {
                if (d.getYear() == now.getYear() && d.getMonthValue() == now.getMonthValue() && d.getDayOfMonth() == now.getDayOfMonth()) {
                    today.add(schedule);

                    if (schedule.getPlaceId() != null) {
                        Marker marker = new Marker();
                        marker.setCaptionText(schedule.getName());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA);
                        marker.setSubCaptionText(schedule.getStart().format(formatter));
                        marker.setSubCaptionTextSize(10);
                        marker.setPosition(new LatLng(schedule.getPlaceLatitude(), schedule.getPlaceLongitude()));
                        marker.setMap(map);
                    }
                }
            }
        }

        adapter = new TodayScheduleListAdapter(schedules.getValue());
        adapter.setOnClickListener(new TodayScheduleListAdapter.OnClickListener() {
            @Override
            public void onScheduleClick(View view, Schedule item) {
                Log.d("test", "1");
                Bundle bundle = new Bundle();
                bundle.putSerializable("schedule", item);

                Navigation.findNavController(view).navigate(R.id.navigation_schedule_details, bundle);
            }

            @Override
            public void onRouteButtonClick(View view, Schedule item) {
                if (lastPath != null) {
                    lastPath.setMap(null);
                }

                GeoApiContext context = OpenApiContext.getInstance(getContext()).getGeoApiContext();

                try {
                    DirectionsResult result = DirectionsApi.getDirections(context, location.getLatitude() + "," + location.getLongitude(), item.getPlaceLatitude() + "," + item.getPlaceLongitude()).arrivalTime(item.getStart().atZone(ZoneId.systemDefault()).toInstant()).mode(TravelMode.TRANSIT).await();

                    List<LatLng> coords = new ArrayList<>();

                    for (com.google.maps.model.LatLng point : result.routes[0].overviewPolyline.decodePath()) {
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
                    map.moveCamera(CameraUpdate.fitBounds(LatLngBounds.from(coords), getResources().getDimensionPixelSize(R.dimen.map_padding)).animate(CameraAnimation.Fly));

                    lastPath = path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNavigateButtonClick(View view, Schedule item) {
                startActivity(
                        NaviClient.getInstance().navigateIntent(
                                new com.kakao.sdk.navi.model.Location(item.getPlaceName(), String.valueOf(item.getPlaceLongitude()), String.valueOf(item.getPlaceLatitude())),
                                new NaviOption(CoordType.WGS84)
                        )
                );
            }
        });

        recyclerView.setAdapter(adapter);
    }

}
