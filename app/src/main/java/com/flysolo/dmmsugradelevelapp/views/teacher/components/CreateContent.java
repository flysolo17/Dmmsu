package com.flysolo.dmmsugradelevelapp.views.teacher.components;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentCreateContentBinding;
import com.flysolo.dmmsugradelevelapp.databinding.ViewLessonTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ViewLessonTabArgs;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;


public class CreateContent extends Fragment {


    private ActivityResultLauncher<Intent> attachmentPicker;
    private Uri attachmentURI = null;
    private FragmentCreateContentBinding binding;
    private String lessonID;
    private String classroomID;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = CreateContentArgs.fromBundle(
                    getArguments()
            ).getClassroomID();
            lessonID = CreateContentArgs.fromBundle(getArguments())
                    .getLessonID();
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateContentBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachmentPicker =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            try {
                if (intent != null) {
                    attachmentURI = intent.getData();
                    addFile(Constants.getFilename(requireActivity().getContentResolver(),attachmentURI),attachmentURI.getLastPathSegment());
                }
            } catch (Exception e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonAttachFile.setOnClickListener(view1 -> {
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            attachmentPicker.launch(intent);
        });
        binding.buttonSaveContent.setOnClickListener(view12 -> {
            String name = binding.inputTitle.getText().toString();
            String desc = binding.inputDesc.getText().toString();
            if (name.isEmpty()) {
                binding.inputTitle.setError("This field is required!");
            } else {
                Content content = new Content("",lessonID,name,desc,"",System.currentTimeMillis());
                if (attachmentURI != null) {
                    uploadAttachment(attachmentURI,content);
                } else {
                    saveContent(content);
                }
            }
        });

    }
    private void addFile(String name,String type) {
        binding.layoutFile.removeAllViews();
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.layout_file,binding.layoutFile,false);
        TextView textFilename = view.findViewById(R.id.textFilename);
        TextView textFileSize = view.findViewById(R.id.textFileSize);
        ImageView imageFile = view.findViewById(R.id.imageFileType);
        imageFile.setImageResource(Constants.identifyFile(type));
        textFilename.setText(name);
        textFileSize.setText(type);
        binding.layoutFile.addView(view,0);
    }

    private void saveContent(Content content) {
        lessonService.addContent(content, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving content");
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
    private void uploadAttachment(Uri uri,Content content) {
        lessonService.uploadAttachment(attachmentURI.getLastPathSegment(),
                Constants.getFilename(requireActivity().getContentResolver(),
                        attachmentURI), uri, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Uploading attachment.....");
            }
            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                content.setAttachment(data);
                saveContent(content);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}