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

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.StudentScoreboardNavBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
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
import java.util.List;


public class StudentScoreboardNav extends Fragment implements ResponsesAdapter.ResponsesClickListener{


    private StudentScoreboardNavBinding binding;
    private LoadingDialog loadingDialog;
    private ClassroomServiceImpl classroomService;
    private LeaderBoardServiceImpl leaderBoardService;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseUser user;
    private ResponsesAdapter responsesAdapter;
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
        classroomService = new ClassroomServiceImpl(firestore,storage);
        leaderBoardService = new LeaderBoardServiceImpl(firestore);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getAllMyClassroom(user.getUid());
        }

        binding.recyclerviewScoreboard.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }
    private void getAllMyClassroom(String uid){
        classroomService.getAllMyClass(uid, new UiState<List<Classroom>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all class...");
            }

            @Override
            public void Successful(List<Classroom> data) {
                loadingDialog.stopLoading();
                if (data.isEmpty()){
                    Toast.makeText(binding.getRoot().getContext(), "no class yet!", Toast.LENGTH_SHORT).show();
                }
                getAllActivities(data,user.getUid());
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAllActivities(List<Classroom> classroomList,String uid) {
        leaderBoardService.getAllResponse(classroomList, uid, new UiState<List<Respond>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting scores....");
            }
            @Override
            public void Successful(List<Respond> data) {
                loadingDialog.stopLoading();
                responsesAdapter = new ResponsesAdapter(binding.getRoot().getContext(),data,StudentScoreboardNav.this);
                binding.recyclerviewScoreboard.setAdapter(responsesAdapter);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResponseClicked(Respond respond,Quiz quiz) {

        NavDirections directions = StudentScoreboardNavDirections.actionNavigationScoreboardToViewScore(respond,quiz);
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }
}