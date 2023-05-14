package com.flysolo.dmmsugradelevelapp.views.student.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentViewRespondBinding;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.views.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
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
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
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