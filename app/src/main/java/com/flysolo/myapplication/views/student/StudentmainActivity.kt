package com.flysolo.myapplication.views.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flysolo.myapplication.R
import com.flysolo.myapplication.databinding.ActivityStudentmainBinding
import com.google.firebase.auth.FirebaseAuth

class StudentmainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStudentmainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentmainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogout.setOnClickListener {
            logout()
        }
    }
    private fun logout() {
        FirebaseAuth.getInstance().signOut().also {
            finish()
        }
    }
}