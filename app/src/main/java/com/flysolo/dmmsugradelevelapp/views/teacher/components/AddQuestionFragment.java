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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentAddQuestionBinding;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.viewmodels.QuestionViewModel;
import com.flysolo.dmmsugradelevelapp.views.dialogs.ChooseClassDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class AddQuestionFragment extends DialogFragment {
    private FragmentAddQuestionBinding binding;
    private ArrayList<String> choices;
    private static final String ARG_ACTIVITY_ID= "activityID";
    private static final String ARG_CLASSROOM_ID = "classroomID";
    private String activityID;
    private String classroomID;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageURI = null;
    public static AddQuestionFragment newInstance(String activityID,String classroomID) {
        AddQuestionFragment fragment = new AddQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY_ID, activityID);
        args.putString(ARG_CLASSROOM_ID, classroomID);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityID = getArguments().getString(ARG_ACTIVITY_ID);
            classroomID = getArguments().getString(ARG_CLASSROOM_ID);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
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
        choices = new ArrayList<>();
        binding.buttonAddChoice.setOnClickListener(view1 -> {
            if (binding.cardAddChoice.getVisibility() == View.VISIBLE){
                binding.cardAddChoice.setVisibility(View.GONE);
            } else {
                binding.cardAddChoice.setVisibility(View.VISIBLE);
            }
        });
        binding.buttonBack.setOnClickListener(view14 -> dismiss());
        binding.buttonSaveChoice.setOnClickListener(view12 -> {
            String choice = binding.inputChoice.getText().toString();
            if (choice.isEmpty()) {
                binding.inputChoice.setError("This field is required");
            } else {
                if (choices.contains(choice)) {
                    Toast.makeText(view.getContext(), choice + " already added!", Toast.LENGTH_SHORT).show();
                } else {
                    addChoice(choice);
                }
            }
        });
        binding.buttonSaveQuestion.setOnClickListener(view13 -> {
            String question = binding.inputQuestion.getText().toString();
            String description = binding.inputDesc.getText().toString();
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
                Question q = new Question("",activityID,question,description,"",answer,choices,Integer.parseInt(points),System.currentTimeMillis());
                if (imageURI != null) {
                    uploadAttachment(imageURI,q);
                } else {
                    saveQuestion(q);
                }
            }
        });
    }

    private void addChoice(String choice) {
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.layout_choice,null,false);
        TextView textChoice = view.findViewById(R.id.textChoice);
        textChoice.setText(choice);
        ImageButton buttonDeleteChoice = view.findViewById(R.id.buttonDeleteChoice);
        buttonDeleteChoice.setOnClickListener(view1 ->{
            choices.remove(choice);
            binding.layoutChoices.removeView(view);
                }
        );
        choices.add(choice);
        binding.layoutChoices.addView(view);
        binding.cardAddChoice.setVisibility(View.GONE);
        binding.inputChoice.setText("");
    }
    public void saveQuestion(Question question) {
        lessonService.addQuestion(question, new UiState<String>() {
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
    private void uploadAttachment(Uri uri, Question question) {
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

}