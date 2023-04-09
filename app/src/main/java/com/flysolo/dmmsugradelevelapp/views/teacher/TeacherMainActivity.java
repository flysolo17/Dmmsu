package com.flysolo.dmmsugradelevelapp.views.teacher;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.ActivityTeacherMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TeacherMainActivity extends AppCompatActivity {
    private ActivityTeacherMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTeacherMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setElevation(1f);
        NavHostFragment navHostFragment =(NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(R.id.navigation_home,R.id.navigation_scoreboard,R.id.navigation_account).build();
            NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView,navController);
        }
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (R.id.navigation_home == navDestination.getId()) {
                getSupportActionBar().hide();
                showBottomNav();
            } else if (R.id.navigation_scoreboard == navDestination.getId()) {
                showBottomNav();
            }else if (R.id.navigation_account == navDestination.getId()) {
                showBottomNav();
            } else {
                getSupportActionBar().show();
                hideBottomNav();
            }
        });

    }
    private void showBottomNav() {
        binding.bottomAppBar.performShow(true);
        binding.bottomAppBar.setHideOnScroll(true);

    }

    private void hideBottomNav() {
        binding.bottomAppBar.performHide(true);
        binding.bottomAppBar.setHideOnScroll(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() ||super.onNavigateUp();
    }
    public void updateToolbar(int color) {
        binding.toolbar.setBackgroundColor(Color.BLACK);
    }
}