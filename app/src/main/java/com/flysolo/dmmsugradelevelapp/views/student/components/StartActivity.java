package com.flysolo.dmmsugradelevelapp.views.student.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStartActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentQuestionAdapter;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ViewLessonTabArgs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class StartActivity extends Fragment {

    private static final String ARG_PARAM1 = "classroomID";
    private static final String ARG_PARAM2 = "studentID";
    private static final String ARG_PARAM3 = "activity";
    private String classroomID;
    private String lessonID;
    private Quiz quiz;
    private LoadingDialog loadingDialog;
    private LessonServiceImpl lessonService;
    private FragmentStartActivityBinding binding;
    private StudentQuestionAdapter questionAdapter;
    private LeaderBoardServiceImpl leaderBoardService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = StartActivityArgs.fromBundle(getArguments()).getClassroomID();
            lessonID = StartActivityArgs.fromBundle(getArguments()).getLessonID();
            quiz = StartActivityArgs.fromBundle(getArguments()).getActivity();
            leaderBoardService = new LeaderBoardServiceImpl(FirebaseFirestore.getInstance());
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStartActivityBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        binding.recyclerviewQuestions.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.textActivityTitle.setText(quiz.getName());
        binding.textActivityDesc.setText(quiz.getDescription());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getQuestions(quiz.getId());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding.buttonBack.setOnClickListener(view1 -> Navigation.findNavController(view1).popBackStack());
        binding.buttonSubmit.setOnClickListener(view12 -> {
            if (user != null) {
                Respond respond = new Respond("",quiz.getId(),user.getUid(),questionAdapter.getAnswer(),System.currentTimeMillis());
                submitAnswer(respond);
            }

        });
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
                questionAdapter = new StudentQuestionAdapter(binding.getRoot().getContext(),data);
                binding.recyclerviewQuestions.setAdapter(questionAdapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void submitAnswer(Respond respond) {
        leaderBoardService.submitAnswer(classroomID, respond, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Submitting answers....");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}