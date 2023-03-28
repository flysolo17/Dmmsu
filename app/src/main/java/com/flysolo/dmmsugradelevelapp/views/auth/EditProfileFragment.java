package com.flysolo.dmmsugradelevelapp.views.auth;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentEditProfileBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.PrimitiveIterator;


public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;
    private LoadingDialog loadingDialog;
    private Accounts accounts = null;
    private Uri imageUri = null;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private AuthServiceImpl authService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accounts = EditProfileFragmentArgs.fromBundle(
                    getArguments()
            ).getAccount();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authService = new AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        if (accounts!= null) {

            displayInfo(accounts);
        }
        galleryLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            try {
                if (intent != null) {
                    imageUri = intent.getData();
                    binding.accountProfile.setImageURI(imageUri);
                }
            } catch (Exception e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonAddImage.setOnClickListener(view1 -> {
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);

        });
        binding.buttonSave.setOnClickListener(view12 -> {
            String name = binding.inputFullname.getText().toString();
            if (name.isEmpty()) {
                binding.layoutFullname.setError("This field is required");
            } else {
                if (accounts != null) {
                    if (imageUri != null) uploadProfile(accounts.getId(), imageUri, name);
                    else saveAccount(accounts.getId(), name, accounts.getProfile());
                }
            }
        });
    }

    private void displayInfo(Accounts accounts) {

        if(!accounts.getProfile().isEmpty()) {

            Glide.with(binding.getRoot().getContext()).load(accounts.getProfile()).into(binding.accountProfile);
        }
        binding.inputFullname.setText(accounts.getName());
    }
    private void uploadProfile(String uid,Uri uri,String name) {
        authService.uploadProfile(uid,uri, Constants.getFileExtension(requireActivity(),uri), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Uploading profile....");
            }
            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                saveAccount(uid,name,data);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveAccount(String uid,String name ,String profile) {
        authService.updateProfile(uid, name, profile, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving info....");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
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