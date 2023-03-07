package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
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
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentCreateClassroomBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class CreateClassroomFragment extends Fragment {



    private FragmentCreateClassroomBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageUri;
    private LoadingDialog loadingDialog;
    private ClassroomServiceImpl classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateClassroomBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(view.getContext());
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            try {
                if (intent != null) {
                    imageUri = intent.getData();
                    binding.imageClassBackground.setImageURI(imageUri);
                }
            } catch (Exception e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonAddImage.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
        binding.buttonCreateClass.setOnClickListener(view12 -> {
            String name = binding.inputName.getText().toString();
            if (name.isEmpty()) {
                binding.layoutName.setError("This field is required");
            } else  {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Classroom classroom = new Classroom("",
                            user.getUid(),
                            "",
                            name,
                            false,
                            "",
                            new ArrayList<>(),System.currentTimeMillis());
                    if (imageUri != null) {
                        uploadBackground(user.getUid(),imageUri,classroom);
                    } else {
                        saveClassroom(classroom);
                    }
                }
            }
        });
    }
    private void uploadBackground(String uid , Uri uri, Classroom classroom) {
        classroomService.uploadBackground(uid, uri, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Uploading background....");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                classroom.setBackground(data);
                saveClassroom(classroom);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveClassroom(Classroom classroom) {
        classroomService.createClassroom(classroom, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving classroom....");
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
            }
        });
    }
}