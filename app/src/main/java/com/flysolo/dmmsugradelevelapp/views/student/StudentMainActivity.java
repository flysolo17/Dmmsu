package com.flysolo.dmmsugradelevelapp.views.student;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.ActivityStudentMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class StudentMainActivity extends AppCompatActivity {
    private ActivityStudentMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}