package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentTeacherLessonBinding;
import com.flysolo.dmmsugradelevelapp.model.Lesson;

public class TeacherLessonFragment extends Fragment {

    private Lesson lesson;
    private FragmentTeacherLessonBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lesson = TeacherLessonFragmentArgs.fromBundle(getArguments()).getLesson();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTeacherLessonBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textLesson.setText(lesson.getTitle());
        binding.textDesc.setText(lesson.getDescription());
    }
}