package com.flysolo.dmmsugradelevelapp.views.student.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentStudentLessonContentBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;

import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;

import com.flysolo.dmmsugradelevelapp.views.adapters.ContentAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentContentAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentLessonAdapter;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import java.util.List;


public class StudentLessonContent extends Fragment {

    private Lesson lesson;
    private FragmentStudentLessonContentBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lesson = StudentLessonContentArgs.fromBundle(getArguments()).getLesson();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStudentLessonContentBinding.inflate(inflater,container,false);
        binding.recyclerviewContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.textTitle.setText(lesson.getTitle());
        binding.textDesc.setText(lesson.getDescription());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StudentContentAdapter contentAdapter  = new StudentContentAdapter(view.getContext(),lesson.getContents());
        binding.recyclerviewContent.setAdapter(contentAdapter);
    }

}