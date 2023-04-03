package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentSettingsTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentArgs;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentDirections;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class SettingsTab extends Fragment {
    private FragmentSettingsTabBinding binding;
    private ClassroomServiceImpl classroomService;
    private Classroom classroom;
    private LoadingDialog loadingDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroom = TeacherClassroomFragmentArgs.fromBundle(getArguments()).getClassroom();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsTabBinding.inflate(inflater,container,false);
        loadingDialog= new LoadingDialog(binding.getRoot().getContext());
        classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getClassroom(classroom.getId());
        binding.buttonDelete.setOnClickListener(view1 -> {
            new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this classroom ?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Continue", (dialogInterface, i) -> deleteClassroom(classroom.getId()))
                    .show();
        });
        binding.buttonEdit.setOnClickListener(view12 -> {
            NavDirections directions = TeacherClassroomFragmentDirections.actionTeacherClassroomFragmentToEditClassroom(classroom);
            Navigation.findNavController(view12).navigate(directions);
        });
        binding.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Share class code");
                intent.putExtra(Intent.EXTRA_TEXT,classroom.getCode());
                startActivity(Intent.createChooser(intent,"Share to"));
            }
        });
    }
    private void deleteClassroom(String classroomID) {
        classroomService.deleteClassroom(classroomID, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Deleting classroom");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayInfo(Classroom classroom) {
        binding.textCreatedAt.setText(Constants.formatDate(classroom.getCreatedAt()));
        binding.textClassroomName.setText(classroom.getName());
        binding.textStudents.setText(String.valueOf(classroom.getStudents().size()));
        binding.textActiveStudents.setText(String.valueOf(classroom.getActiveStudents().size()));
        binding.textTime.setText(classroom.getStartTime());
        binding.textSched.setText(String.join(", " , classroom.getSchedule()));
        String status = classroom.getStatus() ? "OPEN" : "CLOSED";
        int color = classroom.getStatus() ? Color.GREEN : Color.RED;
        binding.textStatus.setText(status);
        binding.textStatus.setTextColor(color);
        if (classroom.getStatus()) {
            binding.textCode.setText(classroom.getCode());
            binding.openDivider.setVisibility(View.VISIBLE);
            binding.layoutCode.setVisibility(View.VISIBLE);

        } else  {
            binding.layoutCode.setVisibility(View.GONE);
            binding.openDivider.setVisibility(View.GONE);
        }
    }
    private void getClassroom(String classroomID ) {
        classroomService.getClassroom(classroomID, new UiState<Classroom>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting classroom.....");
            }

            @Override
            public void Successful(Classroom data) {
                loadingDialog.stopLoading();
                classroom = data;
                displayInfo(classroom);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}