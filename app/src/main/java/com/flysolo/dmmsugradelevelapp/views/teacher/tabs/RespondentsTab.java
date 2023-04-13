package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentRespondentsTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.model.Scores;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.RespondentsAdapter;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.ViewActivityFragmentDirections;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RespondentsTab extends Fragment {


    private static final String ARG_CLASSROOM_ID = "classroomID";
    private static final String ARG_ACTIVITY_ID = "activityID";

    private String classroomID;
    private Quiz quiz;
    private List<String> students;
    private FragmentRespondentsTabBinding binding;
    private LeaderBoardServiceImpl leaderBoardService;
    private LoadingDialog loadingDialog;
    private ClassroomServiceImpl classroomService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_CLASSROOM_ID);
            quiz = getArguments().getParcelable(ARG_ACTIVITY_ID);
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
        classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(),FirebaseStorage.getInstance());
        if (quiz != null) {
            getClassroom(quiz.getId(),classroomID);
        }
    }
    private void getClassroom(String activityID,String classroomID) {
        classroomService.getClassroom(classroomID, new UiState<Classroom>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting classroom...");
            }
            @Override
            public void Successful(Classroom data) {
                loadingDialog.stopLoading();
                getResponses(activityID,data.getStudents());
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
            }
        });
    }

    private void getResponses(String quizID,List<String> students) {
        leaderBoardService.getRespondents(quizID, new UiState<List<Respond>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all responses....");
            }
            @Override
            public void Successful(List<Respond> data) {
                loadingDialog.stopLoading();
                RespondentsAdapter respondentsAdapter = new RespondentsAdapter(binding.getRoot().getContext(),students,data);
                binding.recyclerviewResponses.setAdapter(respondentsAdapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}