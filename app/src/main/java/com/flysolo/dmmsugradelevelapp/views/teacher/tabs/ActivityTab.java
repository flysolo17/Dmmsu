package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.flysolo.dmmsugradelevelapp.databinding.FragmentActivityTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ActivityAdapter;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentArgs;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class ActivityTab extends Fragment implements ActivityAdapter.ActivityClickListener {

    private FragmentActivityTabBinding binding;
    private LessonServiceImpl lessonService;
    private static final String ARG_PARAM1 = "classroomID";
    private static final String ARG_PARAM2 = "lessonID";
    private ActivityAdapter activityAdapter;
    private String lessonID,classroomID;
    private LoadingDialog loadingDialog;
    private List<Quiz> activities;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            if (swipeDir == ItemTouchHelper.LEFT) {
                deleteActivity(activities.get(viewHolder.getAdapterPosition()),viewHolder.getAdapterPosition());
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_PARAM1);
            lessonID = getArguments().getString(ARG_PARAM2);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
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
        activities = new ArrayList<>();
        binding.recyclerviewActivities.setLayoutManager(new LinearLayoutManager(view.getContext()));
        getAllActivities();
        binding.buttonCreateActivity.setOnClickListener(view1 -> {
           binding.layoutInputs.setVisibility(View.VISIBLE);
           binding.buttonCreateActivity.setVisibility(View.GONE);
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewActivities);
        binding.buttonCancel.setOnClickListener(view12 -> {
            binding.inputTitle.setText("");
            binding.inputDesc.setText("");
            binding.layoutInputs.setVisibility(View.GONE);
            binding.buttonCreateActivity.setVisibility(View.VISIBLE);
        });
        binding.buttonSave.setOnClickListener(view13 -> {
            String title = binding.inputTitle.getText().toString();
            String desc = binding.inputDesc.getText().toString();
            if (title.isEmpty()) {
                binding.inputTitle.setError("This field is required");
            } else {
                binding.inputTitle.setText("");
                binding.inputDesc.setText("");
                if (lessonID != null) {
                    Quiz quiz = new Quiz("",lessonID,title,desc,System.currentTimeMillis());
                    createActivity(quiz);
                }

            }
            binding.layoutInputs.setVisibility(View.GONE);
            binding.buttonCreateActivity.setVisibility(View.VISIBLE);
        });
    }
    private void getAllActivities() {
        activities.clear();
        lessonService.getAllActivity(lessonID,new UiState<List<Quiz>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("getting activities....");
            }

            @Override
            public void Successful(List<Quiz> data) {
                loadingDialog.stopLoading();
                activities.addAll(data);
                if (data.isEmpty()) {
                    Toast.makeText(binding.getRoot().getContext(), "no activities yet!", Toast.LENGTH_SHORT).show();
                }
                activityAdapter = new ActivityAdapter(binding.getRoot().getContext(),activities,ActivityTab.this);
                binding.recyclerviewActivities.setAdapter(activityAdapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createActivity(Quiz quiz) {
        lessonService.createActivity(quiz, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Creating new activity...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityClicked(Quiz quiz) {
        NavDirections directions = ViewLessonTabDirections.actionViewLessonTabToViewActivityFragment(classroomID,quiz);
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }
    private void deleteActivity(Quiz quiz, int position) {
        lessonService.deleteActivity(quiz.getId(), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("deleting " + quiz.getName() + "....");
            }
            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                activityAdapter.notifyItemRemoved(position);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}