package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentEditClassroomBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Days;
import com.flysolo.dmmsugradelevelapp.model.Meridiem;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentArgs;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class EditClassroom extends Fragment {


    private Classroom classroom;
    private LoadingDialog loadingDialog;
    private FragmentEditClassroomBinding binding;
    private ClassroomServiceImpl classroomService;
    private Meridiem meridiem =new Meridiem();
    private Days days = new Days();
    private List<Days> listDays = days.getDays();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroom = TeacherClassroomFragmentArgs.fromBundle(getArguments()).getClassroom();
            classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditClassroomBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        displayClassroomInfo(classroom);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonCancel.setOnClickListener(view12 -> Navigation.findNavController(view12).popBackStack());
        binding.buttonUpdate.setOnClickListener(view1 -> {
            String name = binding.inputName.getText().toString();
            if (name.isEmpty()) {
                binding.inputName.setError("This field is required");
            } else if (getSelectedDays().isEmpty()) {
                Toast.makeText(view1.getContext(), "Select day", Toast.LENGTH_SHORT).show();
            } else  {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String start = String.format(Locale.getDefault(), "%02d", binding.hourPicker.getValue()) + " : " + String.format(Locale.getDefault(), "%02d", binding.minutePicker.getValue()) + " " + meridiem.getNames().get(binding.meridiemPicker.getValue());
                    classroom.setName(name);
                    classroom.setStartTime(start);
                    classroom.setSchedule(getSelectedDays());
                    updateClassroom(classroom);
                }
            }
        });
    }

    private void displayClassroomInfo(Classroom classroom){
        //hour picker
        binding.hourPicker.setMinValue(1);
        String hour = classroom.getStartTime().substring(0,classroom.getStartTime().lastIndexOf(":")).trim();
        binding.hourPicker.setMaxValue( 12);
        binding.hourPicker.setShowDividers(0);
        binding.hourPicker.setValue(Integer.parseInt(hour));
        binding.minutePicker.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));
        binding.inputName.setText(classroom.getName());
        //minute picker
        binding.minutePicker.setMinValue(0);
        binding.minutePicker.setMaxValue(60);
        String minute = classroom.getStartTime().substring(classroom.getStartTime().lastIndexOf(":") + 1,classroom.getStartTime().lastIndexOf(" ")).trim();
        binding.minutePicker.setValue(Integer.parseInt(minute));
        //am/pm picker
        binding.meridiemPicker.setMinValue(0);
        if (classroom.getStartTime().contains("AM")) {
            binding.meridiemPicker.setValue(0);
        } else {
            binding.meridiemPicker.setValue(1);
        }
        binding.meridiemPicker.setMaxValue(meridiem.getMeridiems().size() -1);
        binding.meridiemPicker.setDisplayedValues(meridiem.getNames().toArray(new String[0]));
        //display days
        for (Days days: listDays) {
            addDay(days,classroom);
        }
    }
    private void addDay(Days day,Classroom classroom) {
        if (classroom.getSchedule().contains(day.getDay())) {
            day.setClick(true);
        }
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.row_days,binding.getRoot(),false);
        TextView textName = view.findViewById(R.id.texDayName);
        MaterialCardView buttonDays  = view.findViewById(R.id.buttonDays);
        textName.setText(day.getDay());
        if (day.getClick()) {
            textName.setTextColor(Color.WHITE);
            buttonDays.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
        } else {
            buttonDays.setCardBackgroundColor(Color.WHITE);
        }
        buttonDays.setOnClickListener(view1 -> {
            day.setClick(!day.getClick());
            updateButtonColor(textName,buttonDays, day.getClick());
        });
        binding.layoutDays.addView(view);
    }
    private void updateButtonColor(TextView text, MaterialCardView button  ,Boolean isClick ) {
        if (isClick) {
            text.setTextColor(Color.WHITE);
            button.setCardBackgroundColor(getResources().getColor(R.color.purple_500));
        } else {
            text.setTextColor(Color.BLACK);
            button.setCardBackgroundColor(Color.WHITE);
        }
    }
    private List<String> getSelectedDays(){
        List<String> selected = new ArrayList<>();
        for (Days days: listDays) {
            if (days.getClick()) {
                selected.add(days.getDay());
            }
        }
        return selected;
    }
    private  void updateClassroom(Classroom classroom) {
        classroomService.editClassroom(classroom, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Updating classroom...");
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
 }