package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentCreateLessonBinding;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class CreateLessonFragment extends Fragment {
    private FragmentCreateLessonBinding binding;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateLessonBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String classroomID = CreateLessonFragmentArgs.fromBundle(getArguments()).getClassroomID();
        lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        binding.buttonSave.setOnClickListener(view1 -> {
            String title = binding.inputTitle.getText().toString();
            String desc = binding.inputDesc.getText().toString();
            if (title.isEmpty()) {
                binding.layoutTitle.setError("This field is required");
            } else  if (desc.isEmpty()) {
                binding.layoutDesc.setError("This field is required");
            } else {
                Lesson lesson = new Lesson("",classroomID,title,desc,new ArrayList<>(),System.currentTimeMillis());
                saveLesson(lesson);
            }
        });
    }
    private void saveLesson(Lesson lesson) {
        lessonService.createLesson(lesson, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving lesson....");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}