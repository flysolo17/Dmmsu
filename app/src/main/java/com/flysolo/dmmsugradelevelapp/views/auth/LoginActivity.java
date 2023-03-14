package com.flysolo.dmmsugradelevelapp.views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.ActivityLoginBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.UserType;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthService;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.utils.Validation;
import com.flysolo.dmmsugradelevelapp.views.dialogs.ChooseClassDialog;
import com.flysolo.dmmsugradelevelapp.views.student.StudentMainActivity;
import com.flysolo.dmmsugradelevelapp.views.teacher.TeacherMainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private final Validation validation = new Validation();
    private final LoadingDialog loadingDialog = new LoadingDialog(this);
    private final AuthServiceImpl authService =new AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonLogin.setOnClickListener(view -> {
          String email = binding.inputEmail.getText().toString();
          String password = binding.inputPassword.getText().toString();
          if (!validation.isValidEmail(email) || email.isEmpty()) {
              binding.layoutEmail.setError("Invalid email");
          } else if (password.isEmpty()) {
              binding.layoutPassword.setError("Invalid password");
          } else {
              login(email,password);
          }
        });
        binding.buttonForgotPassword.setOnClickListener(view -> {
            View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password,binding.getRoot(),false);
            EditText editText = dialog.findViewById(R.id.edtEmail);
            new MaterialAlertDialogBuilder(this)
                    .setView(dialog)
                    .setTitle("Forgot Password")
                    .setPositiveButton("Send", (dialogInterface, i) -> {
                        String email =editText.getText().toString();
                        if (!validation.isValidEmail(email)) {
                            editText.setError("Invalid email");
                        } else {
                            forgotPassword(email);
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });
        binding.buttonRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));
    }
    private void forgotPassword(String email) {
        authService.resetPassword(email, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Sending reset password link..");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void login(String email ,String password) {

        authService.login(email, password, new UiState<FirebaseUser>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Logging in...");
            }

            @Override
            public void Successful(FirebaseUser data) {
                loadingDialog.stopLoading();
                Toast.makeText(LoginActivity.this, "Successfully Logged in!", Toast.LENGTH_SHORT).show();
                getUserInfo(data.getUid());
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getUserInfo(String uid) {

        authService.getAccount(uid, new UiState<Accounts>() {
            @Override
            public void Loading() {
               loadingDialog.showLoadingDialog("Identifying user...");
            }

            @Override
            public void Successful(Accounts data) {
                loadingDialog.stopLoading();
                if (data.getType() == UserType.STUDENT) {
                    ChooseClassDialog classDialog = ChooseClassDialog.newInstance(data.getId());
                    if (!classDialog.isAdded()){
                        classDialog.show(getSupportFragmentManager(),"Choose class");
                    }
                } else if(data.getType() == UserType.TEACHER) {
                    startActivity(new Intent(LoginActivity.this, TeacherMainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "unidentified user!", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                new MaterialAlertDialogBuilder(binding.getRoot().getContext())
                        .setMessage(message)
                        .show();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getUserInfo(user.getUid());
        }
    }
}