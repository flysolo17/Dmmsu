package com.flysolo.dmmsugradelevelapp.views.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentUpdateQuestionDIalogBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class UpdateQuestionDIalog extends DialogFragment {

    private static final String ARG_CLASSROOM_ID = "classroomID";
    private static final String ARG_QUESTION = "question";
    private String classroomID;
    private Question question;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    private FragmentUpdateQuestionDIalogBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageURI = null;
    private ArrayList<String> choices = new ArrayList<>();
    public static UpdateQuestionDIalog newInstance(String classroomID, Question question) {
        UpdateQuestionDIalog fragment = new UpdateQuestionDIalog();
        Bundle args = new Bundle();
        args.putString(ARG_CLASSROOM_ID, classroomID);
        args.putParcelable(ARG_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_CLASSROOM_ID);
            question = getArguments().getParcelable(ARG_QUESTION);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateQuestionDIalogBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (question != null) {
            display(question);
        }
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
            String textQusetion = binding.inputQuestion.getText().toString();
            String description = binding.inputDesc.getText().toString();
            String answer = binding.inputAnswer.getText().toString();
            String points = binding.inputPoints.getText().toString();
            if (textQusetion.isEmpty()) {
                binding.inputQuestion.setError("This field is required!");
            } else if (answer.isEmpty()) {
                binding.inputAnswer.setError("this field is required");
            } else if (points.isEmpty()) {
                binding.inputPoints.setError("this field is required");
            }
            else {
                question.setQuestion(textQusetion);
                question.setDescription(description);
                question.setAnswer(answer);
                question.setPoints(Integer.parseInt(points));
                question.setChoices(choices);
                if (imageURI != null) {
                    uploadAttachment(imageURI,question);
                } else {
                    updateQuestion(question);
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
    private void updateQuestion(Question question) {
        lessonService.updateQuestion(question.getId(), question, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Updating Question....");
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
                        updateQuestion(question);
                    }

                    @Override
                    public void Failed(String message) {
                        loadingDialog.stopLoading();
                        Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void display(Question question) {
        binding.inputQuestion.setText(question.getQuestion());
        binding.inputDesc.setText(question.getQuestion());
        binding.inputPoints.setText(String.valueOf(question.getPoints()));
        binding.inputAnswer.setText(question.getAnswer());
        if(!question.getImage().isEmpty()) {
            Glide.with(binding.getRoot().getContext()).load(question.getImage()).into(binding.imageAttachment);
        }
        for (String choice : question.getChoices()) {
            addChoice(choice);
        }
    }
}