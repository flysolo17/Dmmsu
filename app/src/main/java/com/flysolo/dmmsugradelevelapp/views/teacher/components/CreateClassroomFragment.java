package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentCreateClassroomBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class CreateClassroomFragment extends Fragment {



    private FragmentCreateClassroomBinding binding;
    private LoadingDialog loadingDialog;
    private ClassroomServiceImpl classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
    private String startTime ="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateClassroomBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(view.getContext());
        binding.buttonStartTime.setOnClickListener(view13 -> {
            MaterialTimePicker builder= new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(0)
                    .setMinute(0)
                    .setPositiveButtonText("OK")
                    .setNegativeButtonText("Cancel")
                    .setTitleText("Pick start time")
                    .build();
            builder.show(getChildFragmentManager(),"Start Time");
            builder.addOnPositiveButtonClickListener(view1 -> {
                startTime = getTime(builder.getHour(),builder.getMinute());
                binding.buttonStartTime.setText(startTime);
            });
        });


        binding.buttonCreateClass.setOnClickListener(view12 -> {
            String name = binding.inputName.getText().toString();
            if (name.isEmpty()) {
                binding.layoutName.setError("This field is required");
            } else if (startTime.isEmpty()) {
                Toast.makeText(view.getContext(), "Please add start time", Toast.LENGTH_SHORT).show();
            } else if (getCheck().isEmpty()) {
                Toast.makeText(view.getContext(), "Select day", Toast.LENGTH_SHORT).show();
            } else  {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Classroom classroom = new Classroom(
                            "",
                            user.getUid(),
                            name,
                            false,
                            "",
                            startTime,
                            getCheck(),
                            new ArrayList<>(),
                            new ArrayList<>()
                            ,System.currentTimeMillis());
                    saveClassroom(classroom);
                }
            }
        });
    }
    private void saveClassroom(Classroom classroom) {
        classroomService.createClassroom(classroom, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Saving classroom....");
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
            }
        });
    }
    public List<String> getCheck() {
        List<String> daylist = new ArrayList<>();
        if (binding.checkMonday.isChecked()) {
            daylist.add("Monday");
        }
        if (binding.checkTuesday.isChecked()) {
            daylist.add("Tuesday");
        }
        if (binding.checkWednesday.isChecked()) {
            daylist.add("Wednesday");
        }
        if (binding.checkThursday.isChecked()) {
            daylist.add("Thursday");
        }
        if (binding.checkFriday.isChecked()) {
            daylist.add("Friday");
        }
        if (binding.checkSaturday.isChecked()) {
            daylist.add("Saturday");
        }
        return daylist;
    }
    private String getTime(int hour ,int minute) {
        String hr = String.valueOf(hour);
        String mn = String.valueOf(minute);
        if (hour == 0) {
            hr = "12";
        }
        if (hr.length() == 1) {
            hr = "0" + hr;
        }
        if (mn.length() == 1) {
            mn = "0" + mn;
        }
        return hr +":" + mn;
    }
}