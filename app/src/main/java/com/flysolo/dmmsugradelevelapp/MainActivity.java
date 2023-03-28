package com.flysolo.dmmsugradelevelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.databinding.ActivityMainBinding;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AuthServiceImpl authService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}