package com.flysolo.dmmsugradelevelapp.views.auth;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;

import com.flysolo.dmmsugradelevelapp.databinding.ActivityRegisterBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.UserType;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.utils.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private Validation validation = new Validation();
    private AuthServiceImpl authService = new AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
    private LoadingDialog loadingDialog = new LoadingDialog(this);
    private Uri imageUri = null;
    private ActivityResultLauncher<Intent> galleryLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        galleryLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            try {
                if (intent != null) {
                    imageUri = intent.getData();
                    binding.imageProfile.setImageURI(imageUri);
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonAddImage.setOnClickListener(view -> {
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
        binding.buttonLogin.setOnClickListener(view -> finish());

        binding.buttonRegister.setOnClickListener(view -> {
            String name = binding.inputName.getText().toString();
            String email = binding.inputEmail.getText().toString();
            String password = binding.inputPassword.getText().toString();
            String confirmPassword = binding.inputConfirmPassword.getText().toString();
            UserType type = getUserType(binding.radioGroup.getCheckedRadioButtonId());
            if (name.isEmpty() || !validation.isValidName(name)) {
                binding.layoutName.setError("Invalid name");
            } else if(!validation.isValidEmail(email) || email.isEmpty()) {
                binding.layoutEmail.setError("Invalid email");
            } else if (validation.isValidPassword(password) || password.isEmpty()) {
                binding.layoutPassword.setError("Invalid password");
            } else if (!validation.isPasswordMatch(password,confirmPassword)) {
                binding.layoutConfirmPassword.setError("Password don't match");
            } else {
                Accounts accounts = new Accounts("","",name,type,email);
                signup(email,password,accounts);
            }
        });
    }
    private void signup(String email,String password ,Accounts accounts){
        authService.signup(email, password, accounts, new UiState<Accounts>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Signing up...");
            }
            @Override
            public void Successful(Accounts data) {
                loadingDialog.stopLoading();
                if (imageUri != null) {
                    uploadProfile(data.getId(),imageUri,data);
                } else {
                    saveAccount(data);
                }

            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveAccount(Accounts accounts) {
        authService.createAccount(accounts, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving info....");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(RegisterActivity.this, "Successfully created", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private UserType getUserType(int type) {
        UserType user;
        if (R.id.radio_button_1 == type) {
            user = UserType.STUDENT;
        } else {
            user = UserType.TEACHER;
        }
        return user;
    }
    private void uploadProfile(String uid,Uri uri,Accounts accounts) {
        authService.uploadProfile(uid,uri, Constants.getFileExtension(this,uri), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Uploading profile....");

            }
            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                accounts.setProfile(data);
                saveAccount(accounts);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}