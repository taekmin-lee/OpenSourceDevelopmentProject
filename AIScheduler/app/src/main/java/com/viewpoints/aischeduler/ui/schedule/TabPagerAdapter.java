package com.viewpoints.aischeduler.ui.schedule;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.viewpoints.aischeduler.data.model.Schedule;

public class TabPagerAdapter extends FragmentStateAdapter {
    protected Schedule schedule;

    public TabPagerAdapter(Fragment fragment, Schedule schedule) {
        super(fragment);
        this.schedule = schedule;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new ScheduleInfoTabFragment();
                break;
            case 1:
                fragment = new WeatherTabFragment();
                break;
            case 2:
                fragment = new DirectionsTabFragment();
                break;
            case 3:
                fragment = new NearbyPlaceTabFragment();
                break;
            default:
                return null;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("schedule", schedule);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
