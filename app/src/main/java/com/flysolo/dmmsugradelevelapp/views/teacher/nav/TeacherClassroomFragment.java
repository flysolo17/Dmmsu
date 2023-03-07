package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavArgs;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentTeacherClassroomBinding;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.teacher.TeacherMainActivity;
import com.flysolo.dmmsugradelevelapp.views.teacher.adapters.LessonAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

public class TeacherClassroomFragment extends Fragment implements LessonAdapter.LessonClickListener {
    private FragmentTeacherClassroomBinding binding;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    private LessonAdapter lessonAdapter;
    private static int firstVisibleInListview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTeacherClassroomBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TeacherMainActivity activity = (TeacherMainActivity) getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        binding.recyclerViewLessons.setLayoutManager(layoutManager);
        //binding.recyclerViewLessons.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));
        String classroomID = TeacherClassroomFragmentArgs.fromBundle(getArguments()).getClassroomID();
        lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
        binding.fabCreateActivity.setOnClickListener(view1 -> {
            NavDirections directions = TeacherClassroomFragmentDirections.actionTeacherClassroomFragmentToCreateLessonFragment(classroomID);
            Navigation.findNavController(view).navigate(directions);
        });
        getAllLesson();

        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();
        binding.recyclerViewLessons.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 20 && binding.fabCreateActivity.isExtended()) {
                    binding.fabCreateActivity.shrink();
                }
                if (dy < -20 && !binding.fabCreateActivity.isExtended()) {
                    binding.fabCreateActivity.extend();
                }
                if (!binding.recyclerViewLessons.canScrollVertically(-1)) {
                    binding.fabCreateActivity.extend();
                }
            }
        });
    }
    private void getAllLesson(){
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
                lessonAdapter = new LessonAdapter(binding.getRoot().getContext(),data,TeacherClassroomFragment.this);
                binding.recyclerViewLessons.setAdapter(lessonAdapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onLessonClick(Lesson lesson) {
        NavDirections directions = TeacherClassroomFragmentDirections.actionTeacherClassroomFragmentToTeacherLessonFragment(lesson);
        Navigation.findNavController(requireView()).navigate(directions);
    }

}