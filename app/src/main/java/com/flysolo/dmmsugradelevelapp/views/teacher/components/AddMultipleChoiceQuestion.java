package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentAddMultipleChoiceQuestionBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.services.activity.ActivityServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddMultipleChoiceQuestion extends DialogFragment {


    private String activityID;
    private static final String ARG_ACTIVITY_ID= "activityID";
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageURI = null;
    private int selected = 0;
    private Uri[] uriList = new Uri[4];
    private ActivityServiceImpl activityService;
    private FragmentAddMultipleChoiceQuestionBinding binding;
    private LoadingDialog loadingDialog;
    public static AddMultipleChoiceQuestion newInstance(String activityID) {
        AddMultipleChoiceQuestion fragment = new AddMultipleChoiceQuestion();
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY_ID, activityID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            activityID = getArguments().getString(ARG_ACTIVITY_ID);
            activityService = new ActivityServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddMultipleChoiceQuestionBinding.inflate(inflater,container,false);
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
                    imageURI = intent.getData();
                    switch (selected) {
                        case 0:
                            binding.imageCorrect.setImageURI(intent.getData());
                            uriList[0] = intent.getData();
                            return;
                        case 1:
                            binding.imageIncorrect1.setImageURI(intent.getData());
                            uriList[1] = intent.getData();
                            return;
                        case 2:
                            binding.imageIncorrect2.setImageURI(intent.getData());
                            uriList[2] = intent.getData();
                            return;
                        case 3:
                            binding.imageIncorrect3.setImageURI(intent.getData());
                            uriList[3] = intent.getData();
                            return;
                        default:
                            binding.imageCorrect.setImageURI(intent.getData());
                            uriList[0] = intent.getData();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.imageCorrect.setOnClickListener(view1 -> {
            selected = 0;
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);

        });
        binding.imageIncorrect1.setOnClickListener(view12 -> {
            selected = 1;
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
        binding.imageIncorrect2.setOnClickListener(view12 -> {
            selected = 2;
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
        binding.imageIncorrect3.setOnClickListener(view12 -> {
            selected = 3;
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra("image",3);
            galleryLauncher.launch(intent);
        });
        binding.buttonSaveQuestion.setOnClickListener(view13 -> {
            String question = binding.inputQuestion.getText().toString();
            String points = binding.inputPoints.getText().toString();
            Question q = new Question("",question,"","", new ArrayList<>(),Integer.parseInt(points));
            if (uriList.length != 0) {
                if (activityID != null) {
                    uploadImages(q,activityID);
                } else {
                    Toast.makeText(view.getContext(), "No activity", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    private void uploadImages(Question question,String activityID) {
    activityService.uploadMultipleImages(requireActivity(), activityID, uriList, new UiState<List<String>>() {
        @Override
        public void Loading() {
            loadingDialog.showLoadingDialog("Uploading images...");
        }

        @Override
        public void Successful(List<String> data) {
            loadingDialog.stopLoading();
            question.setChoices(data);
            question.setAnswer(data.get(0));
            addQuestion(question);
        }
        @Override
        public void Failed(String message) {
            loadingDialog.stopLoading();
            Toast.makeText(binding.getRoot().getContext(),message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addQuestion(Question question) {
        activityService.addQuestion(activityID, question, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving Question");

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