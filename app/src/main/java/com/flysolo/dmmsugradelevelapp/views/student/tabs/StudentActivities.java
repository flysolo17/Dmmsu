package com.flysolo.dmmsugradelevelapp.views.student.tabs;

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
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStudentActivitiesBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentActivityAdapter;
import com.flysolo.dmmsugradelevelapp.views.student.components.StudentViewLessonFragmentDirections;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Arrays;
import java.util.List;


public class StudentActivities extends Fragment implements StudentActivityAdapter.StudentActivityClickListener {


    private static final String ARG_PARAM1 = "classroom";

    private Quiz[] quizzes;

    private FragmentStudentActivitiesBinding binding;
    private StudentActivityAdapter activityAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizzes = StudentActivitiesArgs.fromBundle(getArguments()).getActivities();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentActivitiesBinding.inflate(inflater,container,false);
        binding.recyclerviewActivities.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activityAdapter = new StudentActivityAdapter(view.getContext(), Arrays.asList(quizzes), this);
        binding.recyclerviewActivities.setAdapter(activityAdapter);
    }


    @Override
    public void onActivityClicked(Quiz quiz) {
        NavDirections directions = StudentActivitiesDirections.actionStudentActivitiesToStudentViewActivity(quiz);
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }

}