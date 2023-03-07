package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.TeacherHomeNavBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.teacher.adapters.ClassroomAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class TeacherHomeNav extends Fragment implements ClassroomAdapter.ClassroomClickListener {
    private TeacherHomeNavBinding binding;
    private ClassroomServiceImpl classroomService;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private LoadingDialog loadingDialog;
    private ArrayList<Classroom> classrooms;
    private ClassroomAdapter classroomAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = TeacherHomeNavBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        classroomService = new ClassroomServiceImpl(firestore, FirebaseStorage.getInstance());
        binding.recyclerviewClasses.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false));
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getAllClassroom(user.getUid());
        }
        binding.buttonCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_createClassroomFragment);
            }
        });
    }

    private void getAllClassroom(String uid) {
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        classrooms = new ArrayList<>();
        classroomService.getAllClassrooms(uid, new UiState<List<Classroom>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all classrooms");
            }

            @Override
            public void Successful(List<Classroom> data) {
                loadingDialog.stopLoading();
                classrooms.addAll(data);
                classroomAdapter = new ClassroomAdapter(binding.getRoot().getContext(),classrooms,TeacherHomeNav.this);
                binding.recyclerviewClasses.setAdapter(classroomAdapter);
                if (classrooms.size() == 0) {
                    binding.textNoClass.setVisibility(View.VISIBLE);
                } else {
                    binding.textNoClass.setVisibility(View.GONE);
                }
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClassroomClick(int position) {
        TeacherHomeNavDirections.ActionNavigationHomeToTeacherClassroomFragment directions = TeacherHomeNavDirections.actionNavigationHomeToTeacherClassroomFragment(classrooms.get(position).getId());
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }

    @Override
    public void onStartClassroom(int position) {
        Toast.makeText(binding.getRoot().getContext(), "Class starting", Toast.LENGTH_SHORT).show();
    }
}