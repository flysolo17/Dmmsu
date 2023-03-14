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

import com.flysolo.dmmsugradelevelapp.databinding.FragmentActivityTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ActivityAdapter;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentArgs;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class ActivityTab extends Fragment {
    private Classroom classroom;
    private FragmentActivityTabBinding binding;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    private ActivityAdapter activityAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          classroom = TeacherClassroomFragmentArgs.fromBundle(getArguments()).getClassroom();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActivityTabBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(), classroom.getId());
        binding.recyclerviewActivities.setLayoutManager(new LinearLayoutManager(view.getContext()));
        getAllActivities();
    }
    private void getAllActivities() {
        lessonService.getAllActivity(new UiState<ArrayList<Quiz>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("getting activities....");
            }

            @Override
            public void Successful(ArrayList<Quiz> data) {
                loadingDialog.stopLoading();
                if (data.isEmpty()) {
                    Toast.makeText(binding.getRoot().getContext(), "no activities yet!", Toast.LENGTH_SHORT).show();
                }
                activityAdapter = new ActivityAdapter(binding.getRoot().getContext(),data);
                binding.recyclerviewActivities.setAdapter(activityAdapter);
            }

            @Override
            public void Failed(String message) {
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}