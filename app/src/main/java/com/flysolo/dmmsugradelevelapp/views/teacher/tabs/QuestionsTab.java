package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentQuestionsTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.QuizType;
import com.flysolo.dmmsugradelevelapp.services.activity.ActivityServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.TeacherQuestionAdapter;
import com.flysolo.dmmsugradelevelapp.views.dialogs.UpdateQuestionDIalog;
import com.flysolo.dmmsugradelevelapp.views.teacher.components.AddMultipleChoiceQuestion;
import com.flysolo.dmmsugradelevelapp.views.teacher.components.AddQuestionFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class QuestionsTab extends Fragment implements TeacherQuestionAdapter.QuestionClickListener {

    private static final String ARG_CLASSROOM_ID = "classroomID";
    private static final String ARG_ACTIVITY_ID = "activityID";
    private String classroomID;
    private Quiz quiz;
    private TeacherQuestionAdapter questionAdapter;
    private ActivityServiceImpl activityService;
    private LoadingDialog loadingDialog;
    private FragmentQuestionsTabBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_CLASSROOM_ID);
            quiz = getArguments().getParcelable(ARG_ACTIVITY_ID);
            activityService = new ActivityServiceImpl(FirebaseFirestore.getInstance(),FirebaseStorage.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuestionsTabBinding.inflate(inflater,container,false);
        loadingDialog= new LoadingDialog(binding.getRoot().getContext());
        binding.recyclerviewQuestions.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (quiz != null)  {
            getQuestion(quiz.getId());
        }
        binding.buttonAddQuestion.setOnClickListener(view1 -> {
            DialogFragment fragment = new DialogFragment();
            if (!fragment.isAdded()) {
                if (quiz.getQuizType() == QuizType.IMAGE_MULTIPLE_CHOICE) {
                    fragment = AddMultipleChoiceQuestion.newInstance(quiz.getId());
                } else {
                    fragment = AddQuestionFragment.newInstance(quiz.getId());
                }
                fragment.show(getChildFragmentManager(),"Add Question");
            }


        });
    }
    @Override
    public void onEdit(int position) {
        if (quiz.getQuizType() != QuizType.IMAGE_MULTIPLE_CHOICE) {
            UpdateQuestionDIalog dIalog = UpdateQuestionDIalog.newInstance(position,quiz);
            if (!dIalog.isAdded()) {
                dIalog.show(getChildFragmentManager(),"Update question");
            }
        }
    }
    private void getQuestion(String quizID) {
        activityService.getQuestion(quizID, new UiState<Quiz>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting questions...");
            }

            @Override
            public void Successful(Quiz data) {
                loadingDialog.stopLoading();
                quiz = data;
                if (quiz != null) {
                    binding.textQuestionCount.setText(data.getQuestions().size()+ "");
                    questionAdapter = new TeacherQuestionAdapter(binding.getRoot().getContext(),data.getQuestions(),QuestionsTab.this);
                    binding.recyclerviewQuestions.setAdapter(questionAdapter);
                }
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}