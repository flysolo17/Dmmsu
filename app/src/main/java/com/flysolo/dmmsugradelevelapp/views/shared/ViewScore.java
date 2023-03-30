package com.flysolo.dmmsugradelevelapp.views.shared;

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
import com.flysolo.dmmsugradelevelapp.databinding.FragmentViewScoreBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardService;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentViewQuestionAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.concurrent.Executors;


public class ViewScore extends Fragment {

    Respond respond;
    Quiz quiz;
    private FragmentViewScoreBinding binding;
    private LoadingDialog loadingDialog;
    private LeaderBoardServiceImpl leaderBoardService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            respond = ViewScoreArgs.fromBundle(getArguments()).getRespond();
            quiz = ViewScoreArgs.fromBundle(getArguments()).getQuiz();
            leaderBoardService = new LeaderBoardServiceImpl(FirebaseFirestore.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewScoreBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        binding.recyclerviewResponses.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMyInfo(respond.getStudentID());
        binding.textActivityTitle.setText(quiz.getName());
        binding.textActivityDesc.setText(quiz.getDescription());
        binding.textCreatedAt.setText(Constants.formatDate(quiz.getCreatedAt()));
        binding.textRespondDate.setText(Constants.formatDate(respond.getDateAnswered()));
        getQuestions(respond.getClassroomID(),quiz.getId());
    }
    private void getQuestions(String classroomID,String activityID) {
        leaderBoardService.getQuestions(classroomID, activityID, new UiState<List<Question>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting questions...");
            }

            @Override
            public void Successful(List<Question> data) {
                loadingDialog.stopLoading();
                binding.textMaxScore.setText(String.valueOf(Constants.getMaxScore(data)));
                binding.textMyScore.setText(String.valueOf(Constants.getMyScore(data,respond)));
                StudentViewQuestionAdapter adapter = new StudentViewQuestionAdapter(binding.getRoot().getContext(), data,respond);
                binding.recyclerviewResponses.setAdapter(adapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getMyInfo(String studentID) {
        leaderBoardService.getStudentInfo(studentID, new UiState<Accounts>() {
            @Override
            public void Loading() {
                binding.textStudentName.setText("Loading....");
            }

            @Override
            public void Successful(Accounts data) {
                binding.textStudentName.setText(data.getName());
            }

            @Override
            public void Failed(String message) {
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}