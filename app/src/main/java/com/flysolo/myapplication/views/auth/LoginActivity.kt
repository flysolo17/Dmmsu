package com.flysolo.myapplication.views.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.flysolo.myapplication.utils.UiState
import com.danica.ikidsv2.utils.Validation
import com.flysolo.myapplication.databinding.ActivityLoginBinding
import com.flysolo.myapplication.models.Accounts
import com.flysolo.myapplication.models.UserType
import com.flysolo.myapplication.services.auth.AuthServiceImpl
import com.flysolo.myapplication.utils.LoadingDialog
import com.flysolo.myapplication.views.student.StudentmainActivity
import com.flysolo.myapplication.views.teacher.TeacherMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var authService : AuthServiceImpl
    private val  loadingDialog = LoadingDialog(this)
    private val validation = Validation()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        binding.buttonLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            if (!validation.validateEmail(binding.layoutEmail) || !validation.validatePassword(binding.layoutPassword)) {
                return@setOnClickListener
            } else {
                login(email = email,password = password)
            }
        }
        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }
    private fun login(email : String ,password : String) {
        authService.login(email,password) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Logging in....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,"Successfully Logged in!",Toast.LENGTH_SHORT).show()
                    getUserInfo(it.data.uid)
                }
            }
        }
    }
    private fun getUserInfo(uid : String) {
        authService.getUserInfo(uid) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Identifying user....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    updateUI(it.data)
                }
            }
        }
    }
    private fun updateUI(accounts: Accounts) {
        when (accounts.type) {
            UserType.TEACHER -> {
                startActivity(Intent(this,TeacherMainActivity::class.java).putExtra("user",accounts))
            }
            UserType.STUDENTS -> {
                startActivity(Intent(this,StudentmainActivity::class.java).putExtra("user",accounts))
            }
            else -> {
                Toast.makeText(this,"User not found!",Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser!= null) {
            getUserInfo(currentUser.uid)
        }
    }

}