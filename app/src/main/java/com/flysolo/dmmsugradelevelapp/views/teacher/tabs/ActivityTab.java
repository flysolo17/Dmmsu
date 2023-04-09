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
import com.flysolo.dmmsugradelevelapp.services.activity.ActivityServiceImpl;
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
    private static final String ARG_PARAM2 = "lesson";
    private ActivityAdapter activityAdapter;
    private String classroomID;
    private Lesson lesson;
    private LoadingDialog loadingDialog;
    private List<Quiz> activities;
    private ActivityServiceImpl activityService;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            if (swipeDir == ItemTouchHelper.LEFT) {
                deleteActivity(viewHolder.getBindingAdapterPosition());
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_PARAM1);
            lesson = getArguments().getParcelable(ARG_PARAM2);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
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
        activityService = new ActivityServiceImpl(FirebaseFirestore.getInstance(),FirebaseStorage.getInstance());
        activityAdapter = new ActivityAdapter(activityService.getAllActivity(lesson.getId()),view.getContext(),ActivityTab.this);

        binding.recyclerviewActivities.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.recyclerviewActivities.setAdapter(activityAdapter);
        activityAdapter.startListening();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewActivities);
        binding.buttonCreateActivity.setOnClickListener(view1 -> {
            NavDirections directions = ViewLessonTabDirections.actionViewLessonTabToCreateActivityFragment(lesson.getId());
            Navigation.findNavController(view1).navigate(directions);
        });

    }



    @Override
    public void onActivityClicked(Quiz quiz) {
        NavDirections directions = ViewLessonTabDirections.actionViewLessonTabToViewActivityFragment(classroomID,quiz);
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }
    private void deleteActivity(int position) {
        String id = activityAdapter.getSnapshots().get(position).getId();
        activityService.deleteActivity(id, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Deleting activity.....");
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


}