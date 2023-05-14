package com.flysolo.dmmsugradelevelapp.views.student.components;

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

import com.flysolo.dmmsugradelevelapp.databinding.FragmentStudentLessonBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.views.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentLessonAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class StudentLessonFragment extends Fragment implements StudentLessonAdapter.StudentLessonClickListener {

    private static final String ARG_PARAM1 = "classroom";
    private Classroom classroom;
    private LoadingDialog loadingDialog;
    private FragmentStudentLessonBinding binding;
    private LessonServiceImpl lessonService;
    private AuthServiceImpl authService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroom = getArguments().getParcelable(ARG_PARAM1);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
            authService = new AuthServiceImpl(FirebaseAuth.getInstance(),FirebaseFirestore.getInstance(),FirebaseStorage.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentLessonBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        binding.recyclerviewLessons.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        binding.textClassroomName.setText(classroom.getName());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAllLessons();
    }
    private void getAllLessons() {
        lessonService.getAllLesson(classroom.getId(),new UiState<List<Lesson>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all lessons...");
            }

            @Override
            public void Successful(List<Lesson> data) {
                loadingDialog.stopLoading();
                StudentLessonAdapter studentLessonAdapter = new StudentLessonAdapter(binding.getRoot().getContext(), data, StudentLessonFragment.this);
                binding.recyclerviewLessons.setAdapter(studentLessonAdapter);
                if (data.isEmpty()) {
                    Toast.makeText(binding.getRoot().getContext(), "no lessons yet!", Toast.LENGTH_SHORT).show();
                }
                getTeacherInfo(classroom.getTeacherID());


            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTeacherInfo(String uid) {
        authService.getAccount(uid, new UiState<Accounts>() {
            @Override
            public void Loading() {
                binding.textTeacherName.setText("Loading....");
            }

            @Override
            public void Successful(Accounts data) {
                binding.textTeacherName.setText(data.getName());
            }

            @Override
            public void Failed(String message) {
                binding.textTeacherName.setText("Error");
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onViewLesson(Lesson lesson,List<Quiz> quizzes) {
        NavDirections directions = StudentLessonFragmentDirections.actionStudentLessonFragmentToStudentViewLessonFragment(lesson,quizzes.toArray(new Quiz[0]));
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }
}