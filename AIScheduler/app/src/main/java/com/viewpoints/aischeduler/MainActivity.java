package com.viewpoints.aischeduler;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.viewpoints.aischeduler.data.UserLocationContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_calendar, R.id.navigation_explore).build();

        NavController controller = Navigation.findNavController(this, R.id.host_fragment);
        controller.addOnDestinationChangedListener((c, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.navigation_home:
                case R.id.navigation_calendar:
                case R.id.navigation_explore:
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    break;
                default:
                    bottomNavigationView.setVisibility(View.GONE);
                    break;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }

        UserLocationContext.getInstance(this);

//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, controller);
    }

}
