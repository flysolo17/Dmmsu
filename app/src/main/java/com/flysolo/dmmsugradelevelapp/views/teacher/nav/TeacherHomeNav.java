package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.TeacherHomeNavBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.services.auth.AuthServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ActivityAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.ClassroomAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class TeacherHomeNav extends Fragment implements ClassroomAdapter.ClassroomClickListener {
    private TeacherHomeNavBinding binding;
    private ClassroomServiceImpl classroomService;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private LoadingDialog loadingDialog;
    private ArrayList<Classroom> classrooms;
    private ClassroomAdapter classroomAdapter;
    private AuthServiceImpl authService;
    // implement the TextWatcher callback listener
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void afterTextChanged(Editable editable) {
            if (editable != null) {
                if (!editable.toString().isEmpty()) {
                    classroomAdapter = new ClassroomAdapter(binding.getRoot().getContext(),classrooms.stream().filter(classroom -> classroom.getName().contains(editable.toString())).collect(Collectors.toList()),TeacherHomeNav.this);
                    binding.recyclerviewClasses.setAdapter(classroomAdapter);
                } else {

                }
            }
        }

    };
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
        classrooms = new ArrayList<>();
        authService = new AuthServiceImpl(FirebaseAuth.getInstance(),FirebaseFirestore.getInstance(),FirebaseStorage.getInstance());
        classroomService = new ClassroomServiceImpl(firestore, FirebaseStorage.getInstance());
        binding.recyclerviewClasses.setLayoutManager(new LinearLayoutManager(view.getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getAllClassroom(user.getUid());
        }
        binding.buttonCreateClass.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_navigation_home_to_createClassroomFragment));
        binding.edtSearch.addTextChangedListener(textWatcher);
    }

    private void getAllClassroom(String uid) {
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        classroomService.getAllClassrooms(uid, new UiState<List<Classroom>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all classrooms");
            }
            @Override
            public void Successful(List<Classroom> data) {
                classrooms.clear();
                loadingDialog.stopLoading();
                classrooms.addAll(data);
                classroomAdapter = new ClassroomAdapter(binding.getRoot().getContext(),classrooms,TeacherHomeNav.this);
                binding.recyclerviewClasses.setAdapter(classroomAdapter);
                if (classrooms.size() == 0) {
                    binding.textNoClass.setVisibility(View.VISIBLE);
                } else {
                    binding.textNoClass.setVisibility(View.GONE);
                    //getAllActivities(data);
                }
                displayInfo(uid);

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
        TeacherHomeNavDirections.ActionNavigationHomeToTeacherClassroomFragment directions = TeacherHomeNavDirections.actionNavigationHomeToTeacherClassroomFragment(classrooms.get(position));
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }

    @Override
    public void onStartClassroom(int position) {
        Toast.makeText(binding.getRoot().getContext(), "Class starting", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startClass(int position) {
        Classroom classroom = classrooms.get(position);
        classroomService.startClass(classroom.getId(), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Loading...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                classroomAdapter.notifyItemChanged(position);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void endClass(int position) {
        Classroom classroom = classrooms.get(position);
        classroomService.endClass(classroom.getId(), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Loading...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                classroomAdapter.notifyItemChanged(position);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void shareCode(int position) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Share class code");
        intent.putExtra(Intent.EXTRA_TEXT,classrooms.get(position).getCode());
        startActivity(Intent.createChooser(intent,"Share to"));
    }
    private void displayInfo(String uid) {
        authService.getAccount(uid, new UiState<Accounts>() {
            @Override
            public void Loading() {
                binding.textFullname.setText("Loading....");
                binding.textType.setText("Loading..");
            }

            @Override
            public void Successful(Accounts data) {
                if (!data.getProfile().isEmpty()) {
                    Glide.with(binding.getRoot().getContext()).load(data.getProfile()).into(binding.imageProfile);
                }
                binding.textFullname.setText(data.getName());
                binding.textType.setText(data.getType().toString());
            }

            @Override
            public void Failed(String message) {
                binding.textFullname.setText("No name");
                binding.textType.setText("user not found!");
            }
        });
    }

}