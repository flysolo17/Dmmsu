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
import com.flysolo.dmmsugradelevelapp.databinding.FragmentCreateClassroomBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Days;
import com.flysolo.dmmsugradelevelapp.model.Meridiem;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CreateClassroomFragment extends Fragment {

    private FragmentCreateClassroomBinding binding;
    private LoadingDialog loadingDialog;
    private ClassroomServiceImpl classroomService = new ClassroomServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
    private Meridiem meridiem =new Meridiem();
    private Days days = new Days();
    private List<Days> listDays = days.getDays();
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
        binding.buttonCreateClass.setOnClickListener(view12 -> {
            String name = binding.inputName.getText().toString();
            if (name.isEmpty()) {
                binding.inputName.setError("This field is required");
            } else if (getSelectedDays().isEmpty()) {
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
                            String.format(Locale.getDefault(), "%02d", binding.hourPicker.getValue()) + " : " + String.format(Locale.getDefault(), "%02d", binding.minutePicker.getValue()) + " " + meridiem.getNames().get(binding.meridiemPicker.getValue()),
                            getSelectedDays(),
                            new ArrayList<>(),
                            new ArrayList<>()
                            ,System.currentTimeMillis());
                    saveClassroom(classroom);
                }
            }
        });
        initViews();
    }
    void initViews(){


        //hour picker
        binding.hourPicker.setMinValue(1);
        binding.hourPicker.setMaxValue( 12);
        binding.hourPicker.setShowDividers(0);
        binding.minutePicker.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));

        //minute picker
        binding.minutePicker.setMinValue(0);
        binding.minutePicker.setMaxValue(60);

        //am/pm picker
        binding.meridiemPicker.setMinValue(0);
        binding.meridiemPicker.setMaxValue(meridiem.getMeridiems().size() -1);
        binding.meridiemPicker.setDisplayedValues(meridiem.getNames().toArray(new String[0]));

        //display days
        for (Days days: listDays) {
            addDay(days);
        }
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
    private void addDay(Days day) {
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.row_days,binding.getRoot(),false);
        TextView textName = view.findViewById(R.id.texDayName);
        MaterialCardView buttonDays  = view.findViewById(R.id.buttonDays);
        textName.setText(day.getDay());
        if (day.getClick()) {
            buttonDays.setCardBackgroundColor(Color.parseColor("#FAFAFF"));
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
}