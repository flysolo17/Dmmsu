package com.flysolo.dmmsugradelevelapp.views.teacher.nav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentViewActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.LessonTabAdapter;
import com.flysolo.dmmsugradelevelapp.views.adapters.QuestionTabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class ViewActivityFragment extends Fragment {
    private static final String ARG_CLASSROOM_ID= "classroomID";
    private static final String ARG_ACTVITY_ID = "activityID";
    private String classroomID;
    private Quiz quiz;

    private FragmentViewActivityBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = ViewActivityFragmentArgs.fromBundle(getArguments()).getClassroomID();
            quiz = ViewActivityFragmentArgs.fromBundle(getArguments()).getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewActivityBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewPager();
    }

    private void setUpViewPager() {
        QuestionTabAdapter adapter = new QuestionTabAdapter(getChildFragmentManager(),getLifecycle(), classroomID,quiz);
        binding.viewpager2.setAdapter(adapter);
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewpager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        binding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tablayout.getTabAt(position).select();
            }
        });
    }

}