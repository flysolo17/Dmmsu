package com.flysolo.dmmsugradelevelapp.views.student.nav;

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

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.StudentScoreboardNavBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.model.Scores;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ResponsesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class StudentScoreboardNav extends Fragment {


    private StudentScoreboardNavBinding binding;
    private LoadingDialog loadingDialog;
    private LeaderBoardServiceImpl leaderBoardService;
    private AuthServiceImpl authService;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ResponsesAdapter responsesAdapter;
    private List<Scores> scoresList = new ArrayList<>();;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = StudentScoreboardNavBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authService = new AuthServiceImpl(FirebaseAuth.getInstance(),firestore,storage);
        leaderBoardService = new LeaderBoardServiceImpl(firestore);
        binding.recyclerviewScoreboard.setLayoutManager(new LinearLayoutManager(view.getContext()));
        getAllScore();
    }
    private void getAllStudents(List<Respond> respondList) {
        authService.getAllStudentAccount(new UiState<List<Accounts>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all students");
            }
            @Override
            public void Successful(List<Accounts> data) {
                loadingDialog.stopLoading();
                scoresList.clear();
                for (int i = 0; i < data.size() ; i++) {
                    scoresList.add(new Scores(data.get(i).getId(),data.get(i).getProfile(),data.get(i).getName(),getMyResponses(data.get(i).getId(),respondList)));
                }
                Collections.sort(scoresList, new Comparator<Scores>() {
                    @Override
                    public int compare(Scores lhs, Scores rhs) {
                        return Integer.compare( rhs.getStudentScore(),lhs.getStudentScore());
                    }
                    @Override
                    public Comparator<Scores> reversed() {
                        return Comparator.super.reversed();
                    }
                });
                if (scoresList.size() > 3) {
                    display(scoresList.get(0),scoresList.get(1),scoresList.get(2));

//                    scoresList.remove(0);
//                    scoresList.remove(1);
//                    scoresList.remove(2);
                }
                for (int i = 0; i < 3; i++) {
                    scoresList.remove(i);
                }
                responsesAdapter = new ResponsesAdapter(binding.getRoot().getContext(),scoresList);
                binding.recyclerviewScoreboard.setAdapter(responsesAdapter);

            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
            }
        });
    }
    private void getAllScore() {
        leaderBoardService.getAllResponse(new UiState<List<Respond>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all scores..");
            }
            @Override
            public void Successful(List<Respond> data) {
                loadingDialog.stopLoading();
                getAllStudents(data);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private int getMyResponses(String uid,List<Respond> respondList){
        int count = 0;
        for (Respond respond: respondList) {
            if (respond.getStudentID().equals(uid)) {
                count += respond.getTotal();
            }
        }
        return count;
    }
    private void display(Scores score1,Scores score2 ,Scores score3) {
        binding.textFullname1.setText(score1.getStudentName());
        binding.textFullname2.setText(score2.getStudentName());
        binding.textFullname3.setText(score3.getStudentName());
        binding.textPoints1.setText(score1.getStudentScore() + " QP");
        binding.textPoints2.setText(score2.getStudentScore() + " QP");
        binding.textPoints3.setText(score3.getStudentScore() + " QP");
        if (!score1.getStudentProfile().isEmpty()) {
            Glide.with(binding.getRoot().getContext()).load(score1.getStudentProfile()).into(binding.image1);
        }
        if (!score2.getStudentProfile().isEmpty()) {
            Glide.with(binding.getRoot().getContext()).load(score2.getStudentProfile()).into(binding.image2);
        }
        if (!score3.getStudentProfile().isEmpty()) {
            Glide.with(binding.getRoot().getContext()).load(score3.getStudentProfile()).into(binding.image3);
        }
    }

}