package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentLessonTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.LessonAdapter;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentDirections;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class LessonTab extends Fragment implements LessonAdapter.LessonClickListener{
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    private LessonAdapter lessonAdapter;
    private Classroom classroom;
    private FragmentLessonTabBinding binding;
    private ArrayList<Lesson> lessons;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroom = getArguments().getParcelable("classroom");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLessonTabBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        binding.recyclerviewLessons.setLayoutManager(layoutManager);
        lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(), classroom.getId());
        getAllLesson();
        binding.recyclerviewLessons.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20 && binding.fabCreateLesson.isExtended()) {
                    binding.fabCreateLesson.shrink();
                }
                if (dy < -20 && !binding.fabCreateLesson.isExtended()) {
                    binding.fabCreateLesson.extend();

                }
            }
        });
        binding.fabCreateLesson.setOnClickListener(view1 -> {
            NavDirections directions = TeacherClassroomFragmentDirections.actionTeacherClassroomFragmentToCreateLessonFragment(classroom.getId());
            Navigation.findNavController(view1).navigate(directions);
        });
    }
    private void getAllLesson(){
        lessons = new ArrayList<>();
        lessonService.getAllLesson(new UiState<ArrayList<Lesson>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all lessons");
            }
            @Override
            public void Successful(ArrayList<Lesson> data) {
                loadingDialog.stopLoading();
                if (data.isEmpty()) {
                    Toast.makeText(binding.getRoot().getContext(), "No lessons yet!", Toast.LENGTH_SHORT).show();
                }
                lessons.addAll(data);
                lessonAdapter = new LessonAdapter(binding.getRoot().getContext(),lessons, LessonTab.this);
                binding.recyclerviewLessons.setAdapter(lessonAdapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onCreateActivity(Lesson lesson) {
        NavDirections directions = TeacherClassroomFragmentDirections.actionTeacherClassroomFragmentToCreateActivityFragment(classroom.getId(),lesson.getId());
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }

    @Override
    public void onDeleteLesson(Lesson lesson,int position) {
        lessonService.deleteLesson(lesson.getId(), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("deleting " + lesson.getTitle() + "....");
            }
            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                lessons.remove(lesson);
                lessonAdapter.notifyItemRemoved(position);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewLesson(Lesson lesson) {

    }
}