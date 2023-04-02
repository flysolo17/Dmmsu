package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

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
import com.flysolo.dmmsugradelevelapp.databinding.TeacherScoreboardNavBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ClassroomAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.TeacherScoreboardAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class TeacherScoreboardNav extends Fragment {
    private TeacherScoreboardNavBinding binding;
    private LoadingDialog loadingDialog;
    private FirebaseUser user;
    private ClassroomServiceImpl classroomService;
    private LeaderBoardServiceImpl leaderBoardService;
    private List<String> students = new ArrayList<>();
    private List<Respond> responds = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        leaderBoardService = new LeaderBoardServiceImpl(FirebaseFirestore.getInstance());
        classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = TeacherScoreboardNavBinding.inflate(inflater,container,false);
        binding.recyclerviewScoreboard.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (user!= null) {
            getAllClassroom(user.getUid());
        }
    }

    private void getAllClassroom(String uid) {
        students.clear();
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        classroomService.getAllClassrooms(uid, new UiState<List<Classroom>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all classrooms");
            }
            @Override
            public void Successful(List<Classroom> data) {
                loadingDialog.stopLoading();
                for (Classroom classroom:data) {
                    for (String student: classroom.getStudents()) {
                        if (!students.contains(student)) {
                            students.add(student);
                        }
                    }
                }
                getAllResponses(data);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAllResponses(List<Classroom> classroomList) {
        responds.clear();
        leaderBoardService.getTeacherResponses(classroomList, new UiState<List<Respond>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all students");
            }

            @Override
            public void Successful(List<Respond> data) {
                loadingDialog.stopLoading();
                responds.addAll(data);
                TeacherScoreboardAdapter adapter = new TeacherScoreboardAdapter(binding.getRoot().getContext(),students,classroomList,data);
                binding.recyclerviewScoreboard.setAdapter(adapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}