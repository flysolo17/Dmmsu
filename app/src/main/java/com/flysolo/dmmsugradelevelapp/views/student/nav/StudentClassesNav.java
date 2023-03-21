package com.flysolo.dmmsugradelevelapp.views.student.nav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.StudentClassesNavBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentClassroomAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class StudentClassesNav extends Fragment implements StudentClassroomAdapter.StudentClassroomClickListener {
    private StudentClassesNavBinding binding;
    private LoadingDialog loadingDialog;
    private ClassroomServiceImpl classroomService;
    private FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = StudentClassesNavBinding.inflate(inflater,container,false);
        loadingDialog= new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        binding.buttonSearch.setOnClickListener(view1 -> {
            String code = binding.inputSearch.getText().toString();
            searchClassByCode(code);
        });
        binding.recyclerviewStudentClass.setLayoutManager(new LinearLayoutManager(view.getContext()));
        if (user != null) {
            getAllClasses(user.getUid());
        }
    }
    private void getAllClasses(String studentID) {
        classroomService.getAllMyClass(studentID, new UiState<List<Classroom>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all classes");
            }

            @Override
            public void Successful(List<Classroom> data) {
                loadingDialog.stopLoading();
                StudentClassroomAdapter adapter = new StudentClassroomAdapter(binding.getRoot().getContext(),data,studentID,StudentClassesNav.this);
                binding.recyclerviewStudentClass.setAdapter(adapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
            }
        });
    }
    private void searchClassByCode(String code) {
        classroomService.searchClass(code, new UiState<Classroom>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Searching for class " + code);
            }

            @Override
            public void Successful(Classroom data) {
                loadingDialog.stopLoading();
                if (data != null) {
                    if (user != null) {
                        joinCLass(data.getId(),user.getUid());
                    }

                } else {
                    Toast.makeText(binding.getRoot().getContext(), "No classroom found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void joinCLass(String classID,String studentID) {
        classroomService.joinClass(classID, studentID, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Joining class...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                binding.inputSearch.setText("");
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
    public void onJoin(Classroom classroom) {

    }
}