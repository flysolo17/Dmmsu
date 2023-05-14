package com.flysolo.dmmsugradelevelapp.views.dialogs;

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

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentUpdateQuestionDIalogBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.services.activity.ActivityServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class UpdateQuestionDIalog extends DialogFragment {

    private static final String ARG_POSITION= "position";
    private static final String ARG_QUIZ = "quiz";
    private int position;
    private Quiz quiz;
    private ActivityServiceImpl activityService;
    private LoadingDialog loadingDialog;
    private FragmentUpdateQuestionDIalogBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageURI = null;
    public static UpdateQuestionDIalog newInstance(int position, Quiz quiz) {
        UpdateQuestionDIalog fragment = new UpdateQuestionDIalog();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putParcelable(ARG_QUIZ ,quiz);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            quiz = getArguments().getParcelable(ARG_QUIZ);
            position = getArguments().getInt(ARG_POSITION);
            activityService = new ActivityServiceImpl(FirebaseFirestore.getInstance(),FirebaseStorage.getInstance());
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
        if (quiz.getQuestions().get(position) != null) {
            display(quiz.getQuestions().get(position));
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

        binding.buttonBack.setOnClickListener(view14 -> dismiss());
        binding.buttonSaveQuestion.setOnClickListener(view13 -> {
            String textQusetion = binding.inputQuestion.getText().toString();
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
                quiz.getQuestions().get(position).setQuestion(textQusetion);
                quiz.getQuestions().get(position).setAnswer(answer);
                quiz.getQuestions().get(position).setPoints(Integer.parseInt(points));
                if (imageURI != null) {
                    uploadAttachment(imageURI,quiz.getQuestions().get(position));
                } else {
                    updateQuestion(quiz.getQuestions());
                }
            }
        });
        binding.buttonDelete.setOnClickListener(view12 -> new MaterialAlertDialogBuilder(view12.getContext())
                .setMessage("Are you sure you want to delete this question?")
                .setTitle("Delete Question")
                .setPositiveButton("Delete", (dialogInterface, i) -> {
                    quiz.getQuestions().remove(position);
                    updateQuestion(quiz.getQuestions());
                    dismiss();
                }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).show());
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
                        updateQuestion(quiz.getQuestions());
                    }

                    @Override
                    public void Failed(String message) {
                        loadingDialog.stopLoading();
                        Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
private void deleteQuestion(List<Question> questions) {
    activityService.updateQuestion(quiz.getId(), questions, new UiState<String>() {
        @Override
        public void Loading() {
            loadingDialog.showLoadingDialog("Deleting question....");
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
    private void updateQuestion(List<Question>  questions) {
        activityService.updateQuestion(quiz.getId(), questions, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Updating question....");
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
    private void display(Question question) {
        binding.inputQuestion.setText(question.getQuestion());
        binding.inputPoints.setText(String.valueOf(question.getPoints()));
        binding.inputAnswer.setText(question.getAnswer());
        if(!question.getImage().isEmpty()) {
            Glide.with(binding.getRoot().getContext()).load(question.getImage()).into(binding.imageAttachment);
        }
    }
}