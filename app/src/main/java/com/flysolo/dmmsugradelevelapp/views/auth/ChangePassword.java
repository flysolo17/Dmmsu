package com.flysolo.dmmsugradelevelapp.views.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentChangePasswordBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class ChangePassword extends Fragment {

    private FragmentChangePasswordBinding binding;
    private Accounts accounts;
    private LoadingDialog loadingDialog;
    private AuthServiceImpl authService;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accounts = ChangePasswordArgs.fromBundle(
                    getArguments()
            ).getAccount();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentChangePasswordBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        authService = new AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonChangePassword.setOnClickListener(view1 -> {
            String current = binding.inputCurrentPassword.getText().toString();
            String newPassword = binding.inputNewPassword.getText().toString();
            String confirmPassword= binding.inputConfirmPassword.getText().toString();
            if (current.isEmpty()) {
                binding.layoutCurrentPassword.setError("This field is required!");
            } else if (newPassword.isEmpty()){
                binding.layoutNewPassword.setError("This field is required!");
            }  else if (!newPassword.equals(confirmPassword)){
                binding.layoutConfirmPassword.setError("password don't match!");
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    reAuthenticateAccount(user,current,newPassword);
                }

            }
        });
    }
    private void reAuthenticateAccount(FirebaseUser user,String current ,String newPasword){
        authService.reAuthenticateAccount(user, accounts.getEmail(), current, new UiState<FirebaseUser>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Authenticationg old password...");
            }

            @Override
            public void Successful(FirebaseUser data) {
                loadingDialog.stopLoading();
                changePassword(data,newPasword);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                binding.layoutCurrentPassword.setError(message);
            }
        });
    }
    private void changePassword(FirebaseUser user,String newPassword){
        authService.changePassword(user, newPassword, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving new password...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}