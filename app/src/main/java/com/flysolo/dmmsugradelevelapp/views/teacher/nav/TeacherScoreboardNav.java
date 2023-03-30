package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.TeacherScoreboardNavBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ClassroomAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class TeacherScoreboardNav extends Fragment {
    private TeacherScoreboardNavBinding binding;
    private LoadingDialog loadingDialog;
    private FirebaseUser user;
    private ClassroomServiceImpl classroomService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.teacher_scoreboard_nav, container, false);
    }
    private void getAllClassroom(String uid) {
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        classroomService.getAllClassrooms(uid, new UiState<List<Classroom>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all classrooms");
            }
            @Override
            public void Successful(List<Classroom> data) {
                loadingDialog.stopLoading();

            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}