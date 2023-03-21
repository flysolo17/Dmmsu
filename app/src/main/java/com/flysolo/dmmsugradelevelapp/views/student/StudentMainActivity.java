package com.flysolo.dmmsugradelevelapp.views.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.ActivityStudentMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class StudentMainActivity extends AppCompatActivity {
    private ActivityStudentMainBinding binding;
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
/*        binding.buttonLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });*/

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setElevation(1f);
        NavHostFragment navHostFragment =(NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(R.id.navigation_classes,R.id.navigation_scoreboard,R.id.navigation_account).build();
            NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView,navController);
        }
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (R.id.navigation_classes == navDestination.getId()) {
                showBottomNav();
            } else if (R.id.navigation_scoreboard == navDestination.getId()) {
                showBottomNav();
            }else if (R.id.navigation_account == navDestination.getId()) {
                showBottomNav();
            } else {
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
}