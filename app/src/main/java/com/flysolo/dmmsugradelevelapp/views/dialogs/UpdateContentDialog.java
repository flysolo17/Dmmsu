package com.flysolo.dmmsugradelevelapp.views.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.DialogUpdateContentBinding;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class UpdateContentDialog extends DialogFragment {


    private static final String ARG_LESSON = "lesson";

    private static final String ARG_POSITION = "position";
    private Lesson  lesson;
    private int position;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageURI;
    private DialogUpdateContentBinding binding;
    public UpdateContentDialog() {
        // Required empty public constructor
    }

    public static UpdateContentDialog newInstance( int position,Lesson lesson) {
        UpdateContentDialog fragment = new UpdateContentDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LESSON,lesson);
        args.putInt(ARG_POSITION ,position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
            lesson = getArguments().getParcelable(ARG_LESSON);
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DialogUpdateContentBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (lesson.getContents().get(position) != null) {
            displayContent(lesson.getContents().get(position));
        }
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            try {
                if (intent != null) {
                    imageURI = intent.getData();
                    binding.imageContent.setImageURI(imageURI);
                }
            } catch (Exception e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonUpdateContent.setOnClickListener(view1 -> {
            String name = binding.inputTitle.getText().toString();
            String desc = binding.inputDesc.getText().toString();
            if (name.isEmpty()) {
                binding.inputTitle.setError("This field is required!");
            } else {
                lesson.getContents().get(position).setTitle(name);
                lesson.getContents().get(position).setDescription(desc);
                if (imageURI != null) {
                    uploadAttachment(imageURI,lesson.getContents().get(position));
                } else {
                    updateContent(lesson.getId(), lesson.getContents());
                }
            }
        });
        binding.buttonAddImage.setOnClickListener(view1 -> {
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);

        });
    }
    private void uploadAttachment(Uri uri,Content content) {
        lessonService.uploadAttachment(uri.getLastPathSegment(),
                Constants.getFilename(requireActivity().getContentResolver(),
                        uri), uri, new UiState<String>() {
                    @Override
                    public void Loading() {
                        loadingDialog.showLoadingDialog("Uploading attachment.....");
                    }
                    @Override
                    public void Successful(String data) {
                        loadingDialog.stopLoading();
                        content.setAttachment(data);
                        updateContent(lesson.getId(),lesson.getContents());
                    }

                    @Override
                    public void Failed(String message) {
                        loadingDialog.stopLoading();
                        Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateContent(String lessonID,List<Content> contents) {
        lessonService.updateContent(lessonID,contents, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Updating content...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayContent(Content content) {
        binding.inputTitle.setText(content.getTitle());
        binding.inputDesc.setText(content.getDescription());
        if (!content.getAttachment().isEmpty()) {
            Glide.with(binding.getRoot().getContext()).load(content.getAttachment()).into(binding.imageContent);
        }
    }
}