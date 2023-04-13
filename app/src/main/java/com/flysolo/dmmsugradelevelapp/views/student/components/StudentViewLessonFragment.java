package com.flysolo.dmmsugradelevelapp.views.student.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentStudentViewLessonBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.QuizType;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentActivityAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentContentAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentLessonTabAdapter;
import com.flysolo.dmmsugradelevelapp.views.dialogs.StudentViewActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StudentViewLessonFragment extends Fragment implements StudentActivityAdapter.StudentActivityClickListener {


    private static final String CLASSROOM = "classroom";
    private static final String LESSON = "lesson";

    private Quiz[] quizzes;
    private Lesson lesson;
    private FragmentStudentViewLessonBinding binding;
    private StudentContentAdapter contentAdapter;
    private StudentActivityAdapter activityAdapter;
    public StudentViewLessonFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lesson = StudentViewLessonFragmentArgs.fromBundle(getArguments()).getLesson();
            quizzes = StudentViewLessonFragmentArgs.fromBundle(getArguments()).getQuiz();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentViewLessonBinding.inflate(inflater,container,false);
        binding.recyclerviewContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.recyclerviewActivities.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayLesson(lesson);
        binding.buttonLessons.setOnClickListener(view1 -> {
            NavDirections directions = StudentViewLessonFragmentDirections.actionStudentViewLessonFragmentToStudentLessonContent(lesson);
            Navigation.findNavController(view).navigate(directions);
        });
        binding.buttonActivities.setOnClickListener(view1 -> {
            NavDirections directions = StudentViewLessonFragmentDirections.actionStudentViewLessonFragmentToStudentActivities(quizzes);
            Navigation.findNavController(view).navigate(directions);
        });
    }
    private void displayLesson(Lesson lesson) {
        binding.textActivityCount.setText(String.valueOf(quizzes.length));
        binding.textTitle.setText(lesson.getTitle());
        binding.textDesc.setText(lesson.getDescription());
        contentAdapter = new StudentContentAdapter(binding.getRoot().getContext(), lesson.getContents());
        activityAdapter = new StudentActivityAdapter(binding.getRoot().getContext(), Arrays.asList(quizzes),StudentViewLessonFragment.this);
        binding.recyclerviewContent.setAdapter(contentAdapter);
        binding.recyclerviewActivities.setAdapter(activityAdapter);
    }

    @Override
    public void onActivityClicked(Quiz quiz) {
        NavDirections directions = StudentViewLessonFragmentDirections.actionStudentViewLessonFragmentToStudentViewActivity(quiz);
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }

}