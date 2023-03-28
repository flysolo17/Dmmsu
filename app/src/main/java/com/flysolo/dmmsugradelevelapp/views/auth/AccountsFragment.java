package com.flysolo.dmmsugradelevelapp.views.auth;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentAccountsBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.UserType;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class AccountsFragment extends Fragment {
    private FragmentAccountsBinding binding;
    private FirebaseUser user;
    private AuthServiceImpl authService;
    private LoadingDialog loadingDialog;
    private Accounts accounts = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountsBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        authService =new AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        binding.buttonLogout.setOnClickListener(view1 -> {
            new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout? ")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        FirebaseAuth.getInstance().signOut();
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();


        });
        if (user != null) {
            getAccount(user.getUid());
        }
        binding.buttonEditProfile.setOnClickListener(view12 -> {
            if (accounts != null) {
                NavDirections directions = AccountsFragmentDirections.actionNavigationAccountToEditProfileFragment(accounts);
                Navigation.findNavController(view).navigate(directions);
            }
        });
        binding.buttonChangePassword.setOnClickListener(view12 -> {
            if (accounts != null) {
                NavDirections directions = AccountsFragmentDirections.actionNavigationAccountToChangePassword(accounts);
                Navigation.findNavController(view).navigate(directions);
            }
        });
    }

    private void getAccount(String uid) {
        authService.getAccount(uid, new UiState<Accounts>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting account...");
            }

            @Override
            public void Successful(Accounts data) {
                loadingDialog.stopLoading();
                accounts = data;
                displayInfo(data);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayInfo(Accounts accounts) {
        if (!accounts.getProfile().isEmpty()) {

            Glide.with(binding.getRoot().getContext()).load(accounts.getProfile()).into(binding.acountProfile);
        }
        binding.textFullname.setText(accounts.getName());
        binding.textAccountType.setText(accounts.getType().toString());
    }
}