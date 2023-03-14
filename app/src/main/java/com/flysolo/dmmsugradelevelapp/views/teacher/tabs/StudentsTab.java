package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

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
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStudentsTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.AccountsAdapter;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentArgs;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class StudentsTab extends Fragment implements AccountsAdapter.AccountClickListener {
    private Classroom classroom;
    private FragmentStudentsTabBinding binding;
    private LoadingDialog loadingDialog;
    private ClassroomServiceImpl classroomService;
    private AccountsAdapter adapter;
    private ArrayList<Accounts> accountsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroom = TeacherClassroomFragmentArgs.fromBundle(getArguments()).getClassroom();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentsTabBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountsList = new ArrayList<>();
        binding.recyclerviewAccounts.setLayoutManager(new LinearLayoutManager(view.getContext()));
        classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());

        getAllStudents();


    }
    private void getAllStudents() {
        accountsList.clear();
        classroomService.getStudents(new UiState<ArrayList<Accounts>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all students");
            }

            @Override
            public void Successful(ArrayList<Accounts> data) {
                loadingDialog.stopLoading();
                accountsList.add(null);
                accountsList.addAll(getStudent(data));
                accountsList.add(null);
                accountsList.addAll(getNonStudent(data));
                adapter = new AccountsAdapter(binding.getRoot().getContext(),accountsList,classroom,StudentsTab.this);
                binding.recyclerviewAccounts.setAdapter(adapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void addStudent(int position) {
        classroomService.addStudent(classroom.getId(), accountsList.get(position).getId(), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Adding student...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                classroom.getStudents().add(data);
                adapter.notifyItemChanged(position);
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
    public void removeStudent(int position) {
        classroomService.removeStudent(classroom.getId(), accountsList.get(position).getId(), new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Removing student...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                classroom.getStudents().remove(data);
                adapter.notifyItemChanged(position);
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<Accounts> getStudent(ArrayList<Accounts> list) {
        ArrayList<Accounts> arrayList = new ArrayList<>();
        for (Accounts accounts: list) {
            if (accounts != null) {
                if (classroom.getStudents().contains(accounts.getId())) {
                    arrayList.add(accounts);
                }
            }
        }
        return arrayList;
    }
    private ArrayList<Accounts> getNonStudent(ArrayList<Accounts> list){
        ArrayList<Accounts> arrayList = new ArrayList<>();
        for (Accounts accounts: list) {
            if (accounts != null) {
                if (!classroom.getStudents().contains(accounts.getId())) {
                    arrayList.add(accounts);
                }
            }

        }
        return arrayList;
    }
}