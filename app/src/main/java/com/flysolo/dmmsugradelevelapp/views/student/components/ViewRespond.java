package com.flysolo.dmmsugradelevelapp.views.student.components;

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

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentViewRespondBinding;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentQuestionAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentViewQuestionAdapter;
import com.flysolo.dmmsugradelevelapp.views.dialogs.ChooseClassDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class ViewRespond extends Fragment {

    private Quiz quiz;
    private Respond respond;
    private String classroomID;
    private FragmentViewRespondBinding binding;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quiz = ViewRespondArgs.fromBundle(getArguments()).getQuiz();
            respond = ViewRespondArgs.fromBundle(getArguments()).getRespond();
            classroomID = ViewRespondArgs.fromBundle(getArguments()).getClassroomID();
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewRespondBinding.inflate(inflater,container,false);
        binding.textActivityTitle.setText(quiz.getName());
        binding.textActivityDesc.setText(quiz.getDescription());
        binding.textCreatedAt.setText(Constants.formatDate(quiz.getCreatedAt()));
        binding.textRespondDate.setText(Constants.formatDate(respond.getDateAnswered()));
        binding.recyclerviewQuestions.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getQuestions(quiz.getId());
    }
    private void getQuestions(String activityID) {
        lessonService.getQuestionsByID(activityID, new UiState<List<Question>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all questions....");
            }

            @Override
            public void Successful(List<Question> data) {
                loadingDialog.stopLoading();
                StudentViewQuestionAdapter adapter = new StudentViewQuestionAdapter(binding.getRoot().getContext(),data,respond);
                binding.recyclerviewQuestions.setAdapter(adapter);
                binding.textMaxScore.setText(String.valueOf(getMaxScore(data)));
                binding.textMyScore.setText(String.valueOf(checkIfAnswerCorrect(data,respond)));
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private int checkIfAnswerCorrect(List<Question> questions,Respond respond) {
        int score = 0;
        for (Question question: questions) {
            for (Answer answer: respond.getAnswers()) {
                if (question.getId().equals(answer.getQuestionID()) && question.getAnswer().equals(answer.getAnswer())) {
                    score+= question.getPoints();
                }
            }
        }
        return score;
    }
    private int getMaxScore(List<Question> questions){
        int count = 0;
        for (Question question : questions) {
            count += question.getPoints();
        }
        return count;
    }

}