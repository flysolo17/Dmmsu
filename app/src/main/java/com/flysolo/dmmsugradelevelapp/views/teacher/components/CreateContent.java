package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentCreateContentBinding;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.views.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class CreateContent extends DialogFragment {

    private static String ARG_CLASSROOM = "classroomID";
    private static String ARG_LESSON_ID = "lessonID";
    private ActivityResultLauncher<Intent> attachmentPicker;
    private Uri attachmentURI = null;
    private FragmentCreateContentBinding binding;
    private String lessonID;
    private String classroomID;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    public static CreateContent newInstance(String classroomID,String lessonID) {
        CreateContent fragment = new CreateContent();
        Bundle args = new Bundle();
        args.putString(ARG_CLASSROOM, classroomID);
        args.putString(ARG_LESSON_ID, lessonID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_CLASSROOM);
            lessonID = getArguments().getString(ARG_LESSON_ID);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCreateContentBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonBack.setOnClickListener(view13 -> dismiss());
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
                Content content = new Content(name,desc,"",System.currentTimeMillis());
                if (attachmentURI != null) {
                    uploadAttachment(lessonID,attachmentURI,content);
                } else {
                    saveContent(lessonID,content);
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

    private void saveContent(String lessonID,Content content) {
        lessonService.addContent(lessonID,content, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving content");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                dismiss();

            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadAttachment(String lessonID,Uri uri,Content content) {
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
                saveContent(lessonID,content);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}