package com.flysolo.dmmsugradelevelapp.views.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentChooseClassDialogBinding;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentTeacherClassroomBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentClassroomAdapter;
import com.flysolo.dmmsugradelevelapp.views.auth.LoginActivity;
import com.flysolo.dmmsugradelevelapp.views.student.StudentMainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.C;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseClassDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseClassDialog extends DialogFragment implements StudentClassroomAdapter.StudentClassroomClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID= "UID";


    private String uid;
    private FragmentChooseClassDialogBinding binding;
    private ClassroomServiceImpl classroomService;
    private LoadingDialog loadingDialog;
    private StudentClassroomAdapter classroomAdapter;
    public static ChooseClassDialog newInstance(String uid) {
        ChooseClassDialog fragment = new ChooseClassDialog();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
        }
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentChooseClassDialogBinding.inflate(inflater,container,false);
        classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllClassroom();
        binding.buttonBack.setOnClickListener(view1 -> {
            dismiss();
            startActivity(new Intent(requireActivity(), StudentMainActivity.class));
        });
    }
    private void getAllClassroom() {
        classroomService.getAllClass(new UiState<List<Classroom>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("getting all classrooms");
            }

            @Override
            public void Successful(List<Classroom> data) {
                loadingDialog.stopLoading();
                classroomAdapter = new StudentClassroomAdapter(binding.getRoot().getContext(),data,uid,ChooseClassDialog.this);
                binding.recyclerviewClasses.setAdapter(classroomAdapter);
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
        View view = getLayoutInflater().inflate(R.layout.dialog_join_class,binding.getRoot(),false);
        TextInputEditText inputCID = view.findViewById(R.id.inputCID);

        new MaterialAlertDialogBuilder(binding.getRoot().getContext())
                .setView(view)
                .setTitle("Join Classroom")
                .setPositiveButton("Continue", (dialogInterface, i) -> {
                    String cid = inputCID.getText().toString();
                    if (uid != null) {
                        if (classroom.getId().equals(cid)) {
                            addStudent(cid,uid);
                        } else {
                            Toast.makeText(binding.getRoot().getContext(), "Invalid cid", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }
    private void addStudent(String classroomID , String studentID) {
        classroomService.addStudent(classroomID.trim(), studentID, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Joining class...");
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
