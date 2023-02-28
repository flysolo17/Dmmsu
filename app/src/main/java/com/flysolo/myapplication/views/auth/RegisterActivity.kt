package com.flysolo.myapplication.views.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.danica.ikidsv2.utils.Validation
import com.flysolo.myapplication.R
import com.flysolo.myapplication.databinding.ActivityRegisterBinding
import com.flysolo.myapplication.models.Accounts
import com.flysolo.myapplication.models.UserType
import com.flysolo.myapplication.services.auth.AuthServiceImpl
import com.flysolo.myapplication.utils.LoadingDialog
import com.flysolo.myapplication.utils.Registration
import com.flysolo.myapplication.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val validation = Validation()
    private val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private val loadingDialog = LoadingDialog(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonAddImage.setOnClickListener {
            Toast.makeText(this,"This function is not yet implemented!",Toast.LENGTH_SHORT).show()
        }
        binding.buttonRegister.setOnClickListener {
            val fullname = binding.inputName.text.toString()
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            val confirmPassword = binding.inputConfirmPassword.text.toString()
            val type = getUserType(binding.radioGroup.checkedRadioButtonId)
            when(validation.validateRegistration(fullname,email,password,confirmPassword)) {
                Registration.INVALID_NAME -> {
                    binding.layoutName.error = "Invalid Name"
                }
                Registration.INVALID_EMAIL ->  {
                    binding.layoutEmail.error = "Invalid Email"
                }
                Registration.INVALID_PASSWORD -> {
                    binding.layoutPassword.error = "Invalid Password"
                }
                Registration.PASSWORD_NOT_MATCH -> {
                    binding.layoutConfirmPassword.error = "Password Didn't macth"
                }
                Registration.SUCCESSFUL -> {
                    binding.layoutName.error = ""
                    binding.layoutEmail.error = ""
                    binding.layoutPassword.error = ""
                    binding.layoutConfirmPassword.error = ""
                    val accounts = Accounts("","",fullname,type,email)
                    signup(email,password,accounts)
                }
            }
        }
    }
    private fun signup(email : String , password : String ,accounts: Accounts) {
        authService.signup(email,password,accounts) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Creating account")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    saveAccount(it.data)
                }
            }
        }
    }
    private fun saveAccount(accounts: Accounts) {
        authService.createAccount(accounts) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Saving account")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    finish()
                }
            }
        }
    }

    private fun getUserType(type : Int) : UserType {
        var user = UserType.STUDENTS
        when(type) {
            R.id.radio_button_1 -> {
                user = UserType.STUDENTS
            }
            R.id.radio_button_2 -> {
                user = UserType.TEACHER
            }
        }
        return  user
    }
}