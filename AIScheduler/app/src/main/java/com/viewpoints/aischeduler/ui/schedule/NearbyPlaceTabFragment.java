package com.viewpoints.aischeduler.ui.schedule;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.UserLocationContext;
import com.viewpoints.aischeduler.data.model.PlaceType;
import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.model.VehicleType;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;
import com.viewpoints.aischeduler.data.openapi.kakao.CategorySearchApiRequest;

public class NearbyPlaceTabFragment extends Fragment {
    protected Schedule schedule;
    protected Location scheduleLocation;

    protected Chip restaurantChip, tourChip, hotelChip, cafeChip, bankChip, hospitalChip, pharmacyChip, parkingChip, gasStationChip;
    protected RecyclerView recyclerView;
    protected NearbyPlaceListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        schedule = (Schedule) getArguments().getSerializable("schedule");

        scheduleLocation = new Location("dummy");
        scheduleLocation.setLongitude(schedule.getPlaceLongitude());
        scheduleLocation.setLatitude(schedule.getPlaceLatitude());

        View view = inflater.inflate(R.layout.fragment_nearby_places_tab, container, false);

        restaurantChip = view.findViewById(R.id.restaurant_chip);
        restaurantChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.RESTAURANT);
            }
        });

        tourChip = view.findViewById(R.id.tour_chip);
        tourChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.ATTRACTIONS);
            }
        });

        hotelChip = view.findViewById(R.id.hotel_chip);
        hotelChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.ACCOMMODATION);
            }
        });

        cafeChip = view.findViewById(R.id.cafe_chip);
        cafeChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.CAFE);
            }
        });

        bankChip = view.findViewById(R.id.bank_chip);
        bankChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.BANK);
            }
        });

        hospitalChip = view.findViewById(R.id.hospital_chip);
        hospitalChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.HOSPITAL);
            }
        });

        pharmacyChip = view.findViewById(R.id.pharmacy_chip);
        pharmacyChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.PHARMACY);
            }
        });

        parkingChip = view.findViewById(R.id.parking_chip);
        parkingChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.PARKING);
            }
        });

        gasStationChip = view.findViewById(R.id.gas_station_chip);
        gasStationChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadPlaces(PlaceType.GAS_STATION);
            }
        });

        Location location = UserLocationContext.getInstance(getContext()).getLocation();

        if (scheduleLocation.distanceTo(location) >= 20 * 1000) {
            tourChip.setVisibility(View.VISIBLE);
            hotelChip.setVisibility(View.VISIBLE);
        }
        else
        {
            cafeChip.setVisibility(View.VISIBLE);
        }

        if (schedule.getVehicleType() == VehicleType.CAR) {
            parkingChip.setVisibility(View.VISIBLE);
            gasStationChip.setVisibility(View.VISIBLE);
        }

        switch (schedule.getPlaceType())
        {
            case AGENCY:
            case PUBLIC_OFFICE:
                bankChip.setVisibility(View.VISIBLE);
                break;
            case HOSPITAL:
                pharmacyChip.setVisibility(View.VISIBLE);
                break;
            case PHARMACY:
                hospitalChip.setVisibility(View.VISIBLE);
                break;
        }

        recyclerView = view.findViewById(R.id.recycler_view);

        loadPlaces(PlaceType.RESTAURANT);
        return view;
    }

    private void loadPlaces(PlaceType placeType) {
        OpenApiContext.getInstance(getContext()).getRequestQueue().add(new CategorySearchApiRequest(placeType, scheduleLocation, 2000,
                response -> {
                    adapter = new NearbyPlaceListAdapter(response);
                    recyclerView.setAdapter(adapter);

                    Log.d("NearbyPlace", "success1");
                },
                error -> {
                    Log.d("NearbyPlace", "error1 " + error);
                }
        ));
    }

}
