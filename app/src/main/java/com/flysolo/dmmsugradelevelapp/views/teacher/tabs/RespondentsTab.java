package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentRespondentsTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.RespondentsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class RespondentsTab extends Fragment {


    private static final String ARG_CLASSROOM_ID = "classroomID";
    private static final String ARG_ACTIVITY_ID = "activityID";

    private String classroomID;
    private String activityID;
    private List<String> students;
    private FragmentRespondentsTabBinding binding;
    private LeaderBoardServiceImpl leaderBoardService;
    private LoadingDialog loadingDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_CLASSROOM_ID);
            activityID = getArguments().getString(ARG_ACTIVITY_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRespondentsTabBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext()) ;
        binding.recyclerviewResponses.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leaderBoardService = new LeaderBoardServiceImpl(FirebaseFirestore.getInstance());
        getResponses(activityID);
    }
    private void getResponses(String activityID) {
        leaderBoardService.getRespondents(classroomID, activityID, new UiState<List<Respond>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting Respondents...");
            }

            @Override
            public void Successful(List<Respond> data) {
                loadingDialog.stopLoading();
               getQuestions(data);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getQuestions(List<Respond> respondList) {
        leaderBoardService.getQuestions(classroomID, activityID, new UiState<List<Question>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting Questions...");
            }

            @Override
            public void Successful(List<Question> data) {
                loadingDialog.stopLoading();
                RespondentsAdapter adapter = new RespondentsAdapter(binding.getRoot().getContext(),respondList,data);
                binding.recyclerviewResponses.setAdapter(adapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}