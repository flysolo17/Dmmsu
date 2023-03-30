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

import java.util.List;


public class StudentActivities extends Fragment implements StudentActivityAdapter.StudentActivityClickListener {


    private static final String ARG_PARAM1 = "classroom";
    private static final String LESSON_ID = "lesson";

    private Classroom classroom;
    private Lesson lesson;

    private FragmentStudentActivitiesBinding binding;
    private LoadingDialog loadingDialog;
    private LessonServiceImpl lessonService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroom = getArguments().getParcelable(ARG_PARAM1);
            lesson = getArguments().getParcelable(LESSON_ID);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroom.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentActivitiesBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        binding.recyclerviewActivities.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (lesson.getId() != null) {
            getActivities(lesson.getId());
        }

    }
    private void getActivities(String lessonID) {
        lessonService.getAllActivity(lessonID, new UiState<List<Quiz>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all activities");
            }

            @Override
            public void Successful(List<Quiz> data) {
                loadingDialog.stopLoading();
                StudentActivityAdapter activityAdapter = new StudentActivityAdapter(binding.getRoot().getContext(),data,classroom.getId(),StudentActivities.this);
                binding.recyclerviewActivities.setAdapter(activityAdapter);
                if (data.isEmpty()) {
                    Toast.makeText(binding.getRoot().getContext(), "No Activities yet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), "No Activities yet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityClicked(Quiz quiz) {
        NavDirections  directions = StudentViewLessonFragmentDirections.actionStudentViewLessonFragmentToStartActivity(classroom.getId(),lesson.getId(),quiz);
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }

    @Override
    public void viewScore(Quiz quiz, Respond respond) {
        NavDirections  directions = StudentViewLessonFragmentDirections.actionStudentViewLessonFragmentToViewRespond(quiz,respond, classroom.getId());
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }
}