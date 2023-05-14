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
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentAddQuestionBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.QuizType;
import com.flysolo.dmmsugradelevelapp.services.activity.ActivityServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Locale;


public class AddQuestionFragment extends DialogFragment {
    private FragmentAddQuestionBinding binding;
    private ArrayList<String> choices;
    private static final String ARG_ACTIVITY_ID= "activityID";

    private String activityID;

    private ActivityServiceImpl activityService;
    private LoadingDialog loadingDialog;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageURI = null;
    public static AddQuestionFragment newInstance(String activityID) {
        AddQuestionFragment fragment = new AddQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY_ID, activityID);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityID = getArguments().getString(ARG_ACTIVITY_ID);
            activityService = new ActivityServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        }
        setStyle(STYLE_NORMAL,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddQuestionBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            try {
                if (intent != null) {
                    imageURI = intent.getData();
                    binding.imageAttachment.setImageURI(imageURI);
                }
            } catch (Exception e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonAddImage.setOnClickListener(view1 -> {
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
        binding.buttonBack.setOnClickListener(view14 -> dismiss());
        binding.buttonSaveQuestion.setOnClickListener(view13 -> {
            String question = binding.inputQuestion.getText().toString();
            String answer = binding.inputAnswer.getText().toString();
            String points = binding.inputPoints.getText().toString();

            if (question.isEmpty()) {
                binding.inputQuestion.setError("This field is required!");
            } else if (answer.isEmpty()) {
                binding.inputAnswer.setError("this field is required");
            } else if (points.isEmpty()) {
                binding.inputPoints.setError("this field is required");
            }
            else {
                Question q = new Question("",question,"",answer.toLowerCase(Locale.ROOT),choices,Integer.parseInt(points));
                if (imageURI != null) {
                    uploadAttachment(imageURI,q);
                } else {
                    saveQuestion(q);
                }
            }
        });
    }
    private void uploadAttachment(Uri uri, Question question) {
        activityService.uploadAttachment(uri.getLastPathSegment(),
                Constants.getFilename(requireActivity().getContentResolver(),
                        uri), uri, new UiState<String>() {
                    @Override
                    public void Loading() {
                        loadingDialog.showLoadingDialog("Uploading attachment.....");
                    }
                    @Override
                    public void Successful(String data) {
                        loadingDialog.stopLoading();
                        question.setImage(data);
                        saveQuestion(question);
                    }

                    @Override
                    public void Failed(String message) {
                        loadingDialog.stopLoading();
                        Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveQuestion(Question question) {
        activityService.addQuestion(activityID, question, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("saving question....");
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


}