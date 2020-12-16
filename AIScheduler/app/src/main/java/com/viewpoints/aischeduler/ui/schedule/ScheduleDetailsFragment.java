package com.viewpoints.aischeduler.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.viewpoints.aischeduler.R;

public class ScheduleDetailsFragment extends Fragment {

    protected MaterialToolbar toolbar;

    protected TabPagerAdapter adapter;
    protected ViewPager2 viewPager;

    protected int scheduleId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        scheduleId = bundle.getInt("id");

        View view = inflater.inflate(R.layout.fragment_schedule_details, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        adapter = new TabPagerAdapter(this, scheduleId);

        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        String[] names = {"기본 정보", "날씨", "길찾기", "주변 장소"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(names[position])).attach();

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_details_top_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu:
                Bundle bundle = new Bundle();
                bundle.putInt("id", scheduleId);

                Navigation.findNavController(this.getView()).navigate(R.id.navigation_schedule_form, bundle);
                break;
            case R.id.delete_menu:

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
